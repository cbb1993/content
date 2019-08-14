package com.huanhong.content.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.huanhong.content.R;
import com.huanhong.content.util.NormalUtil;

/**
 * Created by Administrator on 2016/12/15.
 */
public class UrlDialog extends BaseDialog{
    private EditText ev_url;
    private TextView tv_cancle;
    private TextView tv_confirm;

    public UrlDialog(Context context, final OnURLCreatCallback callback) {
        super(context);
        setContentView(R.layout.dialog_url_layout);
        ev_url= (EditText) findViewById(R.id.ev_url);
        tv_cancle= (TextView) findViewById(R.id.tv_cancle);
        tv_confirm= (TextView) findViewById(R.id.tv_confirm);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ev_url.setText("");
                dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ev_url.getText().length()!=0){
                    callback.onComfirmClick(ev_url.getText().toString());
                    dismiss();
                }else {
                    NormalUtil.showToast("请输入URl");
                }
            }
        });

    }
   public interface OnURLCreatCallback{
        void onComfirmClick(String Url);
    }

}
