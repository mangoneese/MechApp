package com.mercury.mechanize;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.mercury.mechanize.Common.Common;
import com.mercury.mechanize.Helper.CustomInfoWindow;
import com.mercury.mechanize.Model.DataMessage;
import com.mercury.mechanize.Model.Driver;
import com.mercury.mechanize.Model.FCMResponse;
import com.mercury.mechanize.Model.Token;
import com.mercury.mechanize.Remote.IFCMService;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,GoogleMap.OnInfoWindowClickListener
{




    SupportMapFragment mapFragment;

    LinearLayout layoutRating;

    //location
    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE=7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST= 3000193;

    private LocationRequest mlocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL= 5000;
    private static int FASTEST_INTERVAL=3000;
    private static int DISPLACEMENT =10;

    public static int navItemIndex=0;
    String title ="Home";
    private int backCounter;


    DatabaseReference driver;
    GeoFire geofire;

    Marker mDriver;

    //BottomSheet
    ImageView imgExpandable;
    BottomSheetDriverFragment mBottomSheet;
    Button btnPickupRequest;

    String driverId;

    int radius = 1; //1km
    int distance = 1; //3km
    private static final int LIMIT = 10;

    //send Alert
    IFCMService mService;

    //presence system
    DatabaseReference mechanicsAvailable;

    PlaceAutocompleteFragment place_location;
    AutoCompleteTextView carProblem;

    CircleImageView avatar;
    TextView driverName;

    FirebaseStorage storage;
    StorageReference storeref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutRating = findViewById(R.id.layoutRating);

        mService = Common.getFCMService();

        //init storage
        storage = FirebaseStorage.getInstance();
        storeref = storage.getReference();

        final DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        driver = FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl);



        View header = navigationView.getHeaderView(0);
        driverName  = header.findViewById(R.id.driverName);
        driverName.setText(String.format("%s",Common.currentDriver.getName()));
        avatar = header.findViewById(R.id.avatar);

        //Load Avatar
        if(Common.currentDriver.getAvatarURL() != null && !TextUtils.isEmpty(Common.currentDriver.getAvatarURL()))
        {
          Picasso.get()
                  .load(Common.currentDriver.getAvatarURL())
                  .into(avatar);
        }

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
            }
        });
        navItemIndex =0;

        //maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        //initView
        imgExpandable = findViewById(R.id.imgExpandable);
//        mBottomSheet =BottomSheetDriverFragment.newInstance("Driver Bottom Sheet");
        imgExpandable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheet.show(getSupportFragmentManager(),mBottomSheet.getTag());
            }
        });

        btnPickupRequest = findViewById(R.id.btnPickup);
        btnPickupRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!Common.isMechanicFound)
                   requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
               else
                   Common.sendRequestToMechanic(Common.MechanicId,mService,getBaseContext(),mLastLocation);

            }
        });

