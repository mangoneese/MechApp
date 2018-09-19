package com.mercury.mechanize.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.mercury.mechanize.Model.Providers;
import com.mercury.mechanize.R;
import com.mercury.mechanize.ViewHolders.ProvidersViewHolder;

import java.util.List;

public class ProviderAdapter extends RecyclerView.Adapter<ProvidersViewHolder>{


    private List<Providers> itemlist;
    private Context context;

    public ProviderAdapter(List<Providers> itemlist, Context context){
        this.itemlist = itemlist;
        this.context = context;
    }

    @NonNull
    @Override
    public ProvidersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_provider,null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ProvidersViewHolder viewHolder = new ProvidersViewHolder(layoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProvidersViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
