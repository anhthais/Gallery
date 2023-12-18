package com.example.gallery.helper;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class StorageUtil {
    public static void exportFile(File src, File dst) throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File expFile = new File(dst.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        FileChannel inChannel = null;
        FileChannel outChannel = null;

//        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

}
