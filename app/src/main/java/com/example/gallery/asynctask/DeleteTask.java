package com.example.gallery.asynctask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.ImageActivity;
import com.example.gallery.MainActivity;
import com.example.gallery.R;
import com.example.gallery.helper.DateConverter;
import com.example.gallery.helper.FileManager;
import com.example.gallery.object.Image;
import com.google.gson.Gson;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DeleteTask extends AsyncTask<ArrayList<Image>, Integer, Void> {
    private Context context;
    private AlertDialog dialog = null;
    private TextView progressMessage = null;
    private TextView progressText = null;
    ArrayList<Uri> uris;
    ArrayList<String> newPaths;
    private int total = 0;
    public DeleteTask(Context context) {
        this.context = context;
        View progressDialogBox = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        progressMessage = progressDialogBox.findViewById(R.id.loading_msg);
        progressText = progressDialogBox.findViewById(R.id.loading_status);
        progressMessage.setText(R.string.moveToTrash);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(progressDialogBox);
        dialog = builder.create();
    }

    protected void onPreExecute() {
        dialog.show();
    }

    @Override
    protected Void doInBackground(ArrayList<Image>... params) {
        String appFolder = context.getApplicationContext().getExternalFilesDir("").getAbsolutePath();
        File folder = new File(appFolder, "Trash");
        if (!folder.exists()) {
            folder.mkdir();
        }
        uris = new ArrayList<>();
        newPaths = new ArrayList<>();
        ArrayList<Image> images = params[0];
        this.total = images.size();
        SharedPreferences myPref = context.getSharedPreferences("TRASH", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        Gson gson = new Gson();
        for (int i = 0; i < images.size(); ++i) {
            Path oldPath = Paths.get(images.get(i).getPath());
            Path newPath = Paths.get(folder.getAbsolutePath(), oldPath.getFileName().toString());
            File file = new File(newPath.toString());
            try {
                if(file.createNewFile()){
                    Files.copy(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                    Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + images.get(i).getIdInMediaStore());
                    uris.add(uri);
                    newPaths.add(newPath.toString());
                    publishProgress((Integer) i);
                    ArrayList<String> data = new ArrayList<>(2);
                    data.add(images.get(i).getPath());
                    Date dateExpires = DateConverter.plusTime(new Date(), 30, Calendar.DATE);
                    data.add(DateConverter.longToString(dateExpires.getTime()));
                    editor.putString(newPath.toString(), gson.toJson(data));
                    editor.apply();
                }

            } catch (Exception e) {
                Log.d("ERROR create/copy file", "id: " + String.valueOf(images.get(i).getIdInMediaStore()));
                if(file.delete()){
                    Log.d("Delete error file", "Delete error file");
                }
            }
        }
        try {
            Intent intent = new Intent();
            intent.putExtra("delete-uris", gson.toJson(uris));
            delete((Activity) context, uris.toArray(new Uri[uris.size()]), 1, intent);
        } catch(Exception e){
            Log.d("ERROR delete image", "ERROR delete image");
            for(int i = 0; i < newPaths.size(); ++i){
                File file = new File(newPaths.get(i));
                file.delete();
                editor.remove(newPaths.get(i));
                editor.apply();
            }
            Toast.makeText(context, R.string.cannot_delete_photo, Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    protected void onProgressUpdate(Integer... progress) {
        String updateStr = progress[0].toString() + "/" + String.valueOf(total);
        progressText.setText(updateStr);
    }

    protected void onPostExecute(Void result) {
        dialog.dismiss();
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
            Toast.makeText(context, R.string.delete_photo_success, Toast.LENGTH_SHORT).show();
        }
    }

    private void delete(final Activity activity, final Uri[] uriList, final int requestCode, Intent fillInIntent)
            throws SecurityException, IntentSender.SendIntentException, IllegalArgumentException
    {
        final ContentResolver resolver = activity.getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            // WARNING: if the URI isn't a MediaStore Uri and specifically
            // only for media files (images, videos, audio) then the request
            // will throw an IllegalArgumentException, with the message:
            // 'All requested items must be referenced by specific ID'

            // No need to handle 'onActivityResult' callback, when the system returns
            // from the user permission prompt the files will be already deleted.
            // Multiple 'owned' and 'not-owned' files can be combined in the
            // same batch request. The system will automatically delete them
            // using the same prompt dialog, making the experience homogeneous.

            final List<Uri> list = new ArrayList<>();
            Collections.addAll(list, uriList);
            final PendingIntent pendingIntent = MediaStore.createDeleteRequest(resolver, list);
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, fillInIntent, 0, 0, 0, null);
        }
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q)
        {
            try
            {
                // In Android == Q a RecoverableSecurityException is thrown for not-owned.
                // For a batch request the deletion will stop at the failed not-owned
                // file, so you may want to restrict deletion in Android Q to only
                // 1 file at a time, to make the experience less ugly.
                // Fortunately this gets solved in Android R.

                for (final Uri uri : uriList)
                {
                    resolver.delete(uri, null, null);
                }
            }
            catch (RecoverableSecurityException ex)
            {
                final IntentSender intent = ex.getUserAction()
                        .getActionIntent()
                        .getIntentSender();

                // IMPORTANT: still need to perform the actual deletion
                // as usual, so again getContentResolver().delete(...),
                // in your 'onActivityResult' callback, as in Android Q
                // all this extra code is necessary 'only' to get the permission,
                // as the system doesn't perform any actual deletion at all.
                // The onActivityResult doesn't have the target Uri, so you
                // need to cache it somewhere.
                activity.startIntentSenderForResult(intent, requestCode, fillInIntent, 0, 0, 0, null);
            }
        }
        else
        {
            // As usual for older APIs
            for (final Uri uri : uriList)
            {
                resolver.delete(uri, null, null);
            }
        }
    }
}
