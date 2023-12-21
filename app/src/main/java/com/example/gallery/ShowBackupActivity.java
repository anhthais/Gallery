package com.example.gallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gallery.adapter.BackupImageAdapter;
import com.example.gallery.object.BackupImage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowBackupActivity extends AppCompatActivity {
    FloatingActionButton fab;
    GridView gridView;
    ArrayList<BackupImage> dataList;
    BackupImageAdapter adapter;
    private DatabaseReference databaseReference ;
    SharedPreferences storedKey ;
    String key = "";
    Toolbar topBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_backup);
        // Hide actionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        fab = findViewById(R.id.fab);
        gridView = findViewById(R.id.gridView);
        topBar = findViewById(R.id.topAppBar);

        dataList = new ArrayList<>();
        adapter = new BackupImageAdapter(dataList,this);
        gridView.setAdapter(adapter);

        storedKey = this.getSharedPreferences("UserCloud-key", Context.MODE_PRIVATE);

        if(storedKey.contains("ID")){
            key = storedKey.getString("ID",null);
            databaseReference = FirebaseDatabase.getInstance().getReference("Users/"+key);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                        BackupImage dataClass = dataSnapshot.getValue(BackupImage.class);
                        dataList.add(dataClass);

                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowBackupActivity.this, UploadFirebaseActivity.class);
                startActivity(intent);
                finish();
            }
        });
        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowBackupActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}