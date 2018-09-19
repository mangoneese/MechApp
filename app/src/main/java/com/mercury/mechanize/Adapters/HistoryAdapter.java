package com.mercury.mechanize.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercury.mechanize.Model.History;
import com.mercury.mechanize.R;
import com.mercury.mechanize.ViewHolders.HistoryViewHolder;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private List<History> itemlist;
    private Context context;

    public HistoryAdapter(List<History> itemlist, Context context){
        this.itemlist = itemlist;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        HistoryViewHolder viewHolder = new HistoryViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
         holder.jobId.setText(itemlist.get(position).getJobId());
         holder.time.setText(itemlist.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
