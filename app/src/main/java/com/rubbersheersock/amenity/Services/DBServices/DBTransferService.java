package com.rubbersheersock.amenity.Services.DBServices;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;
import com.rubbersheersock.amenity.PubTools.HttpUnit;

import java.sql.SQLException;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class DBTransferService extends Service {
    private final IBinder binder = new Binder() {
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        XLog.tag("DBunti").i("DBTransferService is binded!");
        DBThreadPool.DBThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUnit ht = new HttpUnit.Builder("http://192.168.0.109:8080/WordReader_war","DataServlet")
                            .build();
                    OkHttpClient client= new OkHttpClient.Builder().build();
                    Response response = client.newCall(ht.getRequest()).execute();
                    if(response.code()==200){
                        XLog.tag("HTTP").i("访问正常"+response.body().string());
                    }
                } catch (Exception e) {
                    XLog.tag("DBunit").e("DB access error:", e);
                }
            }
        });
        return binder;
    }
}
