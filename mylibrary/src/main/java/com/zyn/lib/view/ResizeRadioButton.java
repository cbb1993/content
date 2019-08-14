package com.zyn.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.zyn.lib.R;

public class ResizeRadioButton extends RadioButton
{
    public ResizeRadioButton(Context context)
    {
        this(context, null);
    }

    public ResizeRadioButton(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ResizeRadioButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.ResizeRadioButton,defStyleAttr,0);
        init(typedArray);
        typedArray.recycle();
    }

    public void init(TypedArray typedArray)
    {
        if (typedArray == null)
        {
            return;
        }

        int width = (int) typedArray.getDimension(R.styleable.ResizeRadioButton_drawable_width, 0);
        int height = (int) typedArray.getDimension(R.styleable.ResizeRadioButton_drawable_height, 0);
        if (width != 0 && height != 0)
        {
            setDrawableSize(width, height);
        }
    }


    public void setDrawableSize(int width, int height)
    {
        Drawable[] drawables = getCompoundDrawables();
        for (Drawable drawable : drawables)
        {
            if(drawable!=null)
            {
                drawable.setBounds(0, 0, width , height);
            }
        }
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }
}
