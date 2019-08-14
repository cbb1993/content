package com.huanhong.content.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huanhong.content.R;


/**
 * Created by Administrator on 2016/12/26.
 */

public class ProgressDialog extends Dialog{
    ProgressBar progressBar;
    TextView msg ;
    public ProgressDialog(Context context) {
        super(context,R.style.dialog_translate);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        setContentView(R.layout.dialog_progress_layout);
        progressBar= (ProgressBar) findViewById(R.id.pro_loading);
        msg= (TextView) findViewById(R.id.pro_tv_msg);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_loading);
        progressBar.startAnimation(animation);
    }

    public void setMsg(String msg) {
        this.msg.setText(msg);
    }
}
