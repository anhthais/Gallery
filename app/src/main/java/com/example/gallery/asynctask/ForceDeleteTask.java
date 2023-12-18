package com.example.gallery.asynctask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.object.TrashItem;

import java.io.File;
import java.util.ArrayList;

public class ForceDeleteTask extends AsyncTask<ArrayList<TrashItem>, Integer, Void> {
    private Context context;
    private AlertDialog dialog = null;
    private TextView progressMessage = null;
    private TextView progressText = null;
    private int total = 0;
    private ArrayList<String> deletedPaths = null;

    public ForceDeleteTask(Context context) {
        this.context = context;
        View progressDialogBox = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        progressMessage = progressDialogBox.findViewById(R.id.loading_msg);
        progressText = progressDialogBox.findViewById(R.id.loading_status);
        progressMessage.setText(R.string.deleteFromTrash);
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
            String oldPath = trashItems.get(i).getPath();
            File oldFile = new File(trashItems.get(i).getPath());
            try {
                oldFile.delete();
                editor.remove(oldPath);
                editor.apply();
                deletedPaths.add(oldPath);
                publishProgress((Integer)i);
            } catch (Exception e) {
                Log.d("ERROR create/copy file", "path: " + trashItems.get(i).getPath());
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
        Toast.makeText(context, R.string.permannently_delete, Toast.LENGTH_SHORT).show();
        if(context instanceof MainActivity) {
            ((MainActivity) context).updateViewManually = false;
        }
    }

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
