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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;

public class ReadTextActivity extends AppCompatActivity {

    private TextToSpeech tts;
    public static final int CAMERA_REQUEST_CODE = 1;
    private Uri imgUri;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_text);
        textView = findViewById(R.id.text_show);
        textView.setText("Loading...");
        checkPermissions(this);

        //imageView = findViewById(R.id.signImg);
        File tempImgFile = new File(getExternalFilesDir( Environment.DIRECTORY_PICTURES), "sign.jpg");
        imgUri = FileProvider.getUriForFile(this, "com.ak.objectFinder", tempImgFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);

        if (Globals.audioPref){
            /* TO-DO: speech prompt user to take a picture*/
        }

    }

    public void onSendClicked(View view){
        /* TO DO: send imgUri  to firebase*/
    }

    public void onTryAgainClicked(View view){
        textView.setText("Loading...");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void onAskForHelpClicked(View view){
        /* TO-DO: insert fire base code here, then send push notification */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK) return;
        if(requestCode == CAMERA_REQUEST_CODE){

            VisionAPI.getTextFromImage(imgUri, this, new VisionAPI.TextAnalysisCallback() {
                @Override
                public void onSuccess(String resultText) {
                    // show the text
                    TextView text = findViewById(R.id.text_show);
                    text.setText(resultText);
                }

                @Override
                public void onError(Exception e) {
                    Log.d("MAD", "error in vision API");
                }
            });
        }
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
