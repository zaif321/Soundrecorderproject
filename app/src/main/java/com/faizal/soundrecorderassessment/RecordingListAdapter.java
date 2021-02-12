package com.faizal.soundrecorderassessment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.File;

public class RecordingListAdapter extends RecyclerView.Adapter<RecordingListAdapter.RecordingViewHolder> {

    private File[] allFiles;
    private TimeFilter timeFilter;
    private OnItemListClick onItemListClick;

    public RecordingListAdapter(File[] allFiles, OnItemListClick onItemListClick){
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }
    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_design,parent,false);
        timeFilter = new TimeFilter();
        return new RecordingViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull RecordingViewHolder holder, int position) {
        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeFilter.getTimeFilter(allFiles[position].lastModified()));

    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class RecordingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;

        public RecordingViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.image_list_recycler);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onItemListClick.onClickListener(allFiles[getAdapterPosition()],getAdapterPosition());

        }
    }
    public interface OnItemListClick{
        void onClickListener(File file, int position);
    }
}
