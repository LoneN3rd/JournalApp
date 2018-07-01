package com.example.android.journalapp;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by JONTE on 28/06/2018.
 */

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<DiaryEntry> mDiaryEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public EntryAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.entry_layout, parent, false);

        return new EntryViewHolder(view);
    }

    public void onBindViewHolder(EntryViewHolder holder, int position) {
        // Determine the values of the wanted data
        DiaryEntry taskEntry = mDiaryEntries.get(position);
        String description = taskEntry.getDescription();
        String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());

        //Set values
        holder.entryDescription.setText(description);
        holder.entryUpdatedAt.setText(updatedAt);
    }

    // Inner class for creating ViewHolders
    class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView entryDescription;
        TextView entryUpdatedAt;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public EntryViewHolder(View itemView) {
            super(itemView);

            entryDescription = itemView.findViewById(R.id.entryDescription);
            entryUpdatedAt = itemView.findViewById(R.id.entryUpdatedAt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mDiaryEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

    public int getItemCount() {
        if (mDiaryEntries == null) {
            return 0;
        }
        return mDiaryEntries.size();
    }

    public List<DiaryEntry> getEntries() {
        return mDiaryEntries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setEntries(List<DiaryEntry> diaryEntries) {
        mDiaryEntries = diaryEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

}
