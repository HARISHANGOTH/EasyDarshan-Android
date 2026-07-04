package com.easydarshan.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.easydarshan.data.model.Temple;

import java.util.List;

@Dao
public interface TempleDao {
    @Query("SELECT * FROM temples")
    List<Temple> getAllTemples();

    @Query("SELECT * FROM temples WHERE id = :id")
    Temple getTempleById(Long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTemples(List<Temple> temples);

    @Query("DELETE FROM temples")
    void deleteAll();
}
