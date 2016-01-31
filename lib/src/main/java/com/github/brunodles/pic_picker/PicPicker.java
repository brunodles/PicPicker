package com.github.brunodles.pic_picker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.brunodles.pic_picker.listener.ActivityStarter;
import com.github.brunodles.pic_picker.listener.CantFindCameraAppErrorListener;
import com.github.brunodles.pic_picker.listener.ErrorCreatingTempFileForCameraListener;
import com.github.brunodles.pic_picker.listener.NeedWritePermissionErrorListener;
import com.github.brunodles.pic_picker.listener.PicResultListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This class will be the main interface to request a picture.
 * You can do this by calling {@link #gallery()} or {@link #camera()}.
 * This class uses a `Method chaining` pattern to set the parameters.
 * Created by brunodles on 10/11/14.
 */
public final class PicPicker {

    private static final String TAG = "PicPicker";

    /**
     * You can change this variable to change the request code used to open gallery
     */
    public static int REQUEST_CODE_ATTACH_IMAGE = 9123;
    /**
     * You can change the variable to change the request code used to open camera
     */
    public static int REQUEST_CODE_TAKE_PICURE = 9124;

    private Uri fileUri;
    private final Context context;
    private final ActivityStarter activityStarter;
    private final PicResultListener listener;

    private CantFindCameraAppErrorListener cameraAppErrorListener;
    private NeedWritePermissionErrorListener permissionErrorListener;
    private ErrorCreatingTempFileForCameraListener fileForCameraListener;

    /**
     * @param context         a Context
     * @param activityStarter an activity starter, can be an {@link Activity} or an
     *                        {@link android.support.v4.app.Fragment}
     * @param listener        A interface able to receive results.All images captured by this class will be passed to this listener on
     *                        {@link PicResultListener#onPictureResult(Bitmap)}.
     */
    public PicPicker(Context context, ActivityStarter activityStarter, PicResultListener listener) {
        this.context = context;
        this.activityStarter = activityStarter;
        this.listener = listener;
    }

    /**
     * @param activity an activity to serve as {@link Context} and as a {@link ActivityStarter}
     * @param listener A interface able to receive results.All images captured by this class will be passed to this listener on
     *                 {@link PicResultListener#onPictureResult(Bitmap)}.
     */
    public PicPicker(Activity activity, PicResultListener listener) {
        this.context = activity;
        this.listener = listener;
        this.activityStarter = new Activity_ActivityStarter(activity);
    }

    /**
     * A method to set a listener for camera app error.
     * When the lib can't find the camera app, the error will be sent thought this interface.
     *
     * @param cameraAppErrorListener
     */
    public PicPicker setCameraAppErrorListener(CantFindCameraAppErrorListener cameraAppErrorListener) {
        this.cameraAppErrorListener = cameraAppErrorListener;
        return this;
    }

    /**
     * A method to set a listener for permission error.
     * When user is using Android Marshmallow he will need to authorize the app to use the external
     * storage to write a temp file.
     *
     * @param permissionErrorListener This is a interface that will be called when we need to
     *                                request camera permission.
     */
    public PicPicker setPermissionErrorListener(NeedWritePermissionErrorListener permissionErrorListener) {
        this.permissionErrorListener = permissionErrorListener;
        return this;
    }

    /**
     * A method to set a listener for temp file error.
     * To use camera, the app will need to create a temp file.
     * But if it fail you can know it just implementing this interface.
     *
     * @param fileForCameraListener This is a interface that will be called when an error was throw
     *                              when we try to create a temp file.
     */
    public PicPicker setFileForCameraListener(ErrorCreatingTempFileForCameraListener fileForCameraListener) {
        this.fileForCameraListener = fileForCameraListener;
        return this;
    }

    /**
     * This method will start the gallery.
     * The result will be passed to the result listener.
     */
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

    /**
     * This method will start the camera.
     * The result will be passed to the result listener.
     */
    public void camera() {
        int permissionCheck = ContextCompat.checkSelfPermission(context,
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
        return context.getPackageManager();
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

    /**
     * To let the lib know when the user choose a image you will need to call this method from an
     * {@link Activity#onActivityResult(int, int, Intent)} or from a
     * {@link android.support.v4.app.Fragment#onActivityResult(int, int, Intent)}
     *
     * @return if the result is false call the default implementation. A true result means that the
     * lib made something.
     */
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = getUri(requestCode, data);
            if (uri != null) {
                executeImageAsyncTask(uri);
                return true;
            }
        }
        return false;
    }

    private Uri getUri(int requestCode, Intent data) {
        if (requestCode == REQUEST_CODE_ATTACH_IMAGE) return data.getData();
        else if (requestCode == REQUEST_CODE_TAKE_PICURE) return fileUri;
        else return null;
    }

    private void executeImageAsyncTask(Uri uri) {
        new AddImageAsyncTask(context, uri, listener)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}