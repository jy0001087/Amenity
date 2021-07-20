package com.rubbersheersock.amenity.room;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class ZonedDateTimeConverter {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static ZonedDateTime revertZonedDateTime(String value) {
        return ZonedDateTime.parse(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static String converterZonedDateTime(ZonedDateTime value) {
        return value.toString();
    }
}

