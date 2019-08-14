package com.huanhong.content.model.split.adaptive;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.huanhong.content.model.plan.server.ServerAction;
import com.huanhong.content.model.plan.server.ServerContent;
import com.zyn.lib.util.ViewUtils;

import java.util.List;

/**
 * Created by tuka2401 on 2017/1/9.
 */

public abstract class Adapter
{
    private Context mContext;

    public Adapter(Context context)
    {
        mContext = context;
    }

    public Context getContext()
    {
        return mContext;
    }

    abstract public void init();

    abstract public void deinit();

    abstract public View getView();

    abstract public ServerContent getCurrContent();

    public List<ServerAction> getCurrActionList()
    {
        ServerContent content = getCurrContent();
        if(content!=null)
        {
            List<ServerAction> actionList = content.getActionList();
            return actionList;
        }
        return null;
    }

    public Bitmap getBitmapShot()
    {
        return ViewUtils.getViewShot(getView());
    }
}
