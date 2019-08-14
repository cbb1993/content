package com.huanhong.content.model.split;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.huanhong.content.model.plan.server.ServerSplit;


public class VideoSplitItem extends SplitItem
{
    private RelativeLayout mRelativeLayout;
    private VideoView mVideoView;

    public VideoSplitItem(Context context, ServerSplit split, String url)
    {
        super(context, split);

        mRelativeLayout = new RelativeLayout(context);

        mVideoView = new VideoView(context);
        mVideoView.setVideoPath(url);
        mVideoView.start();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        mRelativeLayout.addView(mVideoView,params);
    }

    @Override
    public View getView()
    {
        return mRelativeLayout;
    }

    @Override
    public void recycle()
    {
        mRelativeLayout.removeAllViews();
        mRelativeLayout = null;

        mVideoView.pause();
        mVideoView.stopPlayback();
        mVideoView = null;

    }
}
