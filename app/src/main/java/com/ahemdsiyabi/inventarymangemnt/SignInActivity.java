package com.ahemdsiyabi.inventarymangemnt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    private EditText inputEmail;
    private EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputsData();
            }
        });

        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToSignUpActivity();
            }
        });
    }

    private void checkInputsData() {
        String email = inputEmail.getText().toString();
        String pass = inputPassword.getText().toString();

        //check email Pattern
        String matchPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.length() == 0) {
            Toast.makeText(SignInActivity.this, "enter your email", Toast.LENGTH_SHORT).show();
        } else if (!email.matches(matchPattern)) {
            Toast.makeText(SignInActivity.this, "email not valid!", Toast.LENGTH_SHORT).show();
        } else if (pass.length() == 0) {
            Toast.makeText(SignInActivity.this, "enter your password", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(SignInActivity.this, "password length less than 6 characters!", Toast.LENGTH_SHORT).show();
        } else {
            signInByFB(email, pass);
        }
    }

    private void signInByFB(String email, String pass) {
        firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        // Sign in success
                        firebaseUser = firebaseAuth.getCurrentUser();
                        moveToMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignInActivity", "signIn:failed", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void moveToMainActivity() {
        Intent i = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void moveToSignUpActivity() {
        Intent i = new Intent(SignInActivity.this, SignInActivity.class);
        startActivity(i);
        finish();
    }
}