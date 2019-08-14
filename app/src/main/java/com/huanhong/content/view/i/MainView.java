package com.huanhong.content.view.i;


import android.view.View;

public interface MainView
{
    void lock();
    void unlock();
    void showBack(View view);
    void showFront(View view);
    void setShowText(String str);
}
