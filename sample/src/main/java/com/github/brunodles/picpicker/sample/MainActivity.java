package com.github.brunodles.picpicker.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.brunodles.picpicker.R;

import br.com.brunolima.pic_picker.PicPicker;
import br.com.brunolima.pic_picker.impl.WritePermissionAsker;
import br.com.brunolima.pic_picker.listener.ActivityStarter;
import br.com.brunolima.pic_picker.listener.CantFindCameraAppErrorListener;
import br.com.brunolima.pic_picker.listener.ErrorCreatingTempFileForCameraListener;
import br.com.brunolima.pic_picker.listener.PicResultListener;

public class MainActivity extends AppCompatActivity implements ActivityStarter, PicResultListener,
        View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int RC_WRITE_EXTERNAL_STORAGE = 42;

    private Button galery;
    private Button camera;
    private ImageView image;
    private PicPicker picPicker;
    private WritePermissionAsker writePermissionAsker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        writePermissionAsker = new WritePermissionAsker(this, RC_WRITE_EXTERNAL_STORAGE, R.string.permission_message);

        galery = (Button) findViewById(R.id.galery);
        camera = (Button) findViewById(R.id.camera);
        image = (ImageView) findViewById(R.id.image);
        picPicker = new PicPicker(image, this).setResultListener(this)
                .setFileForCameraListener(fileForCameraListener)
                .setCameraAppErrorListener(cameraAppErrorListener)
                .setPermissionErrorListener(writePermissionAsker);

        galery.setOnClickListener(this);
        camera.setOnClickListener(this);
    }

    @Override
    public void onPictureResult(Bitmap bitmap) {
        Log.d(TAG, "onPictureResult: ");
        // do something
    }

    @Override
    public void onClick(View v) {
        if (v == galery)
            picPicker.gallery();
        else if (v == camera)
            picPicker.camera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (writePermissionAsker.onRequestPermissionsResult(requestCode, permissions, grantResults))
            onClick(camera);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!picPicker.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private CantFindCameraAppErrorListener cameraAppErrorListener = new CantFindCameraAppErrorListener() {
        @Override
        public void cantFindCameraApp() {
            Log.e(TAG, "cantFindCameraApp: ");
            Toast.makeText(MainActivity.this, "Can't find the camera app", Toast.LENGTH_SHORT).show();
        }
    };
    private ErrorCreatingTempFileForCameraListener fileForCameraListener = new ErrorCreatingTempFileForCameraListener() {
        @Override
        public void errorCreatingTempFileForCamera() {
            Log.e(TAG, "errorCreatingTempFileForCamera: ");
            Toast.makeText(MainActivity.this, "Error starting camera", Toast.LENGTH_SHORT).show();
        }
    };
}
