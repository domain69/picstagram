package com.example.picstagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    TextView loginRedirect;
    EditText EmailField, PasswordField;
    Button RegisterBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        EmailField = (EditText) findViewById(R.id.emailAddress);
        PasswordField = (EditText) findViewById(R.id.password);
        RegisterBtn = (Button) findViewById(R.id.register);
        loginRedirect = (TextView) findViewById(R.id.loginLink);

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailField.getText().toString();
                String password = PasswordField.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(goToMainActivity);
                                }
                            }
                        });
            }
        });

        loginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToLoginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(goToLoginActivity);
            }
        });
    }
}