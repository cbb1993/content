package com.huanhong.content.model.split.adaptive;

import android.content.Context;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huanhong.content.model.plan.server.ServerContent;
import com.huanhong.content.util.FileUtil;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;

import java.util.List;

/**
 * Created by tuka2401 on 2017/1/9.
 */

public class ImageAdapter extends Adapter
{
    private FrameLayout mRoot;
    private RollPagerView mRollPagerView;
    private View mUnTouch;
    private List<ServerContent> mUrlList;
    private ViewGroup.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public ImageAdapter(Context context, List<ServerContent> urlList)
    {
        super(context);
        mUrlList = urlList;

        mUnTouch = new View(getContext());
        mUnTouch.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
    }

    @Override
    public void init()
    {
        mRoot = new FrameLayout(getContext());

        mRollPagerView = new RollPagerView(getContext());
        mRollPagerView.setPlayDelay(10000);
        mRollPagerView.setAnimationDurtion(1000);
        mRollPagerView.setHintView(null);
        mRollPagerView.setAdapter(new LoopPagerAdapter(mRollPagerView)
        {
            @Override
            public View getView(ViewGroup container, int position)
            {
                if (mUrlList != null)
                {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageURI(Uri.parse(FileUtil.getPathByShowPath(mUrlList.get(position).getShowPath())));
//                    SimpleDraweeView simpleDraweeView = new SimpleDraweeView(getContext());
//                    simpleDraweeView.setImageURI(Uri.parse(FileUtil.getPathByShowPath(mUrlList.get(position).getShowPath())));
                    return imageView;
                }

                return null;
            }

            @Override
            protected int getRealCount()
            {
                if (mUrlList != null)
                {
                    return mUrlList.size();
                }
                return 0;
            }
        });

        mRoot.addView(mRollPagerView, mLayoutParams);
        mRoot.addView(mUnTouch, mLayoutParams);
    }

    @Override
    public View getView()
    {
        return mRoot;
    }

    @Override
    public ServerContent getCurrContent()
    {
        try
        {
            return mUrlList.get(mRollPagerView.getViewPager().getCurrentItem()%mUrlList.size());
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void deinit()
    {
        if (mRoot != null)
        {
            mRoot.removeAllViews();
            mRoot = null;
        }

        mRollPagerView = null;
    }
}
