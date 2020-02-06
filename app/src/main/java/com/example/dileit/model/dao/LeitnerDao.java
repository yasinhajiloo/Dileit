package com.example.dileit.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dileit.model.entity.Leitner;

import java.util.List;

@Dao
public interface LeitnerDao {
    @Insert
    void insert(Leitner leitner);
    @Update
    void update(Leitner leitner);
    @Delete
    void delete(Leitner leitner);
    @Query("SELECT * FROM  Leitner")
    List<Leitner> LEITNER_LIST();
}
