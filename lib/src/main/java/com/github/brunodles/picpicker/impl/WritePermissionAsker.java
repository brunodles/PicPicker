package com.github.brunodles.picpicker.impl;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.github.brunodles.picpicker.listener.NeedWritePermissionErrorListener;

/**
 * This class can be passed as parameter to {@link com.github.brunodles.picpicker.PicPicker} as a
 * Listener.
 * Created by bruno on 30/01/16.
 */
public class WritePermissionAsker implements NeedWritePermissionErrorListener {

    private Activity activity;
    private int requestCode;
    private final int dialogMessage;

    public WritePermissionAsker(Activity activity, int requestCode, @StringRes int dialogMessage) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.dialogMessage = dialogMessage;
    }

    @Override
    public void needWritePermission() {
        askPermission();
    }

    public void askPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            new AlertDialog.Builder(activity)
                    .setMessage(dialogMessage)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    requestCode);
                        }
                    })
                    .show();
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
        }
    }

    /**
     * This method should be called from the activity to enable this class to manage the request
     * permission result.
     *
     * @return Will return true if the permission was granted.
     */
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                              @NonNull int[] grantResults) {
        return ((requestCode == this.requestCode)
                && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED));
    }
}
