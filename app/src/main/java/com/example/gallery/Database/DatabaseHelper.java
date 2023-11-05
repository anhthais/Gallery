package com.example.gallery.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final  String DATABASE_NAME = "GalleryDB.db";
    private static final int DATABASE_VERSION = 1;

    // Create table with colum below
    private static final String TABLE_NAME = "Images";
    private static final String COLUM_ID = "ID";
    private static final String COLUM_NAME= "Name";
    private static final String COLUM_ADDRESS= "Address";
    private static final String COLUM_ALBUM= "Album_Name";
    private static final String COLUM_STATUS= "Status";
    private static final String COLUM_TIME_REMAINING = "Time_Remaining";



    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    // sql schema
    // CREATE TABLE Images ( ID  INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT,
    //                      Address TEXT, Album_Name TEXT, Status TEXT,TimeRemaining TEXT);
    @Override
    public void onCreate(SQLiteDatabase db) {
        // ĐIỀU QUAN TRỌNG NHẮC LẠI 100 LẦN LÀ ĐỪNG ĐỤNG ĐẾN KHOẢNG TRẮNG TRONG CÂU QUERY BÊN DƯỚI
        // LÒI BUG ĐÓ  2(` > `)2
        String query =
                "CREATE TABLE "+ TABLE_NAME +
                        " ("+ COLUM_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        COLUM_NAME + " TEXT, "+
                        COLUM_ADDRESS + " TEXT, "+
                        COLUM_ALBUM + " TEXT, "+
                        COLUM_STATUS + " TEXT, "+
                        COLUM_TIME_REMAINING + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    void addImage(String name, String address, String album_name, String status, String timeRemaining){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUM_NAME,name);
        cv.put(COLUM_ADDRESS,address);
        cv.put(COLUM_ALBUM,album_name);
        cv.put(COLUM_STATUS,status);
        cv.put(COLUM_TIME_REMAINING,timeRemaining);

        long result = db.insert(TABLE_NAME,null,cv);
        if(result == -1){
            Toast.makeText(context,"Added image Failed !",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Added image Successfully !",Toast.LENGTH_SHORT).show();
        }


    }
    public Cursor readAllData(){
        String query = "SELECT * FROM "+TABLE_NAME ;
        SQLiteDatabase db  = this.getReadableDatabase();
        Cursor cursor = null;
        if(db != null){
            db.rawQuery(query,null);
        }
        return  cursor;

    }


    // Use it in activity will update image

    // getAndSetIntentData();
    // DatabaseHelper galleryBD = new DatabaseHelper( [context needed]);

    // galleryBD.updateData([id, name, address, albumName, status,timeRemaing] those parameter get in update acction );
    void updateData(String row_id, String name, String address, String album_name, String status, String timeRemaining){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUM_NAME,name);
        cv.put(COLUM_ADDRESS,address);
        cv.put(COLUM_ALBUM,album_name);
        cv.put(COLUM_STATUS,status);
        cv.put(COLUM_TIME_REMAINING,timeRemaining);

        long result = db.update(TABLE_NAME,cv,"ID=?", new String[]{row_id});

        if(result == -1){
            Toast.makeText(context,"Updated image Failed !",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Updated image Successfully !",Toast.LENGTH_SHORT).show();
        }

    }


    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME,"ID=?",new String[]{row_id});

        if(result == -1){
            Toast.makeText(context,"Deleted image Failed !",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context,"Deleted image Successfully !",Toast.LENGTH_SHORT).show();
        }
    }

}
