package com.github.brunodles.picpicker.sample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.brunodles.picpicker.R;

import br.com.brunolima.pic_picker.PicPicker;
import br.com.brunolima.pic_picker.listener.ActivityStarter;
import br.com.brunolima.pic_picker.listener.CantFindCameraAppErrorListener;
import br.com.brunolima.pic_picker.listener.ErrorCreatingTempFileForCameraListener;
import br.com.brunolima.pic_picker.listener.NeedWritePermissionErrorListener;
import br.com.brunolima.pic_picker.listener.PicResultListener;

public class MainActivity extends AppCompatActivity implements ActivityStarter, PicResultListener,
        View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int RC_WRITE_EXTERNAL_STORAGE = 42;

    private Button galery;
    private Button camera;
    private ImageView image;
    private PicPicker picPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        galery = (Button) findViewById(R.id.galery);
        camera = (Button) findViewById(R.id.camera);
        image = (ImageView) findViewById(R.id.image);
        picPicker = new PicPicker(image, this).setResultListener(this)
                .setFileForCameraListener(new ErrorCreatingTempFileForCameraListener() {
                    @Override
                    public void errorCreatingTempFileForCamera() {
                        Log.e(TAG, "errorCreatingTempFileForCamera: ");
                        Toast.makeText(MainActivity.this, "Error starting camera", Toast.LENGTH_SHORT).show();
                    }
                })
                .setCameraAppErrorListener(new CantFindCameraAppErrorListener() {
                    @Override
                    public void cantFindCameraApp() {
                        Log.e(TAG, "cantFindCameraApp: ");
                        Toast.makeText(MainActivity.this, "Can't find the camera app", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPermissionErrorListener(new NeedWritePermissionErrorListener() {
                    @Override
                    public void needWritePermission() {
                        Log.e(TAG, "needWritePermission: ");
                        askPermission();
                    }
                });

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

    private void askPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(this)
                    .setMessage("We need to write on disk to use camera.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    RC_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RC_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onClick(camera);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picPicker.onActivityResult(requestCode, resultCode, data);
    }
}
