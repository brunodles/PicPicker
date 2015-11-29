package com.github.brunodles.picpicker.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.brunodles.picpicker.R;

import br.com.brunolima.pic_picker.ActivityStarter;
import br.com.brunolima.pic_picker.CantFindCameraAppExpcetion;
import br.com.brunolima.pic_picker.ErrorCreatingTempFileForCamera;
import br.com.brunolima.pic_picker.PicPicker;
import br.com.brunolima.pic_picker.PicResultListener;

public class MainActivity extends AppCompatActivity implements ActivityStarter, PicResultListener,
        View.OnClickListener {
    private static final String TAG = "MainActivity";

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
        picPicker = new PicPicker(image, this).setListener(this);

        galery.setOnClickListener(this);
        camera.setOnClickListener(this);
    }

    @Override
    public void onPictureResult(Bitmap bitmap) {
        Log.d(TAG, "onPictureResult: ");
        // do something
        image.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        if (v == galery)
            picPicker.gallery();
        else if (v == camera)
            try {
                picPicker.camera();
            } catch (CantFindCameraAppExpcetion e) {
                Log.e(TAG, "onClick: ", e);
                Toast.makeText(this, "Can't find the camera app", Toast.LENGTH_SHORT).show();
            } catch (ErrorCreatingTempFileForCamera e) {
                Log.e(TAG, "onClick: ", e);
                Toast.makeText(this, "Error starting camera", Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        picPicker.onActivityResult(requestCode, resultCode, data);
    }
}
