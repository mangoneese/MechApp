package com.mercury.mechanize;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mercury.mechanize.Common.Common;
import com.mercury.mechanize.Model.Driver;
import com.rengwuxian.materialedittext.MaterialEditText;


import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;


    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    private  final static int PERMISSION= 1000;

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.user_driver_tbl);

        //init paper
        Paper.init(this);


        //init View
        btnRegister = findViewById(R.id.btnRegister);
        btnSignIn = findViewById(R.id.btnSignIn);
        rootLayout = findViewById(R.id.rootLayout);


        //Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }


        });

        //Auto Login
        String user = Paper.book().read(Common.user_field);
        String pwd = Paper.book().read(Common.pwd_field);
        if(user != null && pwd != null)
        {
            if(!TextUtils.isEmpty(user)&&  !TextUtils.isEmpty(pwd))
            {
                autoLogin(user,pwd);
            }
        }

    }

    private void autoLogin(String user, String pwd) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("LOADING....");
        progressDialog.show();

        //user signIn
        auth.signInWithEmailAndPassword(user, pwd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        progressDialog.dismiss();




                        //fetch data and set to varible
                        FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Common.currentDriver = dataSnapshot.getValue(Driver.class);

                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();

                //Active button
                btnSignIn.setEnabled(true);
            }
        });





    }

    private void showLoginDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("please use email to sign In");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);


        final MaterialEditText etEmail = login_layout.findViewById(R.id.etEmail);
        final MaterialEditText etpass = login_layout.findViewById(R.id.etpass);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In....");
        progressDialog.show();

        dialog.setView(login_layout);

        //set Button
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();

                //set disable button sign in if is processing
                btnSignIn.setEnabled(false);


                //Check Validation

                if (TextUtils.isEmpty(etEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "please enter your Email", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(etpass.getText().toString())) {
                    Snackbar.make(rootLayout, "please enter your password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (etpass.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "password is too short!!!!", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }







                //user signIn
                auth.signInWithEmailAndPassword(etEmail.getText().toString(), etpass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressDialog.dismiss();


                                //fetch data and set to varible
                                FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Common.currentDriver = dataSnapshot.getValue(Driver.class);

                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                Paper.book().write(Common.user_field,etEmail.getText().toString());
                                Paper.book().write(Common.pwd_field,etpass.getText().toString());


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Snackbar.make(rootLayout, "Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();

                        //Active button
                        btnSignIn.setEnabled(true);
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

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("please use phone to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);


        final MaterialEditText etEmail = register_layout.findViewById(R.id.etEmail);
        final MaterialEditText etName = register_layout.findViewById(R.id.etName);
        final MaterialEditText etphone = register_layout.findViewById(R.id.etphone);
        final MaterialEditText etpass = register_layout.findViewById(R.id.etpass);
        final MaterialEditText etId = register_layout.findViewById(R.id.etId);


        dialog.setView(register_layout);

        //set Button
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //Check Validation
                if (TextUtils.isEmpty(etphone.getText().toString())) {
                    Snackbar.make(rootLayout, "please enter your phone number", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(etEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "please enter your Email", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(etpass.getText().toString())) {
                    Snackbar.make(rootLayout, "please enter your password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (etpass.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "password is too short!!!!", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(etId.getText().toString())) {
                    Snackbar.make(rootLayout, "please confirm your password", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    Snackbar.make(rootLayout, "please enter your Name", Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
//                       Register new user
                auth.createUserWithEmailAndPassword(etEmail.getText().toString(), etpass.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //save user  to database
                                Driver driver = new Driver();
                                driver.setName(etName.getText().toString());
                                driver.setEmail(etEmail.getText().toString());
                                driver.setPassword(etpass.getText().toString());
                                driver.setNationalId(etId.getText().toString());
                                driver.setPhone(etphone.getText().toString());
                                driver.setAvatarURL("");




                                //use email to key
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(driver)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Registered Successfully!!", Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Failed!!!" + e.getMessage(), Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

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



}
