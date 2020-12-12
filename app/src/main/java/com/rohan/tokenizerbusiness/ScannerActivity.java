package com.rohan.tokenizerbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.w3c.dom.Text;

public class ScannerActivity extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView scannerView;
    private String scanned_txt;
    private FirebaseFirestore db;
    Dialog dialog;
    Button confirm, cancel;
    private TextView name, email, timing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        getSupportActionBar().hide();

        scannerView = findViewById(R.id.scannerView);

        codeScanner = new CodeScanner(this, scannerView);

        db = FirebaseFirestore.getInstance();

        dialog = new Dialog(ScannerActivity.this);
        dialog.setContentView(R.layout.qr_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        confirm = dialog.findViewById(R.id.confirm_btn);
        cancel = dialog.findViewById(R.id.cancel_btn);
        name = dialog.findViewById(R.id.user_name);
        email = dialog.findViewById(R.id.user_email);
        timing = dialog.findViewById(R.id.user_timing);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scanned_txt = result.getText();
                        Query docref = db.collection("BookingDetails")
                                .whereEqualTo("Business_email", FirebaseAuth.getInstance().getCurrentUser().getEmail());

                        docref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot ds: queryDocumentSnapshots)
                                {
                                    if(TextUtils.equals(scanned_txt, ds.getId()))
                                    {
                                        FirebaseFirestore documentReference = FirebaseFirestore.getInstance();
                                        documentReference.collection("Users").document(""+ds.getString("UserId"))
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    name.setText("Name: "+task.getResult().getString("Name"));
                                                    email.setText("Email: "+task.getResult().getString("Email"));
                                                    timing.setText("Time: "+ds.getTimestamp("Timing").toDate().toString());
                                                    dialog.show();
                                                    confirm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                db.collection("BookingDetails").document(""+scanned_txt)
                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(ScannerActivity.this, "Booking closed", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        startActivity(new Intent(ScannerActivity.this, Dashboard.class));
                                                        finish();
                                                    }
                                                });
                                            }
                                        });
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
//                        docref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if(task.isSuccessful()) {
//                                    DocumentSnapshot documentSnapshot = task.getResult();
//                                    if(documentSnapshot.exists()) {
//                                        dialog.show();
//
//                                    }
//                                    else {
//                                        Toast.makeText(ScannerActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }).addOnCanceledListener(new OnCanceledListener() {
//                            @Override
//                            public void onCanceled() {
//                                Toast.makeText(ScannerActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
//                            }
//                        });

//                        if(TextUtils.equals(scanned_txt, getIntent().getStringExtra("id")))
//                        {
//                            dialog.show();
//                        }
//                        else {
//                            Toast.makeText(ScannerActivity.this, "Booking Not Found Try Again", Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestforCamera();
    }

    private void requestforCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(ScannerActivity.this, "Camera Permission is required", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}