package com.rubbersheersock.amenity.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.elvishew.xlog.XLog;
import com.rubbersheersock.amenity.MainActivity;

public class ScreenObserverService extends Service {
    //锁屏监听
    private ScreenObserver screenObserver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        XLog.i("on Bind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //锁屏监听服务
        screenObserver = new ScreenObserver(this);
        screenObserver.begin(new ScreenObserver.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                XLog.i("屏幕打开");
            }
            @Override
            public void onScreenOff() {
                XLog.i("屏幕关闭");
            }
            @Override
            public void onUserPresent() {
                XLog.i("解锁了");
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}
