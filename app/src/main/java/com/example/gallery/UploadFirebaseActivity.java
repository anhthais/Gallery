package com.example.gallery;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gallery.object.BackupImage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadFirebaseActivity extends AppCompatActivity {
    private FloatingActionButton uploadButton;
    private ImageView uploadImage;
    EditText uploadCaption;
    ProgressBar progressBar;
    private Uri imageUri;
    String key = null;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    SharedPreferences storedKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_firebase);
        // Hide actionBar
        /*if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }*/

        uploadButton = findViewById(R.id.uploadButton);
        uploadImage = findViewById(R.id.uploadImage);
        uploadCaption = findViewById(R.id.uploadCaption);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        storedKey = this.getSharedPreferences("UserCloud-key", Context.MODE_PRIVATE);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == Activity.RESULT_OK){
                            Intent data = o.getData();
                            imageUri = data.getData();
                            uploadImage.setImageURI(imageUri);
                        }else{
                            Toast.makeText(UploadFirebaseActivity.this,"No Image Selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null){
                    uploadToFirebase(imageUri);
                }
                else{
                    Toast.makeText(UploadFirebaseActivity.this, "Please select image",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadToFirebase(Uri uri) {
        String caption = uploadCaption.getText().toString();
        if(storedKey.contains("ID")){
            key = storedKey.getString("ID",null);

        }
        else {
            key = databaseReference.push().getKey();
            SharedPreferences preferences = getSharedPreferences("UserCloud-key", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("ID", key);
            editor.commit();
        }

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(key);
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis()+"."+ getFileExtension(uri));

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        BackupImage dataClass = new BackupImage(uri.toString(), caption);
                        String keyNew = databaseReference.child(key).push().getKey();
                        databaseReference.child(key).child(keyNew).setValue(dataClass);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadFirebaseActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UploadFirebaseActivity.this, ShowBackupActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(UploadFirebaseActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}