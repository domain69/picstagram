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

public class LoginActivity extends AppCompatActivity {
    TextView registerRedirect;
    EditText EmailField, PasswordField;
    Button LoginBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerRedirect = (TextView) findViewById(R.id.registerLink);
        mAuth = FirebaseAuth.getInstance();

        EmailField = (EditText) findViewById(R.id.emailAddress);
        PasswordField = (EditText) findViewById(R.id.password);
        LoginBtn = (Button) findViewById(R.id.loginBtn);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailField.getText().toString();
                String password = PasswordField.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(goToMainActivity);
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        registerRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegisterActivity = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(goToRegisterActivity);
            }
        });

    }
}