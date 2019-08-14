/*
package com.huanhong.content.view.view.robot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class RobotLayout extends RelativeLayout {

    private int screenWidth, screenHeight;
    private RobotView mRobotView;

    public RobotLayout(Context context) {
        this(context, null);
    }

    public RobotLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobotLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        mRobotView = new RobotView(getContext());
        mRobotView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("test", "点击");
            }
        });
        mRobotView.setClickable(true);
        mRobotView.setOnTouchListener(new OnTouchListener() {
            private int lastX, lastY;
            private long lastTouchTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchTime = System.currentTimeMillis();
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if (right > screenWidth) {
                            right = screenWidth;
                            left = right - v.getWidth();
                        }
                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > screenHeight) {
                            bottom = screenHeight;
                            top = bottom - v.getHeight();
                        }
                        //resize后会重置
//                        v.layout(left, top, right, bottom);
                        //resize后不会重置
                        RelativeLayout.LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
                        layoutParams.leftMargin = left;
                        layoutParams.topMargin = top;
                        v.setLayoutParams(layoutParams);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("test", "松开");
                        if(System.currentTimeMillis()-lastTouchTime>5000)
                        {
                            v.setVisibility(GONE);
                        }
                        break;
                }
                return false;
            }
        });
        addView(mRobotView, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void onDestroy() {
        if (mRobotView != null) {
            mRobotView.cancelAnimation();
        }
    }
}
*/
