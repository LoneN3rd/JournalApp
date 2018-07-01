package com.example.android.journalapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

/**
 * Created by JONTE on 28/06/2018.
 */

public class EntriesViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = EntriesViewModel.class.getSimpleName();

    private LiveData<List<DiaryEntry>> tasks;

    public EntriesViewModel(Application application) {
        super(application);
        JAppDatabase database = JAppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.entryDao().loadAllEntries();
    }

    public LiveData<List<DiaryEntry>> getEntries() {
        return tasks;
    }
}
