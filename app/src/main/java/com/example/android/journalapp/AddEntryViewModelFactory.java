package com.example.android.journalapp;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

/**
 * Created by JONTE on 29/06/2018.
 */

public class AddEntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    //Add member variables for the database and the entryId
    private final int mEntryId;
    private final JAppDatabase mDb;


    public AddEntryViewModelFactory(JAppDatabase database, int entryId) {
        mEntryId = entryId;
        mDb = database;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {

        return (T) new AddEntryViewModel(mDb, mEntryId);

    }
}
