package com.mercury.mechanize;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mercury.mechanize.Common.Common;
import com.mercury.mechanize.Model.Driver;
import com.mercury.mechanize.Remote.IFCMService;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallDriver extends AppCompatActivity {
    CircleImageView image_avatar;
    TextView txt_name, txt_phone, txt_rate;
    Button btn_call, btn_call_phone, btn_book;

    String mechanicId;
    Location mLastLocation;

    IFCMService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_driver);

        mService = Common.getFCMService();

        //initView
        image_avatar = findViewById(R.id.avatar_image);
        txt_name = findViewById(R.id.txt_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_rate = findViewById(R.id.txt_rate);

        btn_call = findViewById(R.id.btn_call_driver);
        btn_call_phone = findViewById(R.id.btn_call_driver_phone);
        btn_book = findViewById(R.id.btn_bookAppointment);


        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mechanicId != null && !mechanicId.isEmpty())
                    Common.sendRequestToMechanic(Common.MechanicId, mService, getBaseContext(), mLastLocation);
            }
        });

        btn_call_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + txt_phone.getText().toString()));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(intent);
            }
        });

        //Get Intent
        if(getIntent() != null)
        {
            mechanicId = getIntent().getStringExtra("mechanicId");
            double lat = getIntent().getDoubleExtra("lat",-1.0);
            double lng = getIntent().getDoubleExtra("lng",-1.0);


            mLastLocation = new Location("");
            mLastLocation.setLatitude(lat);
            mLastLocation.setLongitude(lng);

            loadMechanicInfo(mechanicId);
        }
    }

    private void loadMechanicInfo(String mechanicId) {
        FirebaseDatabase.getInstance()
                .getReference(Common.user_mechanic_tbl)
                .child(mechanicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Driver mechuser = dataSnapshot.getValue(Driver.class);

                        if(!mechuser.getAvatarURL().isEmpty())
                        {
                            Picasso.get()
                                    .load(mechuser.getAvatarURL())
                                    .into(image_avatar);
                        }
                        txt_name.setText(mechuser.getName());
                        txt_phone.setText(mechuser.getPhone());
                        txt_rate.setText(mechuser.getRates());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
