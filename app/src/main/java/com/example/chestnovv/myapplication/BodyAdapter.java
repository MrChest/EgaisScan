package com.example.chestnovv.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.chestnovv.myapplication.db.BodyWithTovar;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.BodyViewHolder>  {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private BodyAdapter.ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<BodyWithTovar> mEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public BodyAdapter(Context context, BodyAdapter.ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public BodyAdapter.BodyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.body_list_item, parent, false);

        return new BodyAdapter.BodyViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(BodyAdapter.BodyViewHolder holder, int position) {
        // Determine the values of the wanted data
        BodyWithTovar bodyEntry = mEntries.get(position);
        String tovarId = bodyEntry.getTovarId();
        String description = bodyEntry.getTovarDescription();
        String count = String.valueOf(bodyEntry.getCount());
        String countStamp = String.valueOf(bodyEntry.getCountStamp());

        //Set values
        holder.tovarId.setText(tovarId);
        holder.description.setText(description);
        holder.count.setText(count+"/"+countStamp);

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

    public List<BodyWithTovar> getBodyWithTovars() {
        return mEntries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setBody(List<BodyWithTovar> bodys) {
        mEntries = bodys;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(String itemId);
    }

    // Inner class for creating ViewHolders
    class BodyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView tovarId;
        TextView description;
        TextView count;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public BodyViewHolder(View itemView) {
            super(itemView);

            tovarId = itemView.findViewById(R.id.tovar_id);
            description = itemView.findViewById(R.id.description);
            count = itemView.findViewById(R.id.count);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String elementId = mEntries.get(getAdapterPosition()).getTovarId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
