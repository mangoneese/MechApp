package com.mercury.mechanize;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mercury.mechanize.Common.Common;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    MaterialEditText edtName,edtPhone,edtBirthday;
    ImageView imageAvatar;
    Button btnCancel,btnSave;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("MY PROFILE");


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        edtName = findViewById(R.id.edtName);
        edtBirthday = findViewById(R.id.edtBirthday);
        edtPhone = findViewById(R.id.edtPhone);
        imageAvatar = findViewById(R.id.imageAvatar);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String phone = edtPhone.getText().toString();
                String Birthday = edtBirthday.getText().toString();

                Map<String,Object> update = new HashMap<>();
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(Birthday))
                {
                    update.put("name",name);
                    update.put("phone",phone);
                    update.put("Birthday",Birthday);
                }

                DatabaseReference driverInfo = FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl);
                driverInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(ProfileActivity.this, "Information Updated!...", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(ProfileActivity.this, "Information Wasn't updated Try Again Later!!", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                startActivity(intent);
            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


        imageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageandUpload();
            }
        });



    }

    private void chooseImageandUpload() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() != null)
        {
            Uri saveUri = data.getData();
            if(saveUri != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Uploading....");
                progressDialog.show();

                String ImageName = UUID.randomUUID().toString(); //random new image to upload
                final StorageReference imageFolder = storageReference.child("images/"+ImageName);
                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();



                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // saving uri to User information table
                                        Map<String,Object> avatar = new HashMap<>();
                                        avatar.put("avatarUrl",uri.toString());

                                        //make the update
                                        DatabaseReference driverinfo = FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl);
                                        driverinfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(avatar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                Toast.makeText(ProfileActivity.this, "Avatar was Uploaded", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(ProfileActivity.this, "Avatar wasn't Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded"+progress+"%");
                            }
                        });

            }
        }
    }
}
