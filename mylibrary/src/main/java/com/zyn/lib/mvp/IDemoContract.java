package com.zyn.lib.mvp;

/**
 * Created by admin on 2017/4/8.
 */

public interface IDemoContract {
    interface IDemoView extends IBaseView<IDemoPresenter>
    {
        void showToast(String message);
    }


    interface IDemoPresenter extends IBasePresenter<IDemoView>
    {

    }
}
