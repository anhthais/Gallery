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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.gallery.fragment.EditImageFragment;
import com.example.gallery.fragment.ImageViewFragment;
import com.example.gallery.helper.DateConverter;
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
    private int curPos;
    private Uri tempEdited;
    Intent intent_image;
    Uri uri;
    private FragmentCallBacks callback;
    public ImageViewFragment imageViewFragment = null;

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
        Gson gson = new Gson();
        String album_arr = intent.getStringExtra("ALBUM-LIST");
        album_names = gson.fromJson(album_arr, new TypeToken<ArrayList<String>>(){}.getType());

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //getApplicationContext--> imageActivity.this
        ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images, album_names, curPos);
        ft.replace(R.id.pictureFragment, imageViewFragment);
        ft.commit();
    }

    public void updateImageViewFragment(ImageViewFragment frag){
        Gson gson = new Gson();
        // update images list
        curPos = frag.imageViewPager2.getCurrentItem();
        // set result for main activity
        frag.deletePos.add(frag.images.get(curPos).getIdInMediaStore());
        frag.deleteTime.add((new Date()).getTime());
        frag.newDeletedImagePath.add(frag.newPath);
        images.remove(curPos);
        if (curPos == images.size()) {
            curPos--;
        } else curPos++;
        //update viewpagerAdapter
        frag.imageViewPager2.setCurrentItem(curPos, false);
        frag.imageViewPager2.getAdapter().notifyDataSetChanged();

        Intent intent = getIntent();
        intent.putExtra("addDelete", gson.toJson(frag.deletePos));
        intent.putExtra("addDeleteTime", gson.toJson(frag.deleteTime));
        intent.putExtra("addDeleteNewPath", gson.toJson(frag.newDeletedImagePath));
        setResult(AppCompatActivity.RESULT_OK, intent);

        SharedPreferences myPref = getSharedPreferences("TRASH", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        ArrayList<String> data = new ArrayList<>(2);
        data.add(frag.oldPath);
        Date dateExpires = DateConverter.plusMinutes(new Date(), 10);
        data.add(DateConverter.longToString(dateExpires.getTime()));
        editor.putString(frag.newPath, gson.toJson(data));
        editor.apply();

        if (images.size() == 0) {
            finish();
        }
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if(sender.equals("EDIT-PHOTO")){
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
        else if(sender.equals("RETURN-IMAGE-VIEW")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //getApplicationContext--> imageActivity.this
            ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images,album_names, curPos);
            ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();
        }
        else if(sender.equals("SAVE-EDITED-IMAGE")) {
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
            ImageViewFragment imageViewFragment = new ImageViewFragment(ImageActivity.this, images,album_names ,curPos);
            ft.replace(R.id.pictureFragment, imageViewFragment); ft.commit();
        }
    }

    @Override
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
        else if(resultCode == AppCompatActivity.RESULT_OK && requestCode == 1){
            if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                getContentResolver().delete(ContentUris.withAppendedId(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), Long.valueOf(imageViewFragment.images.get(imageViewFragment.imageViewPager2.getCurrentItem()).getIdInMediaStore())), null, null);
            }
            updateImageViewFragment(imageViewFragment);
        }
    }

}