package com.huanhong.content.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.huanhong.content.R;

/**
 * Created by Administrator on 2016/12/15.
 */
public class BaseDialog extends Dialog {
    public BaseDialog(Context context) {
        super(context, R.style.dialog);
        Window window = getWindow();
        setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}
