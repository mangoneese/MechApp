package com.mercury.mechanize.Common;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mercury.mechanize.HomeActivity;
import com.mercury.mechanize.Model.DataMessage;
import com.mercury.mechanize.Model.Driver;
import com.mercury.mechanize.Model.FCMResponse;
import com.mercury.mechanize.Model.Token;
import com.mercury.mechanize.Remote.FCMClient;
import com.mercury.mechanize.Remote.IFCMService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Common {

    public static final String mechanic_tbl="Mechanics";
    public static final String user_mechanic_tbl="MechanicsInformation";
    public static final String user_driver_tbl="DriversInformation";
    public static final String pickup_request_tbl="PickupRequest";
    public static final String token_tbl="Tokens";
    public static final String history_tbl="History";
    public static String rate_detail_tbl="RateDetails";

    public static Driver currentDriver = new Driver();
    public static String JobId="";

    public static boolean isMechanicFound = false;


    public static   String MechanicId="";
    public static String DriverId="";

    public static final String fcmUrl="https://fcm.googleapis.com/";
    public static final String user_field= "driver_usr";
    public static final String pwd_field ="driver_pwd";
    public static  final int PICK_IMAGE_REQUEST= 9999;


    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmUrl).create(IFCMService.class);
    }

    public static void sendRequestToMechanic(String MechanicId, final IFCMService mService, final Context context, final Location currentLocation) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.token_tbl);

        tokens.orderByKey().equalTo(MechanicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Token token = postSnapshot.getValue(Token.class);//Get token object from database with key

                            //raw payload- convert Latlng to json
                            String Drivertoken = FirebaseInstanceId.getInstance().getToken();


                            Map<String,String> content = new HashMap<>();
                            content.put("customer",Drivertoken);
                            content.put("lat",String.valueOf(currentLocation.getLatitude()));
                            content.put("lng",String.valueOf(currentLocation.getLongitude()));
                            DataMessage dataMessage = new DataMessage(token.getToken(),content);

                            mService.sendMessage(dataMessage)
                                    .enqueue(new Callback<FCMResponse>() {
                                        @Override
                                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                            if(response.body().success== 1)
                                                Toast.makeText(context, "Request Sent", Toast.LENGTH_SHORT).show();
                                            else
                                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());

                                        }
                                    });






                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
