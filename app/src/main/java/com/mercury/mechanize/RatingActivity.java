package com.mercury.mechanize;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mercury.mechanize.Common.Common;
import com.mercury.mechanize.Model.Rate;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


import me.zhanghai.android.materialratingbar.MaterialRatingBar;



public class RatingActivity extends AppCompatActivity {

    Button btnSubmit,btnLater;
    MaterialEditText etComment;
    MaterialRatingBar ratingBar;


    FirebaseDatabase database;
    DatabaseReference rateDetails;
    DatabaseReference mechanicInfoRef;

    double ratingStars = 0.0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rating);

        database =FirebaseDatabase.getInstance();
        rateDetails =database.getReference(Common.rate_detail_tbl);
        mechanicInfoRef =database.getReference(Common.user_mechanic_tbl);


        //Init View
        btnSubmit = findViewById(R.id.btnSubmit);
        btnLater =findViewById(R.id.btnLater);
        ratingBar=  findViewById(R.id.RatingBar);
        etComment = findViewById(R.id.etComment);

        //Event
        ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                ratingStars = rating;

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRateDetails(Common.MechanicId);
            }
        });
        btnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RatingActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void submitRateDetails(final String mechanicId) {


        Rate rate = new Rate();
        rate.setRates(String.valueOf(ratingStars));
        rate.setComments(etComment.getText().toString());

        //update new value to firebase
        rateDetails.child(mechanicId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) //user id of rider who rates driver
                .push() //gen unique key
                .setValue(rate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // when upload successful on fireBase , we calculate average and update to mechanic
                        rateDetails.child(mechanicId)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        double averageStars = 0.0;
                                        int count = 0;
                                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                        {
                                            Rate rate = postSnapshot.getValue(Rate.class);
                                            averageStars+=Double.parseDouble(rate.getRates());
                                            count++;
                                        }
                                        double finalAverage = averageStars/count;
                                        DecimalFormat df= new DecimalFormat("#,#");
                                        String valueUpdate = df.format(finalAverage);

                                        //creating an object update
                                        Map<String,Object> mechanicUpdateRate = new HashMap<>();
                                        mechanicUpdateRate.put("rates",valueUpdate);

                                        mechanicInfoRef.child(Common.MechanicId)
                                                .updateChildren(mechanicUpdateRate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(RatingActivity.this, "Thank You For Your Feedback", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RatingActivity.this, "Rate Update Can't write to Mechanic Information", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RatingActivity.this, "Rate Failed", Toast.LENGTH_SHORT).show();
                    }
                });



    }
}
