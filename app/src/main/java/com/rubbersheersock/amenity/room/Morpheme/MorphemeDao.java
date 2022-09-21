package com.rubbersheersock.amenity.room.Morpheme;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MorphemeDao {
    @Query("Select * from morpheme")
    List<Morpheme> getRecent();

    @Query("Select * from morpheme where morphemeText like '%'|| :morphemeText ||'%'")
    List<Morpheme> getLikeMorphemeText(String morphemeText);

    @Query("Select * from morpheme where morphemeText = :morphemeText ")
    List<Morpheme> getByMorphemeText(String morphemeText);

    @Query("Select * from morpheme where morphemeText = :morphemeText and morpheme_meaning like '%'|| :meaning ||'%'")
    List<Morpheme> getByMorphemeTextLikeMeaning(String morphemeText,String meaning);

    @Query("Select * from morpheme where morpheme_meaning like '%'|| :morphemeMeaning ||'%'")
    List<Morpheme> getByMorphemeMeaning(String morphemeMeaning);

    @Insert
    void insert(Morpheme... morpheme);

    @Update
    int update(Morpheme morpheme);

}
