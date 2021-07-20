package com.rubbersheersock.amenity.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(
        entities = {Amenity.class},
        version = 1
)
@TypeConverters(ZonedDateTimeConverter.class)
public abstract class AmenityDatabase extends RoomDatabase{
    public abstract AmenityDao amenityDao();
}
