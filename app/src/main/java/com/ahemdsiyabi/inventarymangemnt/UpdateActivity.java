package com.ahemdsiyabi.inventarymangemnt;

import android.os.Bundle;

import com.ahemdsiyabi.inventarymangemnt.mypackage.FBConstants;
import com.ahemdsiyabi.inventarymangemnt.mypackage.IMItem;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private IMItem imItemOld;


    private ImageView imgIcon;
    private EditText inputItemName;
    private EditText inputItemPrice;
    private EditText inputItemQTY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imItemOld = (IMItem) extras.getSerializable(FBConstants.EXTRA_KEY_ITEM);
        }

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        imgIcon = findViewById(R.id.imgIcon);
        inputItemName = findViewById(R.id.inputItemName);
        inputItemPrice = findViewById(R.id.inputItemPrice);
        inputItemQTY = findViewById(R.id.inputItemQTY);

        inputItemName.setText(imItemOld.getItemName());
        inputItemPrice.setText(imItemOld.getItemPrice());
        inputItemQTY.setText(imItemOld.getItemQTY());

        Log.e("url", "onCreate: " + imItemOld.getItemImg());

        Glide.with(imgIcon.getContext())
                .load(imItemOld.getItemImg())
                .centerCrop()
                .placeholder(R.drawable.icon_add_item)
                .into(imgIcon);


        Button buttonUpdateItem = findViewById(R.id.buttonUpdateItem);
        buttonUpdateItem.setOnClickListener(view -> {
            checkInputsData();
        });


    }


    private void checkInputsData() {

        String itemName = inputItemName.getText().toString();
        String itemPrice = inputItemPrice.getText().toString();
        String itemQTY = inputItemQTY.getText().toString();

        //check email Pattern
        String matchPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (itemName.length() == 0) {
            Toast.makeText(UpdateActivity.this, "enter item name", Toast.LENGTH_SHORT).show();
        } else if (itemPrice.length() == 0) {
            Toast.makeText(UpdateActivity.this, "enter item price", Toast.LENGTH_SHORT).show();
        } else if (itemQTY.length() == 0) {
            Toast.makeText(UpdateActivity.this, "enter item QTY", Toast.LENGTH_SHORT).show();
        } else {
            updateFBIMItem(itemName, itemPrice, itemQTY);
        }
    }

    private void updateFBIMItem(String itemName, String itemPrice, String itemQTY) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference(FBConstants.FB_KEY_USERS)
                .child(firebaseUser.getUid())
                .child(FBConstants.FB_KEY_ITEMS);

        IMItem imItem = imItemOld;
        imItem.setItemName(itemName);
        imItem.setItemPrice(itemPrice);
        imItem.setItemQTY(itemQTY);
        imItem.setItemImg(getImageUrl());

        myRef.child(imItem.getItemId()).setValue(imItem).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("UpdateActivity", "Created Successfully");
                finish();

            } else {
                // If sign in fails, display a message to the user.
                Log.w("UpdateActivity", "Creation:failed", task.getException());
                Toast.makeText(UpdateActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getImageUrl() {
        return "https://fastech-racing.com/images/magictoolbox_cache/cf3e6ec01aac7cb79461bcfe9d0d075e/2/2/2259/thumb400x400/4008162058/skm_lipseal.jpg";
    }


}