package com.rubbersheersock.amenity.room;

import android.content.Context;

import androidx.room.Room;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;

public class AmenityDataProcesser {
    //禁止创建实例
    private AmenityDataProcesser(){
    }

    public static AmenityDatabase db;

    public synchronized static AmenityDatabase getAmenityBuilder(Context context) {
        if(db == null){
        db = Room.databaseBuilder(context,
                AmenityDatabase.class, "amenity.db")
                .build();
        }else{
            XLog.i("返回已有db实例");
            return db;
        }
        return db;
    }
}
