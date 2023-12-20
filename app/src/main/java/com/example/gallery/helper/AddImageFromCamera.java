package com.example.gallery.helper;

import static android.graphics.Bitmap.CompressFormat.JPEG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class AddImageFromCamera extends AsyncTask<Bitmap,Void,Void> {
    private Context context;
    private boolean saved=true;
    public AddImageFromCamera(Context context){
        this.context=context;
    }
    @Override
    protected Void doInBackground(Bitmap... bitmaps) {
        Bitmap bm=null;
        try{
            bm= bitmaps[0];
        } catch (Exception e) {

            new RuntimeException(e);
        }
        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        dcimPath+="/Camera";
        File path=new File(dcimPath);
        if(!path.exists()){
            path.mkdir();
        }
        File newImage=new File(path,String.valueOf(System.currentTimeMillis())+".jpg");

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(newImage);
        } catch (FileNotFoundException e) {
            saved=false;
            e.printStackTrace();
        }
        try{
            bm.compress(JPEG,100 ,out); // Compress Image
            out.flush();
            out.close();
            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context,new String[] { newImage.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } catch(Exception e) {
            saved=false;
            if(newImage.exists()){
                newImage.delete();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
    }
}
