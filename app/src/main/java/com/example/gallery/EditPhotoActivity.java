package com.example.gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.widget.ImageView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class EditPhotoActivity extends AppCompatActivity {

    String uri;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);


        uri = getIntent().getStringExtra("uri");
        Uri fileUri = Uri.fromFile(new File(uri));
        imageView = findViewById(R.id.img);
        UCrop.Options  options = new UCrop.Options();

        String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        UCrop.of(fileUri,Uri.fromFile(new File(getCacheDir(),dest_uri)))
                .withOptions(options)
                .withAspectRatio(0,0)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000,2000)
                .start(EditPhotoActivity.this);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode ==UCrop.REQUEST_CROP) {

            final Uri outputUri = UCrop.getOutput(data);
            // handle the result uri as you want, such as display it in an imageView;
            imageView.setImageURI(outputUri);

        }
        else if(resultCode == UCrop.RESULT_ERROR){
            final Throwable cropError = UCrop.getError(data);
        }
    }
}