package br.com.brunolima.pic_picker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by brunodles on 10/11/14.
 */
public class PicPicker {

    private static final int REQUEST_CODE_ATTACH_IMAGE = 9123;
    private static final int REQUEST_CODE_TAKE_PICURE = 9124;

    private Uri fileUri;
    ImageView userImage;
    ActivityStarter activityStarter;
    PicResultListener listener;

    public PicPicker(ImageView userImage, ActivityStarter activityStarter) {
        this.userImage = userImage;
        this.activityStarter = activityStarter;
    }

    public PicPicker setListener(PicResultListener listener) {
        this.listener = listener;
        return this;
    }

    public void gallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_ATTACH_IMAGE);
    }

    private void startActivityForResult(Intent intent, int requestCodeAttachImage) {
        activityStarter.startActivityForResult(intent, requestCodeAttachImage);
    }

    public void camera() throws CantFindCameraAppExpcetion, ErrorCreatingTempFileForCamera {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                // Error occurred while creating the File
                throw new ErrorCreatingTempFileForCamera(ex);
            }
            // Continue only if the File was successfully created
            fileUri = Uri.fromFile(photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICURE);
        } else {
            throw new CantFindCameraAppExpcetion();
        }
    }

    private PackageManager getPackageManager() {
        return userImage.getContext().getPackageManager();
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case REQUEST_CODE_ATTACH_IMAGE:
                    new AddImageAsyncTask(userImage, data.getData())
                            .setListener(listener)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    return true;
                case REQUEST_CODE_TAKE_PICURE:
                    new AddImageAsyncTask(userImage, fileUri)
                            .setListener(listener)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    return true;
            }
        return false;
    }
}
