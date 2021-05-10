package com.rohan.tokenizerbusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private SignInButton signInButton;
    private String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth auth;
    private TextView register_txt;
    private String place_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        getSupportActionBar().hide();

        register_txt = findViewById(R.id.register_txt);
        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });

        email = findViewById(R.id.oTime);
        password = findViewById(R.id.cTime);
        login = findViewById(R.id.login_button);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(LoginActivity.this, "Credentials Can't be Empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    String email_txt = email.getText().toString();
                    String password_txt = password.getText().toString();
                    loginUser(email_txt, password_txt);
                }
            }
        });


    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseFirestore documentReference = FirebaseFirestore.getInstance();
                documentReference.collection("Places").whereEqualTo("Email", email)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                        else {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }
}


