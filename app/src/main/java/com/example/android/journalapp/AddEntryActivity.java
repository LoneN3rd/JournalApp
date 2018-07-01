package com.example.android.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.Date;

public class AddEntryActivity extends AppCompatActivity {

    // Extra for the Entry ID to be received in the intent
    public static final String EXTRA_ENTRY_ID = "extraEntryId";
    // Extra for the ENTRY ID to be received after rotation
    public static final String INSTANCE_ENTRY_ID = "instanceTaskId";

    // Constant for default ENTRY id to be used when not in update mode
    private static final int DEFAULT_ENTRY_ID = -1;
    // Constant for logging
    private static final String TAG = AddEntryActivity.class.getSimpleName();
    // Fields for views
    EditText mEditText;
    Button mButton;

    private int mEntryId = DEFAULT_ENTRY_ID;

    // Member variable for the Database
    private JAppDatabase mDb;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        initViews();

        //initialize the mDB Variable by calling the getInstance() method of the JAppDatabase class
        mDb = JAppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ENTRY_ID)) {
            mEntryId = savedInstanceState.getInt(INSTANCE_ENTRY_ID, DEFAULT_ENTRY_ID);
        }

        Intent intent = getIntent();

        if (intent !=null && intent.hasExtra(EXTRA_ENTRY_ID)){

            if (mEntryId == DEFAULT_ENTRY_ID){

                mButton.setText(R.string.update_button);

                // populate the UI
                mEntryId = intent.getIntExtra(EXTRA_ENTRY_ID, DEFAULT_ENTRY_ID);

                //Declare a AddEntryViewModelFactory using mDb and mEntryId
                AddEntryViewModelFactory factory = new AddEntryViewModelFactory(mDb, mEntryId);

                final AddEntryViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddEntryViewModel.class);

                //Observe the LiveData object in the ViewModel
                viewModel.getEntries().observe(this, new Observer<DiaryEntry>() {
                    @Override
                    public void onChanged(@Nullable DiaryEntry diaryEntry) {
                        viewModel.getEntries().removeObserver(this);
                        populateUI(diaryEntry);
                    }
                });

            }

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_ENTRY_ID, mEntryId);
        super.onSaveInstanceState(outState);
    }

    private void initViews() {
        mEditText = findViewById(R.id.editTextEntryDescription);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    private void populateUI(DiaryEntry task) {
        if (task == null) {
            return;
        }

        mEditText.setText(task.getDescription());
    }

    //call this method when the SAVE button is clicked
    public void onSaveButtonClicked() {
        String description = mEditText.getText().toString();
        Date date = new Date();

        final DiaryEntry entry = new DiaryEntry(description, date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mEntryId == DEFAULT_ENTRY_ID) {
                    // insert new task
                    mDb.entryDao().insertEntry(entry);
                } else {
                    //update task
                    entry.setId(mEntryId);
                    mDb.entryDao().updateEntry(entry);
                }
                finish();
            }
        });
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        Intent signInIntent =new Intent(getApplicationContext(),SignInActivity.class);
                        startActivity(signInIntent);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        super.onStart();
    }

}
