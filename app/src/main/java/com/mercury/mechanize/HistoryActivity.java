package com.mercury.mechanize;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mercury.mechanize.Adapters.HistoryAdapter;
import com.mercury.mechanize.Common.Common;
import com.mercury.mechanize.Model.History;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mHistory;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    FirebaseDatabase database;
    DatabaseReference history;
    DatabaseReference driverInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("HISTORY");

        database = FirebaseDatabase.getInstance();
        history = database.getReference(Common.history_tbl);

        mHistory =  findViewById(R.id.historyRecycler);
        mHistory.setNestedScrollingEnabled(false);
        mHistory.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mHistory.setLayoutManager(mLayoutManager);
        mHistory.setAdapter(mHistoryAdapter);
        mHistoryAdapter = new HistoryAdapter(getHistory(),this);

        getHistoryId();

    }


    private void getHistoryId() {

        driverInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        driverInfo.child(Common.JobId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for(DataSnapshot hist :dataSnapshot.getChildren()){
                                fetchJobHistory(hist.getKey());
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void fetchJobHistory(String Jobkey) {
        driverInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        history.child(Jobkey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String JobId =dataSnapshot.getKey();
                            Long timestamp = 0L;
                            for(DataSnapshot snap : dataSnapshot.getChildren())
                            {
                                if(snap.getKey().equals("timestamp")){
                                    timestamp =Long.valueOf(snap.getValue().toString());
                                }
                            }
                            History hist = new History(JobId,getDate(timestamp));
                            historyResult.add(hist);
                            mHistoryAdapter.notifyDataSetChanged();


                        }
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private String getDate(Long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timestamp*1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm",cal).toString();

        return date;
    }

    private ArrayList historyResult = new ArrayList<History>();

    private ArrayList<History> getHistory() {
        return historyResult;
    }
}