//        place_location =  (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_location);
//        carProblem = (AutoCompleteTextView)findViewById(R.id.mcarProblem);



        setUpLocation();

        updateFirebaseToken();


    }


    private void updateFirebaseToken() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Common.token_tbl);

        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(token);

    }



    private void requestPickupHere(String uid) {
        DatabaseReference dbRequest = FirebaseDatabase.getInstance().getReference(Common.pickup_request_tbl);
        GeoFire mGeoFire = new GeoFire(dbRequest);
        mGeoFire.setLocation(uid,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

        if(mDriver.isVisible())
            mDriver.remove();

        //Add new Marker
        mDriver = mMap.addMarker(new MarkerOptions()
                .title("Pickup Here")
                .snippet("")
                .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mDriver.showInfoWindow();

        btnPickupRequest.setText("Getting your Mechanic....");

        findMechanic();


    }

    private void findMechanic() {
        DatabaseReference mechanics = FirebaseDatabase.getInstance().getReference(Common.mechanic_tbl);
        GeoFire gMechanics = new GeoFire(mechanics);

        final GeoQuery geoQuery = gMechanics.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                            radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //if found
                if(!Common.isMechanicFound)
                {
                    Common.isMechanicFound = true;
                    Common.MechanicId = key;
                    btnPickupRequest.setText("CALL MECHANIC");
                    Toast.makeText(HomeActivity.this,""+key,Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
              //if still not found driver, increase distance
                if(!Common.isMechanicFound && radius<LIMIT)

                {
                    radius++;
                    findMechanic();
                }
                else{
                    if(!Common.isMechanicFound)
                    {
                        Toast.makeText(HomeActivity.this, "No Available Mechanic Near You", Toast.LENGTH_SHORT).show();
                        btnPickupRequest.setText("REQUEST MECHANIC");
                        geoQuery.removeAllListeners();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;
        }
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                ) {

            //Request runtime permission
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE
            },MY_PERMISSION_REQUEST_CODE);
        }
        else{
            if(checkPlayServices()){
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation !=null){

            //Presence system
            mechanicsAvailable = FirebaseDatabase.getInstance().getReference(Common.mechanic_tbl);
            mechanicsAvailable.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //if theres change from mechs table ,reload all available mechs
                    loadAllMechanicsAvailable();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            final double latitude = mLastLocation.getLatitude();
            final double longitude = mLastLocation.getLongitude();



                    //Add Marker
                    if(mDriver != null)
                        mDriver.remove(); //remove old marker
                    mDriver = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude,longitude))
                            .title(String.format("You")));
                    //move camera to this position
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),15.0f));

                    loadAllMechanicsAvailable();

            Log.d("Gitau",String.format("Your Location was changed: %f/%f",latitude,longitude));
        }
        else{
            Log.d("Gitau","Cannot get your Location");
        }


    }

    private void loadAllMechanicsAvailable() {
        //First delete all markers on map
        mMap.clear();
        //after,  juhs add our location again
        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                                          .title("You"));


        //Load all available mechanics in 3km distance
        DatabaseReference mechanicLocation = FirebaseDatabase.getInstance().getReference(Common.mechanic_tbl);
        GeoFire gf = new GeoFire(mechanicLocation);

        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),distance);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, final GeoLocation location) {
                //use key to get email from table Users
                //users - table where driver register account and update info
                FirebaseDatabase.getInstance().getReference(Common.user_mechanic_tbl)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               //Because driver and user model has same properties
                               //So we can use driver model to get User here
                                Driver driver = dataSnapshot.getValue(Driver.class);

                                // add driver to map
                                mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(location.latitude,location.longitude))
                                                .flat(true)
                                                .title(driver.getName())
                                                .snippet("Mechanic ID:"+dataSnapshot.getKey())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mechanic)));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(distance <= LIMIT) //distance just find for 3km
                {
                    distance++;
                    loadAllMechanicsAvailable();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void createLocationRequest() {
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(UPDATE_INTERVAL);
        mlocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mlocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
     int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
     if(resultCode != ConnectionResult.SUCCESS){
         if(GooglePlayServicesUtil.isUserRecoverableError(resultCode))
             GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
         else{
             Toast.makeText(this,"This device is not Supported",Toast.LENGTH_SHORT).show();
             finish();
         }
         return false;
     }
     return true;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }



        if (navItemIndex == 0) { // If in first page exit app
            if(backCounter == 0){
                backCounter++;
                Toast.makeText(this, "You are on the home screen. Press back again to exit the application", Toast.LENGTH_LONG).show();
            } else{
                finish();
            }
        } else {  // Move to first page
            navItemIndex = 0;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Paper.init(this);
            Paper.book().destroy();

            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            navItemIndex = 1;
            Intent intent = new Intent(HomeActivity.this,HistoryActivity.class);
            startActivity(intent);
            title = "HISTORY";
        } else if (id == R.id.nav_providers) {
            navItemIndex =2;
            Intent intent = new Intent(HomeActivity.this,HistoryActivity.class);
            startActivity(intent);
            title = "Providers";

        } else if (id == R.id.nav_profile) {
            navItemIndex = 3;
            Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
            startActivity(intent);
            title = "My Profile";

        }
        else if (id == R.id.nav_car) {
            navItemIndex = 3;
            showcarupdateDialog();
            title = "Car Info";

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showcarupdateDialog() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(HomeActivity.this);
        dialog.setTitle("UPDATE CAR INFORMATION");
        dialog.setMessage("Please fill all information");

        LayoutInflater inflater = this.getLayoutInflater();
        View layout_car = inflater.inflate(R.layout.layout_update_car_type,null);

        final MaterialEditText etCar = layout_car.findViewById(R.id.etCar);
        final MaterialEditText etModel = layout_car.findViewById(R.id.etModel);

        //

        dialog.setView(layout_car);

        dialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new ProgressDialog(HomeActivity.this);
                waitingDialog.show();


                driver.child(driverId)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                String car =etCar.getText().toString();
                String model = etModel.getText().toString();

                Map<String,Object> update = new HashMap<>();
                if(!TextUtils.isEmpty(car))
                    update.put("car",car);
                if(!TextUtils.isEmpty(model))
                    update.put("model",model);

                DatabaseReference driverinfo = FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl);
                driverinfo.child(driverId)
                        .updateChildren(update)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(HomeActivity.this, "Information Updated", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(HomeActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();

                                waitingDialog.dismiss();
                            }
                        });
                        //refresh driver data
                        driverinfo.child(driverId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Common.currentDriver = dataSnapshot.getValue(Driver.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
            }

        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(this));

        mMap.setOnInfoWindowClickListener(this);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mlocationRequest,this);

        }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
       mLastLocation = location;
       displayLocation();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //if marker info window is our location we wont implement this method
        if(!marker.getTitle().equals("You"))
        {
            Intent intent = new Intent(HomeActivity.this,CallDriver.class);
            intent.putExtra("driverId",marker.getSnippet().replaceAll("\\D+",""));
            intent.putExtra("lat",mLastLocation.getLatitude());
            intent.putExtra("lng",mLastLocation.getLongitude());
            startActivity(intent);
        }
    }
}
