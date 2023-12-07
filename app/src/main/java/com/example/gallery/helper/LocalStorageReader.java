package com.example.gallery.helper;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.example.gallery.Database.DatabaseHelper;
import com.example.gallery.R;
import com.example.gallery.object.Album;
import com.example.gallery.object.Image;
import com.example.gallery.object.ImageGroup;
import com.example.gallery.object.TrashItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class LocalStorageReader {
    // TODO: multithreading
    public static ArrayList<Image> getImagesFromLocal(Context context) {
        String[] PROJECTION = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
        };
        String ORDER_BY = MediaStore.Images.Media.DATE_ADDED + " DESC";
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        ArrayList<Image> images = new ArrayList<>();

        try {
            cursor = context.getApplicationContext().getContentResolver().query(uri, PROJECTION, null, null, ORDER_BY);

            if (cursor != null && cursor.moveToFirst()) {
                int idColIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int dataColIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int dateAddedColIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

                do {
                    long id = cursor.getLong(idColIdx);
                    String absolutePath = cursor.getString(dataColIdx);
                    File file = new File(absolutePath);
                    if (!file.exists()) {
                        continue;
                    }
                    long dateAdded = cursor.getLong(dateAddedColIdx) * 1000L;
                    String dateString = DateConverter.simpleLongToString(dateAdded);

                    Image image = new Image(absolutePath, dateString, id);

                    if (!image.getPath().isEmpty()) {
                        images.add(image);
                        Log.d("Path", image.getPath());
                        Log.d("NumOfImages", String.valueOf(images.size()));
                    }
//                    if(images.size() >= 100) break; // for testing
                } while (cursor.moveToNext()); // Move to next row
            }
        } catch (Exception e) {
            Log.e("LocalStorageReader", "Error getting images from local storage", e);
            throw new RuntimeException("Error getting images from local storage", e);
        } finally {
            // Close cursor if not null
            if (cursor != null) {
                cursor.close();
            }
        }

        return images;
    }

    public static ArrayList<ImageGroup> getListImageGroupByDate(ArrayList<Image> imageList) {
        ArrayList<ImageGroup> groupList = new ArrayList<>();
        int count = 0;
        try {
            // group images by taken date, imageList contains images ordered by date DESC
            groupList.add(new ImageGroup(imageList.get(0).getDate(), new ArrayList<>()));
            groupList.get(count).addImg(imageList.get(0));

            for (int i = 1; i < imageList.size(); ++i) {
                if (!imageList.get(i).getDate().equals(imageList.get(i - 1).getDate())) {
                    groupList.add(new ImageGroup(imageList.get(i).getDate(), new ArrayList<>()));
                    count++;
                }
                groupList.get(count).addImg(imageList.get(i));
            }

            return groupList;
        } catch (Exception e) {
            Log.e("getListImageGroup", e.toString());
            return new ArrayList<>();
        }
    }

}