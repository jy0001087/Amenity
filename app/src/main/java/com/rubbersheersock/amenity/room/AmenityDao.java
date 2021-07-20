package com.rubbersheersock.amenity.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AmenityDao {
    @Insert
    void insertAll(Amenity... amenities);

    @Delete
    void delete(Amenity amenity);

    @Query("SELECT * FROM Amenity")
    public Amenity[] loadAllAmenitys();

}
