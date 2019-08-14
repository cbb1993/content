package com.huanhong.content.presenter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.huanhong.content.model.sync.SyncManager;
import com.huanhong.content.view.activity.MainActivity;
import com.huanhong.content.view.i.UpdateView;
import com.zyn.lib.presenter.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class UpdatePresenter extends BasePresenter<UpdateView>
{
    public static final String TPYE = "type";
    public static final String TYPE_PLAN = "plan";
    public static final String TYPE_SYNC = "sync";
    public static final String DATA = "data";

    public UpdatePresenter(Context context, UpdateView view)
    {
        super(context, view);
        EventBus.getDefault().register(this);
        init();
    }


    private void init()
    {
        Intent intent = ((Activity) getContext()).getIntent();
        if (intent != null) {
            SyncManager.getInstance().sync(intent.getStringExtra(TPYE), intent.getStringExtra(DATA));
        }

        getView().startSyncAnim();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlerSyncEvent(SyncManager.SyncEvent syncEvent)
    {
        switch (syncEvent.getType())
        {
            case SyncManager.SyncEvent.TYPE_START:
            {
                getView().showMessage(syncEvent.getMsg());
            }break;
            case SyncManager.SyncEvent.TYPE_MSG:
            {
                getView().showMessage(syncEvent.getMsg());
            }break;
            case SyncManager.SyncEvent.TYPE_ERROR:
            {
                getView().showMessage(syncEvent.getMsg());
            }break;
            case SyncManager.SyncEvent.TYPE_SYNC_START:
            {
                getView().showMessage(syncEvent.getMsg());
            }break;
            case SyncManager.SyncEvent.TYPE_SYNC_FINISH:
            {
                getView().showMessage(syncEvent.getMsg());
            }break;
            case SyncManager.SyncEvent.TYPE_CHECK_START:
            {
                getView().showMessage(syncEvent.getMsg());
            }break;
            case SyncManager.SyncEvent.TYPE_CHECK_FINISH:
            {
                getView().showMessage(syncEvent.getMsg());
            }break;
            case SyncManager.SyncEvent.TYPE_FINISH:
            {
                Log.e("========","============"+syncEvent.getMsg());
                getView().showMessage(syncEvent.getMsg());

                // 更新完成后判断是不是视频图片demo类型 不是则正常跳转


                toMainActivity();
            }break;
            case SyncManager.SyncEvent.TYPE_BUSING:
            {
                //ignore
            }break;
            default:
            {
                getView().showMessage("系统错误，请稍后重试");
            }break;
        }
    }


    private void toMainActivity()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                getContext().startActivity(new Intent(getContext(), MainActivity.class));
                ((Activity) getContext()).finish();
            }
        },0);

    }
    @Override
    public void onDestory() {
        EventBus.getDefault().unregister(this);
        super.onDestory();
    }
}
