package com.rohan.tokenizerbusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

//        getSupportActionBar().hide();


        name = findViewById(R.id.address);
        email = findViewById(R.id.oTime);
        password = findViewById(R.id.cTime);
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

