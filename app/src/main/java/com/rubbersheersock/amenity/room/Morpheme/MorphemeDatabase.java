package com.rubbersheersock.amenity.room.Morpheme;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Morpheme.class}, version=1)
public abstract class MorphemeDatabase extends RoomDatabase {
    public abstract MorphemeDao morphemeDao();
}
