package com.example.gb.forcemultiplier;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public static final String cName = "name";
    public static final String time_r = "time";
    public static final String lat = "latitude";
    public static final String lon = "longitude";
    public static final String desc = "description";

    private List<taskQueue> tasklists;
    private Context context;

    public TaskAdapter(List<taskQueue> tasklists,Context context){
        this.tasklists =tasklists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_for_fragment,viewGroup,false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final taskQueue tasklist =tasklists.get(i);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView cust_Name;
        private TextView reqTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
