/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.chestnovv.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.chestnovv.myapplication.db.Document;
import com.example.chestnovv.myapplication.db.DocumentWithCount;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This TaskAdapter creates and binds ViewHolders, that hold the description and priority of a task,
 * to a RecyclerView to efficiently display data.
 */
public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    final private ItemContextClickListener mItemContextClickListener;
    // Class variables for the List that holds task data and the Context
    private List<DocumentWithCount> mEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public DocumentAdapter(Context context, ItemClickListener listener, ItemContextClickListener contextClickListener) {
        mContext = context;
        mItemClickListener = listener;
        mItemContextClickListener = contextClickListener;

       // mEntries = new SortedList<>(DocumentWithCount.class, new SortedListCallBack());
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.document_list_item, parent, false);

        return new DocumentViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        // Determine the values of the wanted data
        DocumentWithCount documentEntry = mEntries.get(position);
        String number = documentEntry.getNumberInovice();
        String count = String.valueOf(documentEntry.getCount());
        String countStamp = String.valueOf(documentEntry.getCountStamp());
        String client = documentEntry.getTitle();
        String summ = String.format("%.2f", documentEntry.getSumm());
        //int priority = taskEntry.getPriority();
        String date = dateFormat.format(documentEntry.getDate());
        boolean isSendTo1c = documentEntry.isSendTo1c();

        //Set values
        holder.numberView.setText(number);
        holder.dateView.setText(date);
        holder.clientView.setText(client);
        holder.countView.setText(count+"/"+countStamp);
        holder.summView.setText(summ);

        if (isSendTo1c){
            holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_document_send));
        }
        else{
            holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_document));
        }


        if (position%2==0){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.rvItem));
        }
        else{
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mEntries == null) {
            return 0;
        }
        return mEntries.size();
    }

    public List<DocumentWithCount> getDocuments() {
        return mEntries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setDocuments(List<DocumentWithCount> documents) {
        mEntries = documents;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(String itemId);
    }

    public interface ItemContextClickListener {
        void onContextItemSelected(MenuItem item, String itemId);
    }

    // Inner class for creating ViewHolders
    class DocumentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MenuItem.OnMenuItemClickListener, View.OnCreateContextMenuListener {

        // Class variables for the task description and priority TextViews
        TextView numberView;
        TextView dateView;
        TextView countView;
        TextView clientView;
        TextView summView;
        ImageView ivIcon;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public DocumentViewHolder(View itemView) {
            super(itemView);

            numberView = itemView.findViewById(R.id.number);
            dateView = itemView.findViewById(R.id.date);
            countView = itemView.findViewById(R.id.count);
            clientView = itemView.findViewById(R.id.client);
            summView = itemView.findViewById(R.id.summ);
            ivIcon = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View view) {
            String elementId = mEntries.get(getAdapterPosition()).getNumber();
            mItemClickListener.onItemClickListener(elementId);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem myActionItem = menu.add("Отправить");
            myActionItem.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            mItemContextClickListener.onContextItemSelected(item, mEntries.get(getAdapterPosition()).getNumber());
            return true;
        }
    }
}