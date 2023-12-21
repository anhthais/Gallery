package com.example.gallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gallery.fragment.AddImageToAlbumFragment;
import com.example.gallery.fragment.EditImageFragment;
import com.example.gallery.fragment.FilterImageFragment;
import com.example.gallery.fragment.HidePagerFragment;
import com.example.gallery.fragment.ImageViewFragment;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ImageActivity extends AppCompatActivity implements MainCallBacks{
    public ArrayList<Image> images;
    public ArrayList<Album> album_list;
    private int curPos;
    private Uri tempEdited;
    Intent intent_image;
    Uri uri;
    public ArrayList<Long> addLocation;
    public ArrayList<Long> removeLocation;

    public ImageViewFragment imageViewFragment = null;

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
        album_list=intent.getParcelableArrayListExtra("albums");
        addLocation = new ArrayList<>();
        removeLocation = new ArrayList<>();
        String hide = intent.getStringExtra("TYPE");
        if(hide!=null && hide.equals("hide")){
            HidePagerFragment hidePagerFragment = new HidePagerFragment(ImageActivity.this, images, curPos);
            getSupportFragmentManager().beginTransaction().replace(R.id.pictureFragment,hidePagerFragment).commit();
        }else{
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //getApplicationContext--> imageActivity.this
            ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images, album_list, curPos);
            ft.replace(R.id.pictureFragment, imageViewFragment);
            ft.commit();
        }
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if(sender.equals("EDIT-PHOTO")){
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
        else if(sender.equals("CUT-ROTATE")){
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
        else if(sender.equals("FILTER")){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] decodedString = Base64.decode(strValue, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 100,bytes);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            FilterImageFragment filterImageFragment = new FilterImageFragment(ImageActivity.this,decodedBitmap);
            ft.replace(R.id.pictureFragment,filterImageFragment); ft.commit();

        }
        else if(sender.equals("FILTER-RETURN")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            EditImageFragment editImageFragment = new EditImageFragment(ImageActivity.this,strValue,curPos);
            ft.replace(R.id.pictureFragment, editImageFragment); ft.commit();
        }
        else if(sender.equals("RETURN-IMAGE-VIEW")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //getApplicationContext--> imageActivity.this
            ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images,album_list, curPos);
            ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();
        }
        else if(sender.equals("SAVE-EDITED-IMAGE")) {
            byte[] decodeString = Base64.decode(strValue, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);

            String path = images.get(curPos).getPath();
            String pathWithoutFilename = path.substring(0, path.lastIndexOf("/"));

            try {
                // Create a new File object for the output file
                String fname = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
                File file = new File(pathWithoutFilename, fname);

                try {
                    FileOutputStream out = new FileOutputStream(file);
                    decodedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    Toast.makeText(this,R.string.save_edit_success,Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                String pathImage = pathWithoutFilename + "/" + fname;

                Image editedImage = new Image(pathImage, new Date().getTime());
                images.add(editedImage);
                curPos = images.size() - 1;
                dataSend = pathImage;

            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //getApplicationContext--> imageActivity.this
            ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images,album_list ,curPos);
            ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();
        }else if(sender.equals("ADD-TO-ALBUM")){
            ArrayList<String> paths = new ArrayList<>();
            paths.add(strValue);
            AddImageToAlbumFragment albumFragment = new AddImageToAlbumFragment(this,album_list,paths) ;
            getSupportFragmentManager().beginTransaction().replace(R.id.pictureFragment,albumFragment).addToBackStack(null).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==4123)
        {
            Intent intent = getIntent();
            Gson gson = new Gson();

            if (resultCode==RESULT_OK)
            {
                curPos = data.getIntExtra("curPos",-1);
                Double latitude = data.getDoubleExtra("latitude",-1.0);
                Double longitude = data.getDoubleExtra("longitude",-1.0);
                LatLng latLng = new LatLng(latitude,longitude);
                String stringLocation = null;
                try {
                    stringLocation =GetLocationActivity.getAddressFromLatLng(getApplicationContext(), latLng.latitude, latLng.longitude);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (stringLocation ==null)
                {
                    String value = latLng.latitude + "," + latLng.longitude;
                    images.get(curPos).setLocation(latLng, value);
                }
                else {
                    images.get(curPos).setLocation(latLng,stringLocation);
                }

                Toast.makeText(this, R.string.addLocationSuccess, Toast.LENGTH_SHORT).show();
                if (!addLocation.contains(images.get(curPos).getIdInMediaStore()))
                {
                    addLocation.add(images.get(curPos).getIdInMediaStore());
                }
                removeLocation.remove(Long.valueOf(images.get(curPos).getIdInMediaStore()));
                intent.putExtra(images.get(curPos).getIdInMediaStore()+ "-STRINGLOCATION",images.get(curPos).getStringLocation());
                intent.putExtra(images.get(curPos).getIdInMediaStore()+"-LATITUDE",images.get(curPos).getLocation().latitude);
                intent.putExtra(images.get(curPos).getIdInMediaStore()+"-LONGITUDE",images.get(curPos).getLocation().longitude);

                SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();
                ArrayList<Long> imagesLocationList = new ArrayList<>();
                for(int i = 0; i < images.size(); ++i){
                    if(images.get(i).getLocation()!= null){
                        imagesLocationList.add(Long.valueOf(images.get(i).getIdInMediaStore()));
                    }
                }
                editor.putFloat(images.get(curPos).getIdInMediaStore() + "-LATITUDE", (float) images.get(curPos).getLocation().latitude);
                editor.putFloat(images.get(curPos).getIdInMediaStore() + "-LONGITUDE", (float) images.get(curPos).getLocation().longitude);
                editor.putString(images.get(curPos).getIdInMediaStore() + "-STRINGLOCATION",(String) images.get(curPos).getStringLocation());
                editor.putString("IMAGES-ID-LOCATION", gson.toJson(imagesLocationList));
                editor.apply();

            }
            else if (resultCode==RESULT_CANCELED)
            {
                curPos = data.getIntExtra("curPos",-1);
                images.get(curPos).setLocation(null,null);
                Toast.makeText(this, R.string.onCancelLocation, Toast.LENGTH_SHORT).show();
                if (!removeLocation.contains(images.get(curPos).getIdInMediaStore()))
                {
                    removeLocation.add(images.get(curPos).getIdInMediaStore());
                }
                addLocation.remove(Long.valueOf(images.get(curPos).getIdInMediaStore()));
                intent.removeExtra(images.get(curPos).getIdInMediaStore()+ "-STRINGLOCATION");
                intent.removeExtra(images.get(curPos).getIdInMediaStore()+"-LATITUDE");
                intent.removeExtra(images.get(curPos).getIdInMediaStore()+"-LONGITUDE");
                SharedPreferences myPref = getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = myPref.edit();
                ArrayList<Long> imagesLocationList = new ArrayList<>();
                for(int i = 0; i < images.size(); ++i){
                    if(images.get(i).getLocation()!= null){
                        imagesLocationList.add(Long.valueOf(images.get(i).getIdInMediaStore()));
                    }
                }
                editor.remove(images.get(curPos).getIdInMediaStore() + "-LATITUDE");
                editor.remove(images.get(curPos).getIdInMediaStore() + "-LONGITUDE");
                editor.remove(images.get(curPos).getIdInMediaStore() + "-STRINGLOCATION");
                editor.putString("IMAGES-ID-LOCATION", gson.toJson(imagesLocationList));
                editor.apply();
            } else if (resultCode==-2) {
                //Nothing to do, just return image view
            }
            intent.putExtra("addLocation", gson.toJson(addLocation));
            intent.putExtra("removeLocation", gson.toJson(removeLocation));

            setResult(AppCompatActivity.RESULT_OK, intent);
        }
        else
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
        else if(resultCode == AppCompatActivity.RESULT_OK && requestCode == 1){
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                getContentResolver().delete(ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), Long.valueOf(imageViewFragment.images.get(imageViewFragment.imageViewPager2.getCurrentItem()).getIdInMediaStore())), null, null);
            }
            imageViewFragment.saveDeleteAndUpdateView();
        }
    }

}