package com.example.gallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView btnv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnv=findViewById(R.id.navigationBar);
        btnv.setOnNavigationItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.btnGallery){

            }
            else if (R.id.btnAlbum==id){

            }
            else if (R.id.btnSettings==id){
                View v=findViewById(R.id.btnSettings);
                PopupMenu pm = new PopupMenu(this, v);
                pm.getMenuInflater().inflate(R.menu.settings_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return true;
                    }
                }); pm.show();
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

}