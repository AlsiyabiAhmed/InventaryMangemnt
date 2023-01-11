package com.ahemdsiyabi.inventarymangemnt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.ahemdsiyabi.inventarymangemnt.mypackage.FBConstants;
import com.ahemdsiyabi.inventarymangemnt.mypackage.IMItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String itemId;
    private ImageView imgItem;
    private EditText inputItemName;
    private EditText inputItemPrice;
    private EditText inputItemQTY;


    // on default back button clicked close this screen
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // display default back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        imgItem = findViewById(R.id.imgItem);
        inputItemName = findViewById(R.id.inputItemName);
        inputItemPrice = findViewById(R.id.inputItemPrice);
        inputItemQTY = findViewById(R.id.inputItemQTY);



        imgItem.setOnClickListener(view -> {
            imageChooser();
        });

        Button buttonAddItem = findViewById(R.id.buttonAddItem);
        buttonAddItem.setOnClickListener(view -> {
            checkInputsData();
        });


    }


    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launcher.launch(i);
    }


    ActivityResultLauncher<Intent> launcher =  registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri selectedImageUri = data.getData();
                        imgItem.setImageURI(selectedImageUri);
                    }
                }
            });




    private void checkInputsData() {

        String itemName = inputItemName.getText().toString();
        String itemPrice = inputItemPrice.getText().toString();
        String itemQTY = inputItemQTY.getText().toString();

        //check email Pattern
        String matchPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (itemName.length() == 0) {
            Toast.makeText(AddActivity.this, "enter item name", Toast.LENGTH_SHORT).show();
        } else if (itemPrice.length() == 0) {
            Toast.makeText(AddActivity.this, "enter item price", Toast.LENGTH_SHORT).show();
        } else if (itemQTY.length() == 0) {
            Toast.makeText(AddActivity.this, "enter item QTY", Toast.LENGTH_SHORT).show();
        } else {
            createNewFBIMItem(itemName, itemPrice, itemQTY);
        }
    }

    private void createNewFBIMItem(String itemName, String itemPrice, String itemQTY) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference(FBConstants.FB_KEY_USERS)
                .child(firebaseUser.getUid())
                .child(FBConstants.FB_KEY_ITEMS);

        itemId = myRef.push().getKey();

        IMItem imItem = new IMItem();
        imItem.setItemId(itemId);
        imItem.setItemName(itemName);
        imItem.setItemPrice(itemPrice);
        imItem.setItemQTY(itemQTY);
        imItem.setItemImg(getImageUrl());

        myRef.child(itemId).setValue(imItem).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("AddActivity", "Created Successfully");
                finish();

            } else {
                // If sign in fails, display a message to the user.
                Log.w("AddActivity", "Creation:failed", task.getException());
                Toast.makeText(AddActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getImageUrl() {
        return "https://fastech-racing.com/images/magictoolbox_cache/cf3e6ec01aac7cb79461bcfe9d0d075e/2/2/2259/thumb400x400/4008162058/skm_lipseal.jpg";
    }


}