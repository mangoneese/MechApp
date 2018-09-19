package com.mercury.mechanize.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercury.mechanize.R;

public class ProvidersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView MechanicId;

    public ProvidersViewHolder(View itemView) {
        super(itemView);

        this.MechanicId = itemView.findViewById(R.id.mechanicId);
    }

    @Override
    public void onClick(View v) {

    }
}
