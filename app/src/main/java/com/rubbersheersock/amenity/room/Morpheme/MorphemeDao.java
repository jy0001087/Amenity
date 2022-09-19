package com.rubbersheersock.amenity.room.Morpheme;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MorphemeDao {
    @Query("Select * from morpheme")
    List<Morpheme> getRecent();

    @Query("Select * from morpheme where morphemeText like '%'|| :morphemeText ||'%'")
    List<Morpheme> getByMorphemeText(String morphemeText);

    @Query("Select * from morpheme where morpheme_meaning like '%'|| :morphemeMeaning ||'%'")
    List<Morpheme> getByMorphemeMeaning(String morphemeMeaning);

    @Insert
    void insert(Morpheme morpheme);

}
