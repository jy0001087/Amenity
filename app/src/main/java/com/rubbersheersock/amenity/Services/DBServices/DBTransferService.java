package com.rubbersheersock.amenity.Services.DBServices;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;

import java.sql.SQLException;

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

                } catch (Exception e) {
                    XLog.tag("DBunit").e("DB access error:", e);
                }
            }
        });
        return binder;
    }
}
