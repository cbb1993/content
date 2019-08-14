package com.zyn.lib.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.kevin.wraprecyclerview.WrapRecyclerView;
import com.zyn.lib.R;
import com.zyn.lib.adapter.CommonAdapter;
import com.zyn.lib.adapter.ViewHolder;
import com.zyn.lib.api.ApiResponse;
import com.zyn.lib.constant.CommonConstants;
import com.zyn.lib.api.ApiUtils;
import com.zyn.lib.util.GsonUtils;
import com.zyn.lib.view.recycleview.FooterLayout;
import com.zyn.lib.view.recycleview.HeaderLayout;
import com.zyn.lib.view.recycleview.NRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public abstract class RecycleFrag<T> extends BaseFrag implements com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener<WrapRecyclerView>,
        com.zyn.lib.view.recycleview.OnItemClickListener<T>, com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener
{
    protected final int HTTP_TYPE_REFRESH = 1033;
    protected final int HTTP_TYPE_LOAD = 1034;

    private List<T> mData;
    private int mCurrentPage = 1, mCountPage = CommonConstants.DEFAULT_PAGE_SIZE;

    private NRecyclerView mRecyclerView;
    private WrapRecyclerView mWrapRecyclerView;
    private FooterLayout mFooterLayout;
    private CommonAdapter mAdapter;

    private Map<String, String> mHttpData = new HashMap<>();
    private String mHttpUrl;
    private int mLayoutId;
    private Class<T> mClazz;
    private String firstKey = null;
    private String lastKey = null;

    public RecycleFrag(int layoutId, Class<T> clazz)
    {
        this.mLayoutId = layoutId;
        this.mClazz = clazz;
    }

    @Override
    public int setContentView()
    {
        return R.layout.lib_frag_recycle;
    }

    public abstract void setItemView(T item, ViewHolder holder, int position);

    @SuppressWarnings("unchecked")
    @Override
    protected void initView()
    {
        super.initView();
        mRecyclerView = (NRecyclerView) this.findViewById(R.id.frag_recycle_content);

        //wrap recycler view
        mWrapRecyclerView = mRecyclerView.getRefreshableView();
        //设置layoutManager
        if (getLayoutManager() == null)
        {
            mWrapRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        } else
        {
            mWrapRecyclerView.setLayoutManager(getLayoutManager());
        }
        mWrapRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //adapter
        mData = new ArrayList<T>();
        mAdapter = new CommonAdapter<T>(getContext(), mLayoutId, mData)
        {
            @Override
            public void convert(ViewHolder holder, T item)
            {
                setItemView(item, holder, holder.getRealPosition());
            }
        };
        mWrapRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHeaderLayout(new HeaderLayout(getContext()));
        mFooterLayout = new FooterLayout(getContext());
        mRecyclerView.setSecondFooterLayout(mFooterLayout);
    }

    @Override
    protected void initListener()
    {
        super.initListener();
        mRecyclerView.setOnRefreshListener(this);
        mRecyclerView.setOnLastItemVisibleListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initData()
    {
        super.initData();
    }

    public RecyclerView.LayoutManager getLayoutManager()
    {
        return null;
    }

    public void setHttpData(Map<String, String> keysValus, String url)
    {
        mHttpData.clear();
        mHttpData.putAll(keysValus);
        mHttpData.put(CommonConstants.CUR_PAGE, mCurrentPage + "");
        mHttpData.put(CommonConstants.PAGE_SIZE, mCountPage + "");
        mHttpUrl = url;
    }

    @Override
    protected boolean init()
    {
        super.init();
        if (mRecyclerView != null)
        {
            refresh();
            return true;
        } else
        {
            return false;
        }
    }

    public void doPullRefresh()
    {
        mRecyclerView.setRefreshing();
    }

    public void refresh()
    {
        mCurrentPage = 1;
        mFooterLayout.setHasData();
        http(HTTP_TYPE_REFRESH);
    }

    protected void http(final int httpId)
    {
        if (mHttpData != null)
        {
            if (httpId == HTTP_TYPE_LOAD)
            {
                if (!mFooterLayout.isHasMoreData())
                {
                    return;
                }
            }
            mHttpData.put(CommonConstants.CUR_PAGE, mCurrentPage + "");
            ApiUtils
                    .getApi()
                    .http(mHttpUrl, mHttpData)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ApiResponse>()
                    {
                        @Override
                        public void onCompleted()
                        {

                        }

                        @Override
                        public void onError(Throwable e)
                        {
                            if (!checkHttpId(httpId)) return;
                            endPull(httpId);
                        }

                        @Override
                        public void onNext(ApiResponse s)
                        {
                            if (!checkHttpId(httpId)) return;
                            endPull(httpId);
                            try
                            {
                                String code = s.getStateCode();
                                if (code.equals(CommonConstants.CODE_SUCCESS))
                                {
                                    String list = getListString(s.getData());
                                    if (httpId == HTTP_TYPE_REFRESH)
                                    {
                                        mData.clear();
                                    }
                                    List<T> dataCache = GsonUtils.fromJsonArray(list, mClazz);
                                    if (dataCache.size() > 0)
                                    {
                                        mCurrentPage++;
                                    }
                                    mData.addAll(dataCache);
                                    boolean more = dataCache.size() == mCountPage;
                                    if (more)
                                    {
                                        mFooterLayout.setHasData();
                                    } else
                                    {
                                        mFooterLayout.setNoData();
                                    }
                                    if (httpId == HTTP_TYPE_REFRESH)
                                    {
                                        mAdapter.notifyDataSetChanged();
                                    } else
                                    {
                                        mAdapter.notifyItemRangeInserted(mData.size() - dataCache.size() + getHeadersCount(), dataCache.size());
                                    }
                                } else if (code.equals(CommonConstants.CODE_NO_MORE_DATA))
                                {
                                    if (HTTP_TYPE_REFRESH == httpId)
                                    {
                                        getData().clear();
                                    }
                                    mFooterLayout.setNoData();
                                    mAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
        } else
        {
            endPull(httpId);
        }
    }

    @Override
    public void onRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase refreshView)
    {
        refresh();
    }

    @Override
    public void onLastItemVisible()
    {
        Log.e("test", "底部可见");
        http(HTTP_TYPE_LOAD);
    }

    protected void endPull(int httpId)
    {
        if (httpId == HTTP_TYPE_REFRESH)
        {
            mRecyclerView.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mRecyclerView.onRefreshComplete();
                }
            }, 500);

        } else
        {
            mFooterLayout.setHasData();
        }
    }


    public void setFirstKey(String firstKey)
    {
        this.firstKey = firstKey;
    }

    public void setLastKey(String lastKey)
    {
        this.lastKey = lastKey;
    }

    protected String getListString(String s) throws JSONException
    {
        JSONObject jsonObject = new JSONObject(s);
        String list;
        if (lastKey == null)
        {
                 /*------默认dataMap{"list",[]}------*/
            JSONObject sj = jsonObject.getJSONObject(CommonConstants.DATAMAP);
            list = sj.getString(CommonConstants.LIST);
        } else if (lastKey.equals(""))
        {
            /*--------data[]或者datamap[]---------*/
            if (TextUtils.isEmpty(firstKey))
            {
                list = jsonObject.getString(CommonConstants.DATAMAP);
            } else
            {
                list = jsonObject.getString(firstKey);
            }
        } else
        {
             /*--------data{"lastKey",[]}或者datamap{"lastKey",[]}---------*/
            JSONObject sj = jsonObject.getJSONObject(firstKey);
            list = sj.getString(lastKey);
        }
        return list;
    }

    private boolean checkHttpId(int httpId)
    {
        if (httpId == HTTP_TYPE_REFRESH || httpId == HTTP_TYPE_LOAD)
        {
            return true;
        }
        return false;
    }

    public List<T> getData()
    {
        return mData;
    }

    public void setData(List<T> data)
    {
        mData.clear();
        mData.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    public void addData(List<T> data)
    {
        int oldCount = mData.size();
        mData.addAll(data);
        mAdapter.notifyItemRangeInserted(oldCount + getHeadersCount(), data.size());//第0项是header
    }

    public void setFooterLayoutVisiable(int visiable)
    {
        mFooterLayout.setVisibility(visiable);
    }

    private int getHeadersCount()
    {
        int headersCount = mWrapRecyclerView.getHeadersCount();
        if (headersCount < 0)
        {
            headersCount = 0;
        }
        return headersCount;
    }

    public NRecyclerView getList()
    {
        return mRecyclerView;
    }

    public void setMode(PullToRefreshBase.Mode mode)
    {
        mRecyclerView.setMode(mode);
    }

    public CommonAdapter getAdapter()
    {
        return mAdapter;
    }

    @Override
    public boolean onItemLongClick(ViewGroup parent, View view, T t, int position)
    {
        return false;
    }
}
