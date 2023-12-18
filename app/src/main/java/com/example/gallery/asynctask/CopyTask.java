package com.example.gallery.asynctask;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gallery.R;
import com.example.gallery.object.Album;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class CopyTask extends AsyncTask<ArrayList<String>, Integer, Void> {
    private Context context;
    private Album album;
    private AlertDialog dialog = null;
    private TextView progressMessage = null;
    private TextView progressText = null;
    private String cannotAdd = "";
    private String sameName = "";
    private int total = 0;

    public CopyTask(Context context, Album album) {
        this.context = context;
        this.album = album;
        View progressDialogBox = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        progressMessage = progressDialogBox.findViewById(R.id.loading_msg);
        progressText = progressDialogBox.findViewById(R.id.loading_status);
        progressMessage.setText(R.string.copyMultiToAlbum);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(progressDialogBox);
        dialog = builder.create();
    }

    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected Void doInBackground(ArrayList<String> ... params) {
        ArrayList<String> paths = params[0];
        this.total = paths.size();
        for (int i = 0; i < paths.size(); ++i) {
            String filename = paths.get(i).substring(paths.get(i).lastIndexOf("/") + 1);
            Path source = Paths.get(paths.get(i));
            Path dest = Paths.get(album.getPath(), filename);
            File file = new File(dest.toString());
            try {
                file.createNewFile();
            } catch (IOException e) {
                cannotAdd += filename + "; ";
                continue;
            }
            try {
                Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
                publishProgress((Integer) i);
            } catch (IOException e) {
                sameName += filename + "; ";
                try{
                    file.delete();
                } catch (Exception ex){

                }
            }
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        String updateStr = progress[0].toString() + "/" + String.valueOf(total);
        progressText.setText(updateStr);
    }

    protected void onPostExecute(Void result) {
        dialog.dismiss();
        if(!cannotAdd.equals("")){
            Toast.makeText(context, context.getString(R.string.cannot_add) + ": " + cannotAdd, Toast.LENGTH_SHORT).show();
        }
        Log.d("cannotadd", cannotAdd);
        Log.d("samename", sameName);
        if(!sameName.equals("")){
            Toast.makeText(context, context.getString(R.string.existed_cannot_add) + ": " + sameName, Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context, R.string.add_to_album_success, Toast.LENGTH_SHORT).show();
    }
}
