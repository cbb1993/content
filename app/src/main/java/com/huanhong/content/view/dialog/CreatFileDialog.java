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
public class CreatFileDialog extends BaseDialog{
    private EditText ev_filename;
    private TextView tv_cancle;
    private TextView tv_confirm;
    private RadioButton rbt_1;
    private RadioButton rbt_2;
    public CreatFileDialog(Context context, final OnCreatCallback callback) {
        super(context);
        setContentView(R.layout.dialog_creat_layout);
        ev_filename= (EditText) findViewById(R.id.ev_filename);
        tv_cancle= (TextView) findViewById(R.id.tv_cancle);
        tv_confirm= (TextView) findViewById(R.id.tv_confirm);
        rbt_1= (RadioButton) findViewById(R.id.rbt_1);
        rbt_2= (RadioButton) findViewById(R.id.rbt_2);
        rbt_1.setChecked(true);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ev_filename.setText("");
                dismiss();
            }
        });
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ev_filename.getText().length()!=0){
                    boolean b = false;
                    if(rbt_1.isChecked()){
                        b=true;
                    }else if(rbt_2.isChecked()){
                        b=false;
                    }
                    callback.onComfirmClick(ev_filename.getText().toString(),b);
                    dismiss();
                }else {
                    NormalUtil.showToast("请输入文件（夹）名");
                }


            }
        });

    }
   public interface OnCreatCallback{
        void onComfirmClick(String FileName, boolean isFile);
    }

}
