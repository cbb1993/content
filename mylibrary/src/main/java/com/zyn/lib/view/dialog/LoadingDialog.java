package com.zyn.lib.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

import com.zyn.lib.R;

public class LoadingDialog
{
    private Dialog dialog;
    private Context context;
    private View view;

    public LoadingDialog(Context context)
    {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.lib_dialog_loading, null);
        dialog = new Dialog(context, R.style.LibFloatingDialog);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        //设置dialog宽高
        dialog.getWindow().setLayout(displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        dialog.setCancelable(false);
        dialog.setContentView(view);
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener()
//        {
//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
//            {
//                if(keyCode == KeyEvent.KEYCODE_BACK)
//                {
//                    dialog.dismiss();
//                    return true;
//                }
//                return false;
//            }
//        });
    }

    public void show()
    {
        dialog.show();
    }

    public void dismiss()
    {
        dialog.dismiss();
    }
}
