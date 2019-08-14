package com.zyn.lib.api;

import com.zyn.lib.api.Api;
import com.zyn.lib.constant.HostConstants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiUtils
{
    private static Api mApi;

    public static Api getApi()
    {
        if (mApi == null)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(HostConstants.HOST)
                    //增加返回值为Gson的支持(以实体类返回)
                    .addConverterFactory(GsonConverterFactory.create())
                    //增加返回值为String的支持
                    .addConverterFactory(ScalarsConverterFactory.create())
                    //增加Call为Oservable<T>的支持
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            mApi = retrofit.create(Api.class);
        }
        return mApi;
    }

}
