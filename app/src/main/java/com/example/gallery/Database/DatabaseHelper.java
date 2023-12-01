package com.example.gallery.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gallery.helper.LocalStorageReader;
import com.example.gallery.object.Image;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// MAIN IDEA:
public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String DATABASE_NAME = "GalleryDB.db";
    private static final int DATABASE_VERSION = 1;

    // Create table with colum below
    private static final String TABLE_NAME = "Images";
    private static final String COLUMN_ID_MEDIASTORE = "ID_MediaStore";
    private static final String COLUMN_DATA = "Data";
    private static final String COLUMN_IS_FAVORITE = "Is_Favorite";
    private static final String COLUMN_DATE_EXPIRES = "Date_Expires";
    private static final String COLUMN_DESCRIPTION = "Description";
    private static final String COLUMN_TAG_NAME = "Tag";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                        " ("+ COLUMN_ID_MEDIASTORE + " INTEGER PRIMARY KEY, " +
                        COLUMN_DATA + " TEXT NOT NULL, " +
                        COLUMN_IS_FAVORITE + " BOOLEAN DEFAULT 0, " +
                        COLUMN_DATE_EXPIRES + " TEXT, " +
                        COLUMN_DESCRIPTION + "TEXT, " +
                        COLUMN_TAG_NAME + "TEXT);";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addImage(long id, String path){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ID_MEDIASTORE, id);
        cv.put(COLUMN_DATA, path);

        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context,"Added image Failed !",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Added image Successfully !",Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

//    public void updateColumnDescription(Image image, String des){
//        SQLiteDatabase db = this.getWritableDatabase();
//        if(image.isFavorite() || image.getDateExpires() != null || image.getTags() != null){
//            ContentValues cv = new ContentValues();
//            cv.put(COLUMN_ID_MEDIASTORE, image.getIdInMediaStore());
//            cv.put(COLUMN_DATA, image.getPath());
//            cv.put(COLUMN_IS_FAVORITE, image.isFavorite());
//            cv.put(COLUMN_DATE_EXPIRES, image.getDateExpires());
//            cv.put(COLUMN_TAG_NAME, image.getTags());
//            cv.put(COLUMN_DESCRIPTION, des);
//
//            int result = (int) db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
//            if (result == -1) {
//                ContentValues description = new ContentValues();
//                description.put(COLUMN_DESCRIPTION, des);
//                db.update(TABLE_NAME, description, COLUMN_ID_MEDIASTORE + "=?", new String[] {String.valueOf(image.getIdInMediaStore())});
//            }
//
//        }
//    }

    // TODO: update later
    void deleteOneRow(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME,COLUMN_ID_MEDIASTORE + "=?",new String[]{String.valueOf(id)});

        if(result == -1){
            Log.d("delete one row db", "Error at " + String.valueOf(id));
        }
    }

    // TODO: asynctask
    // just update data with custom value (isFavorite, date expires, tags, description ...)
//    public void updateFrom(ArrayList<Image> images){
//        SQLiteDatabase db = this.getWritableDatabase();
//        Log.d("before insert data", "before");
//        for(int i = 0; i < images.size(); ++i){
//            Image image = images.get(i);
//            if(image.isFavorite() || image.getDateExpires() != null || image.getTags() != null){
//                ContentValues cv = new ContentValues();
//                cv.put(COLUMN_ID_MEDIASTORE, image.getIdInMediaStore());
//                cv.put(COLUMN_DATA, image.getPath());
//                cv.put(COLUMN_IS_FAVORITE, image.isFavorite());
////                cv.put(COLUMN_TAG_NAME, image.getTags());
//                Log.d("id: ", String.valueOf(image.getIdInMediaStore()));
//                Log.d("data: ", image.getPath());
//                Log.d("isFavorite: ", String.valueOf(image.isFavorite()));
//
//                int result = (int) db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
//                if (result == -1) {
//                    db.update(TABLE_NAME, cv, COLUMN_ID_MEDIASTORE + "=?", new String[] {String.valueOf(image.getIdInMediaStore())});
//                }
//
//            }
//        }
//    }

    public void updateIsFavorite(ArrayList<Image> images){
        SQLiteDatabase db = this.getWritableDatabase();
        for(int i = 0; i < images.size(); ++i){
            Image image = images.get(i);
            if(image.isFavorite()){
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_ID_MEDIASTORE, image.getIdInMediaStore());
                cv.put(COLUMN_IS_FAVORITE, image.isFavorite());

                int result = (int) db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                if (result == -1) {
                    db.update(TABLE_NAME, cv, COLUMN_ID_MEDIASTORE + "=?", new String[] {String.valueOf(image.getIdInMediaStore())});
                }

            }
        }
    }

    // update array with existing data in database
    // TODO: asynctask
    public void updateTo(ArrayList<Image> images){
        try{
            Cursor cursor = this.readAllData();
            if (cursor != null && cursor.moveToFirst()) {
                int idColIdx = cursor.getColumnIndexOrThrow(COLUMN_ID_MEDIASTORE);
                int isFavoriteColIdx = cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE);
                int tagColIdx = cursor.getColumnIndexOrThrow(COLUMN_TAG_NAME);

                Map<Long, Image> map = new HashMap<>();
                for(Image image : images){
                    map.put(image.getIdInMediaStore(), image);
                }

                Calendar myCal = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

                do {
                    long id = cursor.getLong(idColIdx);
                    Image image = map.get(id);

                    if(image != null){
                        Log.d("row", String.valueOf(id));
                        Log.d("is_favorite", String.valueOf(cursor.getInt(isFavoriteColIdx) != 0));
                        Log.d("tags", cursor.getString(tagColIdx));
                        image.setFavorite(cursor.getInt(isFavoriteColIdx) != 0);
                        image.setTags(cursor.getString(tagColIdx));
                    } else {
                        deleteOneRow(id);
                    }

                } while (cursor.moveToNext());
            }
        } catch(Exception e){
            // TODO
        }
    }
}