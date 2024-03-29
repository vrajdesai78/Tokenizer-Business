package com.rohan.tokenizerbusiness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int IMAGE_REQUEST = 2;
    private EditText firmCategory, Address, open_time, close_time;
    private Button registerBtn;
    private String name, email, password;
    private FirebaseAuth auth;
    private ImageView profile;
    private Uri imageUri;
    private String cat;
    private AutoCompleteTextView atv;
    MaterialTimePicker materialTimePicker;
    TextInputLayout t1, t2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        atv = findViewById(R.id.firmCategory);
        String[] ctr = getResources().getStringArray(R.array.category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, ctr);
//        spinner.setAdapter(adapter);
        atv.setAdapter(adapter);



//        getSupportActionBar().hide();

//        firmCategory = findViewById(R.id.firmCategory);

//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.select_category, );
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.category, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setPrompt("Choose Category");
//        spinner.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);

        Address = findViewById(R.id.address);
        open_time = findViewById(R.id.oTime);
        close_time = findViewById(R.id.cTime);
        registerBtn = findViewById(R.id.nextButton);
        profile = findViewById(R.id.uploadProfile);
        t1 = findViewById(R.id.text_field_2);
        t2 = findViewById(R.id.text_field_3);


        name = getIntent().getStringExtra("firmName");
        email = getIntent().getStringExtra("Email");
        password = getIntent().getStringExtra("Password");

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(Address.getText())
                        ||  TextUtils.isEmpty(open_time.getText()) || TextUtils.isEmpty(close_time.getText())) {
                    Toast.makeText(RegisterDetails.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else {
//                    Toast.makeText(RegisterDetails.this, ""+email+"  "+password, Toast.LENGTH_SHORT).show();
                    registerBusiness(name, email, password, atv.getText().toString(),
                            Address.getText().toString(), open_time.getText().toString(),
                            close_time.getText().toString());
                }
            }
        });


        open_time.setOnClickListener(new View.OnClickListener() {
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
                        open_time.setText(materialTimePicker.getHour() + " : " + materialTimePicker.getMinute());
                    }
                });
            }
        });

        close_time.setOnClickListener(new View.OnClickListener() {
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
                        close_time.setText(materialTimePicker.getHour() + " : " + materialTimePicker.getMinute());
                    }
                });
            }
        });


    }

//    public void onCreateContextMenu(ContextMenu menu, View v,
//                                    ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.select_category, menu);
//    }

//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//    }

    private void registerBusiness(String firm_name, String email, String password, String firm_category,
                                  String addr, String o_time, String c_time) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterDetails.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Places")
                            .document(auth.getInstance().getCurrentUser().getUid());
                    Map<String, Object> user = new HashMap<>();
                    user.put("Name", firm_name);
                    user.put("Email", email);
                    user.put("Category", firm_category);
                    user.put("address", addr);
                    user.put("open", o_time);
                    user.put("close", c_time);

                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterDetails.this, "Business Registered Successfully", Toast.LENGTH_SHORT).show();
                            uploadImage();
                            Intent intent = new Intent(RegisterDetails.this, Dashboard.class);
                            intent.putExtra("Name", firm_name);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                else {
                    Toast.makeText(RegisterDetails.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            profile.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Uploading");
//        pd.show();

        if(imageUri != null) {
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("Places").child(auth.getCurrentUser().getUid() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Log.d("DownloadUrl", url);
//                            pd.dismiss();
                            Toast.makeText(RegisterDetails.this, "Image Upload Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cat = parent.getItemAtPosition(position).toString();
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        ((TextView) parent.getChildAt(0)).setTextSize(20);
        Toast.makeText(this, ""+cat, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}