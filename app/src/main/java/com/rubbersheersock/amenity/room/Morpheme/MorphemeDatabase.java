package com.rubbersheersock.amenity.room.Morpheme;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Morpheme.class}, version=1)
public abstract class MorphemeDatabase extends RoomDatabase {
    public abstract MorphemeDao morphemeDao();
    static volatile MorphemeDatabase INSTANCE;
    public static final String MORPHEMEDATABASENAME = "MorphemeDatabase";

    public static final ExecutorService writeExecutor = Executors.newFixedThreadPool(3);

    public static final ExecutorService queryExecutor = Executors.newFixedThreadPool(3);

    public static MorphemeDatabase getDataBase(Context context){
        if(INSTANCE == null){
            synchronized (MorphemeDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context, MorphemeDatabase.class, MORPHEMEDATABASENAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
