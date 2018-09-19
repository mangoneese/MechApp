package com.mercury.mechanize.ViewHolders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mercury.mechanize.HistoryPage;
import com.mercury.mechanize.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView jobId;
    public TextView time;

    public HistoryViewHolder(View itemView) {
        super(itemView);

        jobId = itemView.findViewById(R.id.jobId);
        time = itemView.findViewById(R.id.time);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(),HistoryPage.class);
        Bundle b = new Bundle();
        b.putString("jobId",jobId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
