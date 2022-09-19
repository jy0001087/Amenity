package com.rubbersheersock.amenity.room.Morpheme;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Morpheme {
    @PrimaryKey @NonNull
    public String morphemeText;

    @ColumnInfo(name = "morpheme_meaning")
    public String morphemeMeaning;

    @ColumnInfo(name = "morpheme_meaning2")
    public String getMorphemeMeaning2;

    @ColumnInfo(name = "morpheme_reserve")
    public String getMorphemeReserve;
}
