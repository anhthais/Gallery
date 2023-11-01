package com.example.gallery.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.gallery.object.Image;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class LocalStorageReader {
    // Constant fields for projection and sort order
    private static final String[] PROJECTION = {
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
    };
    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";

    // Method to get images from local storage
    public static final ArrayList<Image> getImagesFromLocal(Context context) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        ArrayList<Image> images = new ArrayList<>();

        try {
            cursor = context.getApplicationContext().getContentResolver().query(uri, PROJECTION, null, null, ORDER_BY);

            // Check if cursor is not null and move to first row
            if (cursor != null && cursor.moveToFirst()) {
                // Get data
//                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                int dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);

                // Create a calendar and a formatter for date conversion
                Calendar myCal = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                do {
                    // Get image data from cursor
                    String absolutePath = cursor.getString(columnIndex);
                    File file = new File(absolutePath);
                    if (!file.canRead()) {
                        continue;
                    }
                    Long dateTaken = cursor.getLong(dateIndex);

                    // Convert date taken to string format
                    myCal.setTimeInMillis(dateTaken);
                    String dateText = formatter.format(myCal.getTime());

                    // Create an image object and set its fields
                    Image image = new Image(absolutePath, dateText);

                    if (!image.getPath().isEmpty()) {
                        images.add(image);
                        Log.d("Path", image.getPath());
                        Log.d("NumOfImages", String.valueOf(images.size()));
                    }

                    if(images.size() >= 100) break; // for testing
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

        // Return list of images

        return images;
    }
}
