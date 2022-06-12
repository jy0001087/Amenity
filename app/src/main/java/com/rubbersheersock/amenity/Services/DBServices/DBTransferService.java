package com.rubbersheersock.amenity.Services.DBServices;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.elvishew.xlog.XLog;
import com.rubbersheersock.amenity.PubTools.HttpUnit;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class DBTransferService extends Service {
 //用于向其他控件传输数据
    private Handler serviceHandler = new ServiceHandler();//用于子线程向service传输数据
    private JSONObject json;


    //设置Service内数据接收handler
    class ServiceHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            json = (JSONObject) msg.obj;
            Intent jsonIntent = new Intent();
            jsonIntent.setAction("com.rubbersheersock.amenity.jsonQuery");
            jsonIntent.putExtra("json",json.toString());
            sendBroadcast(jsonIntent);
            XLog.tag("DBunit").i("Service has already gotten json ");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        XLog.tag("DBunti").i("DBTransferService is binded!");
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        DBThreadPool.DBThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUnit ht = new HttpUnit.Builder("http://cloud.wind4us.com:8080/Spider-0.1-SNAPSHOT","DataServlet")
                            .build();
                    OkHttpClient client= new OkHttpClient.Builder().build();
                    Response response = client.newCall(ht.getRequest()).execute();
                    if(response.code()==200){
                        JSONObject json = new JSONObject(response.body().string());
                        Message msg = Message.obtain();
                        msg.obj=json;
                        serviceHandler.sendMessage(msg);
                        XLog.tag("HTTP").i("访问远程数据服务正常");
                    }
                } catch (Exception e) {
                    XLog.tag("DBunit").e("DB access error:", e);
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

}
