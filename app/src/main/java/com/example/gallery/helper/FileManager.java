package com.example.gallery.helper;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileManager {
    public static boolean deleteFromPath(String path, Context context, Activity activity){
        try{
            long id = getFilePathToMediaID(path,context);
            final Uri uri = ContentUris.withAppendedId( MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), Long.valueOf(id));
            final Uri[] uris={uri};
            delete(activity,uris,1);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }
    public static boolean moveFile(final Activity activity, String sourcePath, String desPath, Context context){
        Path source = Paths.get(sourcePath);
        Path dest = Paths.get(desPath);
        long id = getFilePathToMediaID(sourcePath,context);
        final Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        final Uri[] uris = { uri };
        File file = new File(dest.toString());
        if(file.exists()){
            return false;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            return false;
        }
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            FileManager.delete(activity, uris, 1);
        } catch (Exception e) {
            file.delete();
            return false;
        }
        return true;
    }

    public static void delete(final Activity activity, final Uri[] uriList, final int requestCode)
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
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, null, 0, 0, 0, null);
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
                activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null);
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

    public static long getFilePathToMediaID(String imagePath, Context context)
    {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Images.Media.DATA;
        String[] selectionArgs = {imagePath};
        String[] projection = {MediaStore.Images.Media._ID};

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }

        return id;
    }
}
