package com.mercury.mechanize;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mercury.mechanize.Adapters.HistoryAdapter;
import com.mercury.mechanize.Model.History;
import com.mercury.mechanize.Model.Providers;

import java.util.ArrayList;

public class ProvidersActivity extends AppCompatActivity {

    private RecyclerView mProvider;
    private RecyclerView.Adapter mProviderAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers);
        setTitle("PROVIDERS");

        mProvider =  findViewById(R.id.historyRecycler);
        mProvider.setNestedScrollingEnabled(false);
        mProvider.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mProvider.setLayoutManager(mLayoutManager);
        mProvider.setAdapter(mProviderAdapter);
        mProviderAdapter = new HistoryAdapter(getProviders(),this);



        mProviderAdapter.notifyDataSetChanged();
    }
    private ArrayList providers = new ArrayList<Providers>();

    private ArrayList<History> getProviders() {
        return providers;
    }
}
