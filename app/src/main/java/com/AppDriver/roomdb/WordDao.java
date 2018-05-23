package com.AppDriver.roomdb;



import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WordDao {


    @Query("SELECT * from word_table ORDER BY word ")
    LiveData<List<Word>> getAlphabetizedWords();


    @Insert
    void insert(Word word);

    @Query("DELETE FROM word_table")
    void deleteAll();
}
