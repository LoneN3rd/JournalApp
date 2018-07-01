package com.example.android.journalapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

/**
 * Created by JONTE on 28/06/2018.
 */

@Database(entities = {DiaryEntry.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class JAppDatabase extends RoomDatabase {

    private static final String LOG_TAG = JAppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "diaryentries";
    private static JAppDatabase sInstance;

    public static JAppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        JAppDatabase.class, JAppDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract EntryDao entryDao();
}
