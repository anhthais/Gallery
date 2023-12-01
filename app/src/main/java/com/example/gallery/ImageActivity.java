package com.example.gallery;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.gallery.fragment.EditImageFragment;
import com.example.gallery.fragment.ImageViewFragment;
import com.example.gallery.object.Image;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ImageActivity extends AppCompatActivity implements MainCallBacks {
    private ArrayList<Image> images;
    private ArrayList<String> album_names;
    private ArrayList<String> fav_img_names;
    private int curPos;
    BottomNavigationView bottomNavigation;
    Intent intent_image;
    Uri uri;
    Toolbar tool_bar;
    private ImageViewFragment imageViewFragment;
    private String dataSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: display cutouts [backlog]
        setContentView(R.layout.activity_image);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        intent_image = intent;
        images = intent.getParcelableArrayListExtra("images");
        curPos = intent.getIntExtra("curPos", 0);
        Gson gson = new Gson();
        String album_arr = intent.getStringExtra("ALBUM-LIST");
        album_names = gson.fromJson(album_arr, new TypeToken<ArrayList<String>>() {
        }.getType());
        gson = new Gson();
        fav_img_names = new ArrayList<>();
        String fav_arr = intent.getStringExtra("FAV-IMG-LIST");


        fav_img_names = gson.fromJson(fav_arr, new TypeToken<ArrayList<String>>() {
        }.getType());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //getApplicationContext--> imageActivity.this
        ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images, album_names, fav_img_names, curPos);
        ft.replace(R.id.pictureFragment, imageViewFragment);
        ft.commit();
        //bottomNavigation=findViewByIzd(R.id.navigation_bar_picture);
        //handleBottomNavigation();
        //tool_bar=findViewById(R.id.topAppBar);
        //handleToolBar();
    }


    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if (sender == "EDIT-PHOTO") {
            Toast.makeText(this, "Edit Feature Pos: " + strValue, Toast.LENGTH_SHORT).show();
            curPos = Integer.valueOf(strValue);

            try {
                uri = Uri.parse(images.get(Integer.valueOf(strValue)).getPath().toString());


                if (uri != null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    //getApplicationContext--> imageActivity.this
                    EditImageFragment editImageFragment = new EditImageFragment(ImageActivity.this, uri.toString(), Integer.valueOf(strValue));
                    ft.replace(R.id.pictureFragment, editImageFragment);
                    ft.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (sender == "CUT-ROTATE") {
            Uri fileUri = Uri.fromFile(new File(images.get(Integer.valueOf(strValue)).getPath().toString()));
            if (uri != null) {
                UCrop.Options options = new UCrop.Options();

                String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                        .withOptions(options)
                        .withAspectRatio(0, 0)
                        .useSourceImageAspectRatio()
                        .withMaxResultSize(2000, 2000)
                        .start(ImageActivity.this);

            }


        }

        if (sender == "RETURN-IMAGE-VIEW") {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //getApplicationContext--> imageActivity.this
            ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images, album_names, fav_img_names, Integer.valueOf(strValue));
            ft.replace(R.id.pictureFragment, imageViewFragment);
            ft.commit();

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==UCrop.REQUEST_CROP) {

            final Uri outputUri = UCrop.getOutput(data);
            Bitmap finalBitmap = BitmapFactory.decodeFile(outputUri.getPath());
            // handle the result uri as you want, such as display it in an imageView;
            String path = images.get(curPos).getPath();
            String pathWithoutFilename = path.substring(0, path.lastIndexOf("/"));

            try {
                // Create a new File object for the output file
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String date = dateFormat.format(currentDate);
                String fname = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                File file = new File(pathWithoutFilename, fname);

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                String pathImage =  pathWithoutFilename +"/"+ fname;

                Image editedImage = new Image(pathImage,date);
                images.add(editedImage);
                curPos = images.size() - 1;
                dataSend = pathImage;

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //getApplicationContext--> imageActivity.this
                EditImageFragment editImageFragment = new EditImageFragment(ImageActivity.this,pathImage,curPos);
                ft.replace(R.id.pictureFragment, editImageFragment); ft.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if(resultCode == UCrop.RESULT_ERROR){
            final Throwable cropError = UCrop.getError(data);
        }
    }
}