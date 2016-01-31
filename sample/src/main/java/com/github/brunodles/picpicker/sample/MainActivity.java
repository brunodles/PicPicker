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

import com.github.brunodles.compressor.BitmapCompressor;
import com.github.brunodles.pic_picker.PicPicker;
import com.github.brunodles.pic_picker.impl.WritePermissionAsker;
import com.github.brunodles.pic_picker.listener.ActivityStarter;
import com.github.brunodles.pic_picker.listener.CantFindCameraAppErrorListener;
import com.github.brunodles.pic_picker.listener.ErrorCreatingTempFileForCameraListener;
import com.github.brunodles.pic_picker.listener.PicResultListener;

public class MainActivity extends AppCompatActivity implements ActivityStarter,
        View.OnClickListener {
    private static final String TAG = "MainActivity";
    // This is the request code used to ask write permission
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
        galery = (Button) findViewById(R.id.galery);
        camera = (Button) findViewById(R.id.camera);
        image = (ImageView) findViewById(R.id.image);

        // This is the default impelementation of the permission asker, but you can write your own.
        writePermissionAsker = new WritePermissionAsker(this, RC_WRITE_EXTERNAL_STORAGE,
                R.string.permission_message);
        // Prepare the picPicker
        picPicker = new PicPicker(this, picResultListener)
                .setFileForCameraListener(fileForCameraListener)
                .setCameraAppErrorListener(cameraAppErrorListener)
                .setPermissionErrorListener(writePermissionAsker);

        galery.setOnClickListener(this);
        camera.setOnClickListener(this);
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
        // You need to pass the persmission result to writePermissionAsker, so the lib can check
        // if the app have write permission. Having permission we start the camera.
        if (writePermissionAsker.onRequestPermissionsResult(requestCode, permissions, grantResults))
            picPicker.camera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // You need to pass the activity result to picPicker, so the lib can check the response from
        // the gallery or camera.
        if (!picPicker.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private PicResultListener picResultListener = new PicResultListener() {
        @Override
        public void onPictureResult(final Bitmap bitmap) {
            Log.d(TAG, "onPictureResult: ");
            image.setImageBitmap(bitmap);
            new BitmapCompressor(400) {
                @Override
                protected void onPostExecute(Bitmap[] bitmaps) {
                    Log.d(TAG, "bitmapCompressor.onPostExecute: ");
                    image.setImageBitmap(bitmaps[0]);
                }
            }.execute(bitmap);
        }
    };
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
