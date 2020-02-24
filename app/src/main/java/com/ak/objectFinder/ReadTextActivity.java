package com.ak.objectFinder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;

public class ReadTextActivity extends AppCompatActivity {

    private TextToSpeech tts;
    public static final int CAMERA_REQUEST_CODE = 1;
    private ImageView imageView;
    private Uri imgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_text);
        checkPermissions(this);

        imageView = findViewById(R.id.signImg);
        File tempImgFile = new File(getExternalFilesDir( Environment.DIRECTORY_PICTURES), "sign.jpg");
        imgUri = FileProvider.getUriForFile(this, "com.ak.objectFinder", tempImgFile);

        if (Globals.audioPref){
            // re
        }

    }

    public void onTakePhotoClicked(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK) return;
        if(requestCode == CAMERA_REQUEST_CODE){
            imageView.setImageURI(imgUri);

            //Crop.of(imgUri,imgUri).withMaxSize(200,400);

        } /*if (requestCode == Crop.REQUEST_CROP){
            imgUri = Crop.getOutput(data);
            imageView.setImageURI(null);
            imageView.setImageURI(imgUri);
        } */



    }

    public static void checkPermissions(Activity activity){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }
}
