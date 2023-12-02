package com.example.gallery.helper;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DownLoadImage extends AsyncTask<String,Void,Void> {
    private Context context;
    private boolean saved=true;
    public DownLoadImage(Context context){
        this.context=context;
    }
    @Override
    protected Void doInBackground(String... strings) {
        if(!isNetworkAvailable()){
            saved=false;
            return null;
        }

        URL url=null;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            saved=false;
            throw new RuntimeException(e);
        }
        Bitmap bm=null;
        try{
            bm= BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            saved=false;
            new RuntimeException(e);
        }
        String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        dcimPath+="/Download";
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
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out); // Compress Image
            out.flush();
            out.close();
            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(context,new String[] { newImage.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    // Log.i("ExternalStorage", "Scanned " + path + ":");
                    //    Log.i("ExternalStorage", "-> uri=" + uri);
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context,(saved)? "Saved":"Cannot save, maybe no internet", Toast.LENGTH_SHORT).show();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

