package com.ahemdsiyabi.inventarymangemnt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ahemdsiyabi.inventarymangemnt.mypackage.FBConstants;
import com.ahemdsiyabi.inventarymangemnt.mypackage.IMAdapter;
import com.ahemdsiyabi.inventarymangemnt.mypackage.IMItem;
import com.ahemdsiyabi.inventarymangemnt.mypackage.OnItemClickedCallback;
import com.ahemdsiyabi.inventarymangemnt.mypackage.OnItemDeleteCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MAIN = "TAG_MAIN";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    private RecyclerView rvItems;
    private IMAdapter mAdapter;
    private ArrayList<IMItem> imItemArrayList;
    private AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        imItemArrayList = new ArrayList<>();
        mAdapter = new IMAdapter();
        rvItems = findViewById(R.id.rvItems);

        FloatingActionButton fabAddItem = findViewById(R.id.fabAddItem);
        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToAddActivity();
            }
        });

        readItemsFromFB();

        setupRecyclerView();


    }

    private void readItemsFromFB() {

        // Read from the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FBConstants.FB_KEY_USERS)
                .child(firebaseUser.getUid())
                .child(FBConstants.FB_KEY_ITEMS);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mAdapter.reset();

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    IMItem value = d.getValue(IMItem.class);
                    assert value != null;
                    mAdapter.addItems(value);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG_MAIN, "Failed to read value.", error.toException());
            }
        });
    }


    private void setupRecyclerView() {
        mAdapter.addItems(imItemArrayList);
        // Attach the adapter to the recyclerview to populate items
        rvItems.setAdapter(mAdapter);

        //set on itemClickListener
        mAdapter.setOnItemClicked(new OnItemClickedCallback() {
            @Override
            public void onItemClicked(IMItem item) {
                moveToUpdateActivity(item);
            }
        });

        //set on itemDeleteClickListener
        mAdapter.setOnItemDeleteClicked(new OnItemDeleteCallback() {
            @Override
            public void onItemDeleteClicked(IMItem item) {
                checkQTY(item);
            }
        });

        // Set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvItems.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(this, layoutManager.getOrientation());
        rvItems.addItemDecoration(divider);
    }


    private void moveToAddActivity() {
        Intent i = new Intent(MainActivity.this, AddActivity.class);
        startActivity(i);
    }

    private void moveToUpdateActivity(IMItem imItem) {
        Intent i = new Intent(MainActivity.this, UpdateActivity.class);
        i.putExtra(FBConstants.EXTRA_KEY_ITEM, imItem);
        startActivity(i);
    }

    private void checkQTY(IMItem item) {

        int qty = Integer.parseInt(item.getItemQTY());
        if (qty > 1) {
            String newQTY = String.valueOf(--qty);
            item.setItemQTY(newQTY);
            updateItemQTY(item);
        } else {
            showDeleteDialog(item);
        }

    }

    private void updateItemQTY(IMItem item) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database
                .getReference(FBConstants.FB_KEY_USERS)
                .child(firebaseUser.getUid())
                .child(FBConstants.FB_KEY_ITEMS)
                .child(item.getItemId())
                .child(FBConstants.FB_KEY_ITEMS_ITEM_QTY);

        myRef.setValue(item.getItemQTY()).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("MainAct", "Created Successfully");


            } else {
                // If sign in fails, display a message to the user.
                Log.w("MainAct", "Creation:failed", task.getException());
                Toast.makeText(MainActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog(IMItem imItem) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("do you really want to delete " + imItem.getItemName());
        builder.setTitle("Delete Item Alert");
        builder.setCancelable(false);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteItemFromFB(imItem);
            }
        });

        alert = builder.create();
        alert.show();
    }

    private void deleteItemFromFB(IMItem imItem) {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FBConstants.FB_KEY_USERS)
                .child(firebaseUser.getUid())
                .child(FBConstants.FB_KEY_ITEMS)
                .child(imItem.getItemId());

        myRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("MainActivity", "Delete Successfully");
                alert.dismiss();

            } else {
                // If sign in fails, display a message to the user.
                Log.w("MainActivity", "Delete:failure", task.getException());
                Toast.makeText(MainActivity.this, "Delete failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}