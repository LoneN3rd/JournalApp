package com.example.android.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by JONTE on 28/06/2018.
 */

@Dao
public interface EntryDao {

    @Query("SELECT * FROM Diary ORDER BY id")
    LiveData<List<DiaryEntry>> loadAllEntries();

    @Insert
    void insertEntry(DiaryEntry diaryEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateEntry(DiaryEntry diaryEntry);

    @Delete
    void deleteEntry(DiaryEntry diaryEntry);

    @Query("SELECT * FROM Diary WHERE id = :id")
    LiveData<DiaryEntry> loadEntryById(int id);

}
