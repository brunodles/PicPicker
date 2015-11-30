package br.com.brunolima.pic_picker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.brunolima.pic_picker.listener.ActivityStarter;
import br.com.brunolima.pic_picker.listener.CantFindCameraAppErrorListener;
import br.com.brunolima.pic_picker.listener.ErrorCreatingTempFileForCameraListener;
import br.com.brunolima.pic_picker.listener.NeedWritePermissionErrorListener;
import br.com.brunolima.pic_picker.listener.PicResultListener;

/**
 * Created by brunodles on 10/11/14.
 */
public class PicPicker {

    private static final String TAG = "PicPicker";

    private static final int REQUEST_CODE_ATTACH_IMAGE = 9123;
    private static final int REQUEST_CODE_TAKE_PICURE = 9124;

    private Uri fileUri;
    private ImageView userImage;
    private ActivityStarter activityStarter;
    private PicResultListener listener;

    private CantFindCameraAppErrorListener cameraAppErrorListener;
    private NeedWritePermissionErrorListener permissionErrorListener;
    private ErrorCreatingTempFileForCameraListener fileForCameraListener;

    public PicPicker(ImageView userImage, ActivityStarter activityStarter) {
        this.userImage = userImage;
        this.activityStarter = activityStarter;
    }

    public PicPicker setResultListener(PicResultListener listener) {
        this.listener = listener;
        return this;
    }

    public PicPicker setCameraAppErrorListener(CantFindCameraAppErrorListener cameraAppErrorListener) {
        this.cameraAppErrorListener = cameraAppErrorListener;
        return this;
    }

    public PicPicker setPermissionErrorListener(NeedWritePermissionErrorListener permissionErrorListener) {
        this.permissionErrorListener = permissionErrorListener;
        return this;
    }

    public PicPicker setFileForCameraListener(ErrorCreatingTempFileForCameraListener fileForCameraListener) {
        this.fileForCameraListener = fileForCameraListener;
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

    public void camera() {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            startCameraIntent();
        else
            needWritePermission();
    }

    private void needWritePermission() {
        if (permissionErrorListener == null)
            Log.e(TAG, "Hey dev, you need to call `setPermissionErrorListener` on PicPicker, to manage permission error.");
        else
            permissionErrorListener.needWritePermission();
    }

    private Context getContext() {
        return userImage.getContext();
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                // Error occurred while creating the File
                errorCreatingTempFileForCamera(ex);
            }
            // Continue only if the File was successfully created
            fileUri = Uri.fromFile(photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICURE);
        } else {
            cantFindCameraApp();
        }
    }

    private void errorCreatingTempFileForCamera(Exception ex) {
        if (fileForCameraListener == null)
            Log.e(TAG, "errorCreatingTempFileForCamera: ", ex);
        else
            fileForCameraListener.errorCreatingTempFileForCamera();
    }

    private void cantFindCameraApp() {
        if (cameraAppErrorListener == null)
            Log.e(TAG, "Hey dev, you need to call `setCameraAppErrorListener` on PicPicker, to manage this error. Aparently this device don't have camera, so, there's no app to open.");
        else
            cameraAppErrorListener.cantFindCameraApp();
    }

    private PackageManager getPackageManager() {
        return getContext().getPackageManager();
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
