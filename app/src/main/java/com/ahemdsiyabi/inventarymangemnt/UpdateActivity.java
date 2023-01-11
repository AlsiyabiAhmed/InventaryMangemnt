package com.ahemdsiyabi.inventarymangemnt;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ahemdsiyabi.inventarymangemnt.mypackage.FBConstants;
import com.ahemdsiyabi.inventarymangemnt.mypackage.IMItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private IMItem imItemOld;

    private Uri selectedImageUri;
    private ImageView imgItem;
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

        imgItem = findViewById(R.id.imgItem);
        inputItemName = findViewById(R.id.inputItemName);
        inputItemPrice = findViewById(R.id.inputItemPrice);
        inputItemQTY = findViewById(R.id.inputItemQTY);

        inputItemName.setText(imItemOld.getItemName());
        inputItemPrice.setText(imItemOld.getItemPrice());
        inputItemQTY.setText(imItemOld.getItemQTY());

        Glide.with(imgItem.getContext())
                .load(imItemOld.getItemImg())
                .placeholder(R.drawable.icon_add_item)
                .into(imgItem);

        imgItem.setOnClickListener(view -> {
            imageChooser();
        });

        Button buttonUpdateItem = findViewById(R.id.buttonUpdateItem);
        buttonUpdateItem.setOnClickListener(view -> {
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

    // open gallery and wait for the user to select a single img
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        selectedImageUri = data.getData();
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
        progressBarToggle();
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
                uploadImageToFBStorage();

            } else {
                progressBarToggle();
                // If sign in fails, display a message to the user.
                Log.w("UpdateActivity", "Creation:failed", task.getException());
                Toast.makeText(UpdateActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void uploadImageToFBStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a Cloud Storage reference from the app
        StorageReference storageRef = storage.getReference();

        // Create a reference to "mountains.jpg"
        StorageReference imgRef = storageRef
                .child(firebaseUser.getUid())
                .child(selectedImageUri.getLastPathSegment());

        UploadTask uploadTask = imgRef.putFile(selectedImageUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    progressBarToggle();
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    updateItemImage(downloadUri);
                } else {
                    // Handle failures
                    // ...
                    progressBarToggle();
                }
            }
        });
    }

    private void updateItemImage(Uri storageURL) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference(FBConstants.FB_KEY_USERS)
                .child(firebaseUser.getUid())
                .child(FBConstants.FB_KEY_ITEMS)
                .child(imItemOld.getItemId())
                .child(FBConstants.FB_KEY_ITEMS_ITEM_IMG);

        myRef.setValue(storageURL.toString()).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("MainAct", "upload Successfully done");
                progressBarToggle();

            } else {
                progressBarToggle();
                // If sign in fails, display a message to the user.
                Log.w("MainAct", "Upload img Failed.", task.getException());
                Toast.makeText(UpdateActivity.this, "Upload img Failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getImageUrl() {
        return "https://fastech-racing.com/images/magictoolbox_cache/cf3e6ec01aac7cb79461bcfe9d0d075e/2/2/2259/thumb400x400/4008162058/skm_lipseal.jpg";
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