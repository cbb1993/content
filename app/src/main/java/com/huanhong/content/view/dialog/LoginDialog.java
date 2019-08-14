package com.huanhong.content.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huanhong.content.R;
import com.huanhong.content.util.NormalUtil;
import com.zyn.lib.util.SharedPreferencesUtils;

/**
 * Created by Administrator on 2016/12/15.
 */
public class LoginDialog extends BaseDialog{
    private EditText dialog_login_tv_acount;
    private EditText dialog_login_tv_password;
    private TextView tv_cancle;
    private TextView tv_confirm;

    public LoginDialog(Context context, final OnLoginCallback callback) {
        super(context);
        setContentView(R.layout.dialog_login_layout);
        dialog_login_tv_acount= (EditText) findViewById(R.id.dialog_login_tv_acount);
        dialog_login_tv_password= (EditText) findViewById(R.id.dialog_login_tv_password);
        tv_cancle= (TextView) findViewById(R.id.tv_cancle);
        tv_confirm= (TextView) findViewById(R.id.tv_confirm);

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_login_tv_acount.setText("");
                dialog_login_tv_password.setText("");
                dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    callback.onComfirmClick(dialog_login_tv_acount.getText().toString(),dialog_login_tv_password.getText().toString());
                    dismiss();
                }
            }
        });
        dialog_login_tv_acount.setText(SharedPreferencesUtils.readData("acount"));

        if(dialog_login_tv_acount.getText().length()!=0){
            dialog_login_tv_password.requestFocus();
        }

    }
   public interface OnLoginCallback{
        void onComfirmClick(String acount,String password);
    }
    private boolean validate(){
        if(dialog_login_tv_acount.length()==0){
            NormalUtil.showToast("请输入用户编号");
            return false;
        }
        if(dialog_login_tv_password.length()==0){
            NormalUtil.showToast("请输入密码");
            return false;
        }
        return true;
    }
}
