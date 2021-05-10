package com.rohan.tokenizerbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.RemoteInput;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.core.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserDetails extends AppCompatActivity {

    BottomNavigationView bmv;
    TextView f_name, adr, ot, ct;
    FirebaseFirestore firebaseFirestore;
    Button editDetails;
    Dialog dialog;
    EditText firmName, address, oTime, cTime;
    Button confirm, cancel;
    ImageView img,e_img;
    StorageReference storageReference;
    MaterialTimePicker materialTimePicker;
    TextInputLayout t1,t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        f_name = findViewById(R.id.f_name);
        adr = findViewById(R.id.adr);
        ot = findViewById(R.id.ot);
        ct = findViewById(R.id.ct);
        img = findViewById(R.id.i_img);

        firebaseFirestore = FirebaseFirestore.getInstance();
        editDetails = findViewById(R.id.editDetails);

        bmv = findViewById(R.id.bottom_navigation);

        bmv.setSelectedItemId(R.id.page_2);

        firebaseFirestore.collection("Places").document(""+FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                f_name.setText(documentSnapshot.get("Name").toString());
                adr.setText(documentSnapshot.get("address").toString());
                ot.setText(documentSnapshot.get("open").toString());
                ct.setText(documentSnapshot.get("close").toString());

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("Places")
                .child(FirebaseAuth.getInstance().getUid()+ "." + "jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageUrl = uri.toString();
                Glide.with(getApplicationContext()).load(imageUrl).into(img);
            }
        });



        dialog = new Dialog(UserDetails.this);

        dialog.setContentView(R.layout.edit_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        firmName = dialog.findViewById(R.id.editfirmName);
        address = dialog.findViewById(R.id.editaddress);
        oTime = dialog.findViewById(R.id.editopentime);
        cTime = dialog.findViewById(R.id.editclosetime);
        confirm = dialog.findViewById(R.id.confirm);
        cancel = dialog.findViewById(R.id.cancel);
        e_img = dialog.findViewById(R.id.b_image);


        oTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
//                mTimePicker.show(getSupportFragmentManager(), "TIME_PICKER");
                materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(hour)
                        .setMinute(minute)
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "Time_Picker");
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        oTime.setText(materialTimePicker.getHour() + " : " + materialTimePicker.getMinute());
                    }
                });
            }
        });

        cTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
//                mTimePicker.show(getSupportFragmentManager(), "TIME_PICKER");
                materialTimePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(hour)
                        .setMinute(minute)
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "Time_Picker");
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cTime.setText(materialTimePicker.getHour() + " : " + materialTimePicker.getMinute());
                    }
                });
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        firebaseFirestore.collection("Places").document(""+FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                firmName.setText(documentSnapshot.get("Name").toString());
                address.setText(documentSnapshot.get("address").toString());
                oTime.setText(documentSnapshot.get("open").toString());
                cTime.setText(documentSnapshot.get("close").toString());

            }
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("Places")
                .child(FirebaseAuth.getInstance().getUid()+ "." + "jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageUrl = uri.toString();
                Glide.with(getApplicationContext()).load(imageUrl).into(e_img);
            }
        });


        bmv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.page_1:
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.page_2:
                        return true;
                }
                return false;
            }
        });

        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> data = new HashMap<>();
                data.put("Name", firmName.getText().toString());
                data.put("address", address.getText().toString());
                data.put("open", oTime.getText().toString());
                data.put("close", cTime.getText().toString());

                firebaseFirestore.collection("Places").document(""+FirebaseAuth.getInstance().getUid())
                        .set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Toast.makeText(UserDetails.this, "Data Updated", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(UserDetails.this, "Unable to update", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}