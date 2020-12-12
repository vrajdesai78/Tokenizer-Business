package com.rohan.tokenizerbusiness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.file.attribute.UserPrincipalLookupService;

public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 2;
    private Button nextBtn;
    private EditText name, email, password;
    private ImageView upload;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        name = findViewById(R.id.firmName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        upload = findViewById(R.id.uploadProfile);
        login = findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });


        nextBtn = findViewById(R.id.nextButton);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(name.getText()) || TextUtils.isEmpty(email.getText()) || TextUtils.isEmpty(password.getText())) {
                    Toast.makeText(MainActivity.this, "Credentials are empty", Toast.LENGTH_SHORT).show();
                }
                else if(password.getText().toString().length() < 6) {
                    Toast.makeText(MainActivity.this, "Password must of atleast 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent intent = new Intent(MainActivity.this, RegisterDetails.class);
                    intent.putExtra("firmName", "" + name.getText().toString());
                    intent.putExtra("Email", "" + email.getText().toString());
                    intent.putExtra("Password", "" + password.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
        {
            startActivity(new Intent(MainActivity.this, Dashboard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }
}

