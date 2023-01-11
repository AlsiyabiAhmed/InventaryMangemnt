package com.ahemdsiyabi.inventarymangemnt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ahemdsiyabi.inventarymangemnt.mypackage.FBConstants;
import com.ahemdsiyabi.inventarymangemnt.mypackage.IMUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    private EditText inputPersonName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputRewritePassword;

    // on default back button clicked close this screen
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // display default back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        inputPersonName = findViewById(R.id.inputPersonName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputRewritePassword = findViewById(R.id.inputRewritePassword);

        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputsData();
            }
        });


    }


    private void checkInputsData() {
        String pName = inputPersonName.getText().toString();
        String email = inputEmail.getText().toString();
        String pass = inputPassword.getText().toString();
        String rePass = inputRewritePassword.getText().toString();

        //check email Pattern
        String matchPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (pName.length() == 0) {
            Toast.makeText(SignUpActivity.this, "enter your name", Toast.LENGTH_SHORT).show();
        } else if (email.length() == 0) {
            Toast.makeText(SignUpActivity.this, "enter your email", Toast.LENGTH_SHORT).show();
        } else if (!email.matches(matchPattern)) {
            Toast.makeText(SignUpActivity.this, "email not valid!", Toast.LENGTH_SHORT).show();
        } else if (pass.length() == 0) {
            Toast.makeText(SignUpActivity.this, "enter your password", Toast.LENGTH_SHORT).show();
        } else if (pass.length() < 6) {
            Toast.makeText(SignUpActivity.this, "password length less than 6 characters!", Toast.LENGTH_SHORT).show();
        } else if (rePass.length() == 0) {
            Toast.makeText(SignUpActivity.this, "rewrite password is empty!", Toast.LENGTH_SHORT).show();
        } else if (rePass.length() < 6) {
            Toast.makeText(SignUpActivity.this, "rewrite password length less than 6 characters!", Toast.LENGTH_SHORT).show();
        } else if (!rePass.equals(pass)) {
            Toast.makeText(SignUpActivity.this, "password not matching to rewrite password", Toast.LENGTH_SHORT).show();
        } else {
            progressBarToggle();
            signUpToFB(pName, email, pass);
        }
    }

    private void signUpToFB(String pName, String email, String pass) {

        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success

                        firebaseUser = firebaseAuth.getCurrentUser();
                        createNewFBUser(pName, email);
                    } else {
                        // If sign in fails
                        progressBarToggle();
                        Log.d("SignUpActivity", "Sign in:failed", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void createNewFBUser(String pName, String email) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FBConstants.FB_KEY_USERS);

        IMUser u = new IMUser();
        u.setUserId(firebaseUser.getUid());
        u.setUserPersonName(pName);
        u.setUserEmail(email);
        Log.d("SingUpActictity", "createNewFBUser: " + u.getUserId());

        myRef.child(u.getUserId()).setValue(u).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success,
                progressBarToggle();
                moveToMainActivity();

            } else {
                // If sign in fails
                progressBarToggle();
                Log.d("SignUpActivity", "Signup:failure", task.getException());
                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void moveToMainActivity() {
        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void progressBarToggle() {
        View viewBlur = findViewById(R.id.viewBlur);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        if (progressBar.getVisibility() == View.VISIBLE) {
            viewBlur.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            viewBlur.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

}