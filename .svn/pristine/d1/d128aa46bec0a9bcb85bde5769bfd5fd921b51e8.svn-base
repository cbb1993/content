package com.zyn.lib.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zyn.lib.R;


public class RadiusImageView extends ImageView
{
    private int mRadius = (int) getResources().getDimension(R.dimen.lib_radius);

    public RadiusImageView(Context context)
    {
        this(context, null);
    }

    public RadiusImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public RadiusImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    Path path = new Path();
    @Override
    protected void onDraw(Canvas canvas)
    {
        path.rewind();
        RectF rectF = new RectF(0,0,canvas.getWidth(),canvas.getHeight());
        path.addRoundRect(rectF,mRadius,mRadius, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
