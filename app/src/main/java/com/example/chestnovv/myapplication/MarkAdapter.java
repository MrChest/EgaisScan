package com.example.chestnovv.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.chestnovv.myapplication.db.Mark;

import java.util.List;

public class MarkAdapter extends RecyclerView.Adapter<MarkAdapter.MarkViewHolder> {

    private List<Mark> mEntries;
    private Context mContext;

    public MarkAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MarkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.marks_list_item, viewGroup, false);
        return new MarkAdapter.MarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkViewHolder holder, int position) {
        Mark mark = mEntries.get(position);
        holder.mark.setText(mark.getStamp());
        holder.itemView.setTag(mark.getStamp());

        if (position%2==0){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.rvItem));
        }
        else{
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        if (mEntries==null){
            return 0;
        }
        return mEntries.size();
    }

    public void setMark(List<Mark> marks){
        mEntries = marks;
        notifyDataSetChanged();
    }

    public class MarkViewHolder extends RecyclerView.ViewHolder{
        TextView mark;

        public MarkViewHolder(@NonNull View itemView) {
            super(itemView);
            mark = itemView.findViewById(R.id.mark);
        }
    }
}
