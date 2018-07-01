package com.example.android.journalapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

/**
 * Created by JONTE on 29/06/2018.
 */

public class AddEntryViewModel extends ViewModel {

    private LiveData<DiaryEntry> entry;

    //Create a constructor where you call loadEntryById of the entryDao to initialize the tasks variable
    public AddEntryViewModel(JAppDatabase database, int taskId) {
        entry = database.entryDao().loadEntryById(taskId);
    }

    // Create a getter for the entry variable
    public LiveData<DiaryEntry> getEntries() {
        return entry;
    }

}
