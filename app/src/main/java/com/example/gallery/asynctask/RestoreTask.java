package com.example.gallery.asynctask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.object.Image;
import com.example.gallery.object.TrashItem;
import com.google.gson.Gson;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RestoreTask extends AsyncTask<ArrayList<TrashItem>, Integer, Void> {
    private Context context;
    private AlertDialog dialog = null;
    private TextView progressMessage = null;
    private TextView progressText = null;
    private ArrayList<String> deletedPaths = null;
    private int total = 0;

    public RestoreTask(Context context) {
        this.context = context;
        View progressDialogBox = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        progressMessage = progressDialogBox.findViewById(R.id.loading_msg);
        progressText = progressDialogBox.findViewById(R.id.loading_status);
        progressMessage.setText(R.string.restoreFromTrash);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(progressDialogBox);
        dialog = builder.create();
    }

    protected void onPreExecute() {
        dialog.show();
        //
        if(context instanceof MainActivity) {
            ((MainActivity) context).updateViewManually = true;
        }
        //
    }

    @Override
    protected Void doInBackground(ArrayList<TrashItem>... params) {
        ArrayList<TrashItem> trashItems = params[0];
        this.total = trashItems.size();
        deletedPaths = new ArrayList<>();
        SharedPreferences myPref = context.getSharedPreferences("TRASH", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        for (int i = 0; i < trashItems.size(); ++i) {
            String oldPathStr = trashItems.get(i).getPath();
            String newPathStr = trashItems.get(i).getPrevPath();
            Path oldPath = Paths.get(oldPathStr);
            Path newPath = Paths.get(newPathStr);
            File oldFile = new File(oldPathStr);
            File newFile = new File(newPathStr);
            try {
                if(newFile.createNewFile()){
                    Files.copy(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                    MediaScannerConnection.scanFile(context, new String[]{newPathStr}, null, null);
                    editor.remove(trashItems.get(i).getPath());
                    editor.apply();
                    oldFile.delete();
                    deletedPaths.add(oldPathStr);
                    publishProgress((Integer)i);
                }
            } catch (Exception e) {
                Log.d("ERROR create/copy file", "path: " + trashItems.get(i).getPath());
                if(newFile.delete()){
                    Log.d("ERROR delete error file", "Delete error file");
                }
            }

        }
        updateMainActivity(deletedPaths);
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        String updateStr = progress[0].toString() + "/" + String.valueOf(total);
        progressText.setText(updateStr);
    }

    protected void onPostExecute(Void result) {
        dialog.dismiss();
        Toast.makeText(context, R.string.restore_success, Toast.LENGTH_SHORT).show();
        //
        if(context instanceof MainActivity) {
            ((MainActivity) context).updateViewManually = false;
        }
        //
    }

    // this will make the class less abstractive :((
    public final void updateMainActivity(ArrayList<String> paths){
        Activity activity = (Activity) context;
        if(activity instanceof MainActivity){
            ArrayList<TrashItem> mainTrashItems = ((MainActivity) activity).trashItems;
            for(int i = 0; i < paths.size(); ++i){
                String path = paths.get(i);
                for(int j = 0; j < mainTrashItems.size(); ++j){
                    if(mainTrashItems.get(j).getPath().equals(path)){
                        mainTrashItems.remove(j);
                    }
                }
            }
        }
    }
}
