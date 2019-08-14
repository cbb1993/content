package com.huanhong.content.model.uploadfile;


import com.zyn.lib.constant.HostConstants;
import com.zyn.lib.util.SharedPreferencesUtils;
import org.json.JSONObject;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/1/13.
 */

public class UpLoadUtil {
private static Retrofit retrofit;

    public  static void upLoad(File file, final UpLoadListener listener){

        FileUploadService service = getInstance().create(FileUploadService.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        MultipartBody.Part body1 = MultipartBody.Part.createFormData("myFile",file.getName(),requestBody);

        MultipartBody.Part body2 = MultipartBody.Part.createFormData("platform_id",SharedPreferencesUtils.readData("acount"));

        Call<ResponseBody> call = service.upload(body1,body2);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                String s="";
                if(response.body()!=null){
                    try {
                        s= new JSONObject(response.body().string()).getString("data");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.success(s);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.fail(t.toString());
            }
        });
    }

    private static Retrofit getInstance(){
        if(retrofit==null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();
             retrofit = new Retrofit.Builder().baseUrl(HostConstants.BASEHOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}
