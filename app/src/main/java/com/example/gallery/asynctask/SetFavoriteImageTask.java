package com.example.gallery.asynctask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.example.gallery.object.Image;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SetFavoriteImageTask extends AsyncTask<ArrayList<Image>, Void, Void> {
    private Context context;
    public SetFavoriteImageTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(ArrayList<Image>... params) {
        ArrayList<Image> allImages = params[0];
        Gson gson = new Gson();
        SharedPreferences myPref = context.getSharedPreferences("GALLERY", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        ArrayList<Long> favIds = new ArrayList<>();
        for(int i = 0; i < params[0].size(); ++i){
            if(allImages.get(i).isFavorite()){
                favIds.add(allImages.get(i).getIdInMediaStore());
            }
        }
        editor.putString("FAVORITE", gson.toJson(favIds));
        editor.apply();
        return null;
    }
}