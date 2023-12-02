package com.example.gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gallery.fragment.EditImageFragment;
import com.example.gallery.fragment.ImageViewFragment;
import com.example.gallery.object.Image;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ImageActivity extends AppCompatActivity implements MainCallBacks{
    private ArrayList<Image> images;
    private ArrayList<String> album_names;
    private ArrayList<String> fav_img_names;
    private int curPos;
    private Uri tempEdited;
    BottomNavigationView bottomNavigation;
    Intent intent_image;
    Uri uri;
    Toolbar tool_bar;
    private FragmentCallBacks callback;


    private String dataSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: display cutouts [backlog]
        setContentView(R.layout.activity_image);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        intent_image=intent;
        images = intent.getParcelableArrayListExtra("images");
        curPos = intent.getIntExtra("curPos", 0);
        Gson gson=new Gson();
        String album_arr=intent.getStringExtra("ALBUM-LIST");
        album_names=gson.fromJson(album_arr, new TypeToken<ArrayList<String>>(){}.getType());
        gson = new Gson();
        fav_img_names = new ArrayList<>();
        String fav_arr= intent.getStringExtra("FAV-IMG-LIST");



        fav_img_names = gson.fromJson(fav_arr,new TypeToken<ArrayList<String>>(){}.getType());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //getApplicationContext--> imageActivity.this
        ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images,album_names,fav_img_names, curPos);
        ft.replace(R.id.pictureFragment, imageViewFragment);
        ft.commit();



    }


    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if(sender == "EDIT-PHOTO"){
            Toast.makeText(this,"Edit Feature Pos: "+strValue,Toast.LENGTH_SHORT).show();
            curPos = Integer.valueOf(strValue);

            try {
                uri = Uri.parse(images.get(Integer.valueOf(strValue)).getPath().toString());


                if (uri!=null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    //getApplicationContext--> imageActivity.this
                    Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    EditImageFragment editImageFragment = new EditImageFragment(ImageActivity.this,encodedBitmap,Integer.valueOf(strValue));
                    ft.replace(R.id.pictureFragment, editImageFragment); ft.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(sender == "CUT-ROTATE"){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            //Uri fileUri = Uri.fromFile(new File(images.get(Integer.valueOf(strValue)).getPath().toString()));
            byte[] decodedString = Base64.decode(strValue, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), decodedBitmap, "Ucrop-Temp", null);
            Uri fileUri = Uri.parse(path);
            tempEdited = fileUri;

            if (uri !=null) {
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

        if(sender == "RETURN-IMAGE-VIEW"){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //getApplicationContext--> imageActivity.this
            ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images,album_names,fav_img_names,curPos);
            ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();

        }

        if(sender == "SAVE-EDITED-IMAGE") {
            byte[] decodeString = Base64.decode(strValue, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);

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
                    decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    Toast.makeText(this,"Successfully save Edited Image",Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                String pathImage = pathWithoutFilename + "/" + fname;

                Image editedImage = new Image(pathImage, date);
                images.add(editedImage);
                curPos = images.size() - 1;
                dataSend = pathImage;

            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //getApplicationContext--> imageActivity.this
            ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images,album_names,fav_img_names,curPos);
            ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();


        }


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==UCrop.REQUEST_CROP) {

            final Uri outputUri = UCrop.getOutput(data);
            Bitmap finalBitmap = BitmapFactory.decodeFile(outputUri.getPath());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

            this.getContentResolver().delete(tempEdited, null, null);


            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //getApplicationContext--> imageActivity.this
            EditImageFragment editImageFragment = new EditImageFragment(ImageActivity.this,encodedBitmap,curPos);
            ft.replace(R.id.pictureFragment, editImageFragment); ft.commit();


        }
        else if(resultCode == UCrop.RESULT_ERROR){
            final Throwable cropError = UCrop.getError(data);
        }
    }


}