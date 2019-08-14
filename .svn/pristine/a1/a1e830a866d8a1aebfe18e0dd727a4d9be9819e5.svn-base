package com.zyn.lib.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

public interface Api
{
    @FormUrlEncoded
    @POST("{method}")
    Observable<ApiResponse> http(@Path("method") String method, @FieldMap Map<String,String> keysValues);
}