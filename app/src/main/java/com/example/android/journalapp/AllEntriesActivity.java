package com.example.android.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class AllEntriesActivity extends AppCompatActivity implements EntryAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = AllEntriesActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private EntryAdapter mAdapter;

    private JAppDatabase mDb;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_entries);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewEntries);

        // Set the layout for the RecyclerView to be a linear layout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new EntryAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        //Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<DiaryEntry> tasks = mAdapter.getEntries();
                        mDb.entryDao().deleteEntry(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        //Set the Floating Action Button (FAB) to its corresponding View.
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(AllEntriesActivity.this, AddEntryActivity.class);
                startActivity(addTaskIntent);
            }
        });

        mDb = JAppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    private void setupViewModel() {
        EntriesViewModel viewModel = ViewModelProviders.of(this).get(EntriesViewModel.class);
        viewModel.getEntries().observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(@Nullable List<DiaryEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setEntries(taskEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddEntryActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(AllEntriesActivity.this, AddEntryActivity.class);
        intent.putExtra(AddEntryActivity.EXTRA_ENTRY_ID, itemId);
        startActivity(intent);
    }

    private void signOut() {
        //Intent signInIntent = new Intent(this, SignInActivity.class);
        //startActivity(signInIntent);

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
