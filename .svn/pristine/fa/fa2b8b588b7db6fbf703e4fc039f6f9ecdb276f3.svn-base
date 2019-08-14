package com.zyn.lib.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyn.lib.R;

import java.lang.Boolean;
import java.lang.Override;
import java.lang.String;


public class TitleView extends LinearLayout {
    private View view, mBtnBack, mBtnTvOther, mBtnIvOther;
    private View mRoot;
    private TextView mTvTitle;
    private View mLine;

    public TitleView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TitleView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.TitleView_title_background) {
                mRoot.setBackgroundColor(a.getColor(attr, 0));

            } else if (attr == R.styleable.TitleView_title_text) {
                ((TextView) (view.findViewById(R.id.view_title_tv_title))).setText(a
                        .getString(attr));

            } else if (attr == R.styleable.TitleView_title_other_text) {
                ((TextView) (view.findViewById(R.id.view_title_tv_other))).setText(a
                        .getString(attr));
            } else if (attr == R.styleable.TitleView_title_other_text_color) {
                ((TextView) (view.findViewById(R.id.view_title_tv_other))).setTextColor(a
                        .getColor(attr, 0x000000));
            } else if (attr == R.styleable.TitleView_title_spare_visible) {
                view.findViewById(R.id.view_title_btn_spare).setVisibility(
                        a.getBoolean(attr, false) ? View.VISIBLE
                                : View.INVISIBLE);

            } else if (attr == R.styleable.TitleView_title_text_other_visible) {
                view.findViewById(R.id.view_title_tv_other).setVisibility(
                        a.getBoolean(attr, false) ? View.VISIBLE
                                : View.INVISIBLE);

            } else if (attr == R.styleable.TitleView_title_back_visible) {
                ImageView btn_back = (ImageView) view.findViewById(R.id.view_title_btn_back);
                Boolean isVisible = a.getBoolean(attr, false);
                btn_back.setVisibility(isVisible
                        ? View.VISIBLE
                        : View.INVISIBLE);
                if (isVisible) {
                    btn_back.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Activity) context).onBackPressed();
                        }
                    });
                }
            } else if (attr == R.styleable.TitleView_title_back_src) {
                ImageView btn_back = (ImageView) view.findViewById(R.id.view_title_btn_back);
                Drawable drawable = a.getDrawable(attr);
                if (drawable != null) {
                    btn_back.setImageDrawable(drawable);
                }
            } else if (attr == R.styleable.TitleView_title_spare_src) {
                int drawId = a.getResourceId(attr, 0);
                if (drawId == 0)
                    break;
                ((ImageView) (view.findViewById(R.id.view_title_btn_spare)))
                        .setImageResource(drawId);

            } else if (attr == R.styleable.TitleView_title_line_visible) {
                boolean visible = a.getBoolean(attr, true);
                mLine.setVisibility(visible ? VISIBLE : INVISIBLE);
            }
        }
        a.recycle();

        Drawable background = mRoot.getBackground();
        ColorDrawable colorDrawable = (ColorDrawable) background;
        int color = colorDrawable.getColor();
        setBackgroundColor(color);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.lib_view_title,
                this, true);
        mRoot = view.findViewById(R.id.view_title_root);
        mTvTitle = (TextView) view.findViewById(R.id.view_title_tv_title);
        mBtnBack = view.findViewById(R.id.view_title_btn_back);
        mBtnTvOther = view.findViewById(R.id.view_title_tv_other);
        mBtnIvOther = view.findViewById(R.id.view_title_btn_spare);
        mLine = view.findViewById(R.id.view_title_line);

        mBtnBack.setVisibility(GONE);
        mBtnTvOther.setVisibility(GONE);
        mBtnIvOther.setVisibility(GONE);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context) {
        this(context, null, 0);
    }

    public void setTitle(String textRes) {

        mTvTitle.setText(textRes);
    }

    public void setTitleTextColor(@ColorInt int color) {
        mTvTitle.setTextColor(color);
    }

    public void setLineVisibility(int visible) {
        mLine.setVisibility(visible);
    }

    public void setRightTextClickListener(OnClickListener onClickListener) {
        mBtnTvOther.setOnClickListener(onClickListener);
    }

}
