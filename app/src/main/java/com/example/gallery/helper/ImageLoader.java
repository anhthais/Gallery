package com.example.gallery.helper;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import com.example.gallery.object.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.LogRecord;

public class ImageLoader extends AsyncTaskLoader<ArrayList<Image>> {
    private ArrayList<Image> mImages;
    private ContentObserver mObserver;
    private int sortOrder = -1;

    public ImageLoader(Context context) {
        super(context);
        this.sortOrder = sortOrder;
    }

    @Override
    protected void onStartLoading() {
        if (mImages != null) {
            deliverResult(mImages);
        }

        if (mObserver == null) {
            mObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    onContentChanged();
                }
            };
            getContext().getContentResolver().registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    true,
                    mObserver);
        }

        if (takeContentChanged() || mImages == null) {
            forceLoad();
        }
    }

    @Override
    public ArrayList<Image> loadInBackground() {
        ArrayList<Image> images = new ArrayList<>();
        String[] PROJECTION = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_TAKEN,
        };
        String ORDER_BY = MediaStore.Images.Media.DATE_ADDED + " DESC";
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        try (Cursor cursor = getContext().getContentResolver().query(uri, PROJECTION, null, null, ORDER_BY)) {

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

                    Image image = new Image(absolutePath, dateAdded, id);

                    if (!image.getPath().isEmpty()) {
                        images.add(image);
                        Log.d("Path", image.getPath());
                        Log.d("NumOfImages", String.valueOf(images.size()));
                    }
                    // just for running the app
                    // I don't know how to set pagination for loading images now 😭
                    if (images.size() >= 1000) break;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("LocalStorageReader", "Error getting images from local storage", e);
            throw new RuntimeException("Error getting images from local storage", e);
        }
        // Close cursor if not null
        return images;
    }

    @Override
    public void deliverResult(ArrayList<Image> data) {
        mImages = data;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        // Stop loading and clear the cached data
        onStopLoading();
        if (mImages != null) {
            mImages = null;
        }

        // Unregister the content observer
        if (mObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(mObserver);
            mObserver = null;
        }
    }
}
