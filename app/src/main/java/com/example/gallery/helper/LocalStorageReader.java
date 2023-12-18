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

import androidx.loader.content.CursorLoader;

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
import java.util.Map;

public class LocalStorageReader {
    public static int BY_DATE = 1;
    public static int BY_MONTH = 2;
    public static int BY_YEAR = 3;

    public static ArrayList<ImageGroup> getListImageGroupByDate(ArrayList<Image> imageList) {
        ArrayList<ImageGroup> groupList = new ArrayList<>();
        int count = 0;
        try {
            // group images by taken date, imageList contains images ordered by date DESC
            String dateStr = DateConverter.longToSimpleString(imageList.get(0).getDate());
            groupList.add(new ImageGroup(dateStr, new ArrayList<>()));
            groupList.get(count).addImg(imageList.get(0));

            for (int i = 1; i < imageList.size(); ++i) {
                if (!dateStr.equals(DateConverter.longToSimpleString(imageList.get(i).getDate()))) {
                    dateStr = DateConverter.longToSimpleString(imageList.get(i).getDate());
                    groupList.add(new ImageGroup(dateStr, new ArrayList<>()));
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