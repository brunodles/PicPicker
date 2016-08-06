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
import com.github.brunodles.picpicker.PicPicker;
import com.github.brunodles.picpicker.impl.WritePermissionAsker;
import com.github.brunodles.picpicker.listener.ActivityStarter;
import com.github.brunodles.picpicker.listener.CantFindCameraAppErrorListener;
import com.github.brunodles.picpicker.listener.ErrorCreatingTempFileForCameraListener;
import com.github.brunodles.picpicker.listener.PicResultListener;

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

        // This is the default implementation of the permission asker,
        // but you can write your another implementation, or ask the permission for your own.
        writePermissionAsker = new WritePermissionAsker(this, RC_WRITE_EXTERNAL_STORAGE,
                R.string.permission_message);

        // Prepare the picPicker
        picPicker = new PicPicker(this, picResultListener)
                // the will be called when we get a picture from the camera
                .setFileForCameraListener(fileForCameraListener)
                // this will be called when we got a error from the camera app,
                // sometimes it even doesn't exists.
                .setCameraAppErrorListener(cameraAppErrorListener)
                // this will be called when we need a permission, for android 6+
                .setPermissionErrorListener(writePermissionAsker);

        galery.setOnClickListener(this);
        camera.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == galery)
            picPicker.gallery(); // Here you call the gallery app
        else if (v == camera)
            picPicker.camera(); // Here you call the camera app
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // You need to pass the permission result to writePermissionAsker, so the lib can check
        // if the app have write permission. Having permission we start the camera.
        if (writePermissionAsker.onRequestPermissionsResult(requestCode, permissions, grantResults))
            picPicker.camera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // You need to pass the activity result to picPicker, so the lib can check the response from
        // the gallery or camera. It will return true if found the image.
        if (!picPicker.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    // This listener will receive the bitmap from the picPicture, so you don't need to worry about from
    // where we got the image.
    private PicResultListener picResultListener = new PicResultListener() {
        @Override
        public void onPictureResult(final Bitmap bitmap) {
            Log.d(TAG, "onPictureResult: ");
            image.setImageBitmap(bitmap); // the the bitmap on a ImageView
            // This is the BitmapCompressor, it will compress the image to a better size.
            // We may not need a full sized picture on our services.
            // This is a AsyncTask, so we need to implement the onPostExecute
            // This could even be a field on the Activity, it's here just to show that it's optional.
            new BitmapCompressor(400) {
                @Override
                protected void onPostExecute(Bitmap[] bitmaps) {
                    // Here we get the response from the bitmapCompressor
                    Log.d(TAG, "bitmapCompressor.onPostExecute: ");
                    image.setImageBitmap(bitmaps[0]); // sets the compressed bitmap on the ImageView
                }
            }.execute(bitmap); // Here we pass the bitmap
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
