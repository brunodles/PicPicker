package com.github.brunodles.picpicker.listener;

import android.graphics.Bitmap;

/**
 * This interface will receive a bitmap when the lib got the response.
 * Created by brunodles on 10/11/14.
 */
public interface PicResultListener {
    void onPictureResult(Bitmap bitmap);
}
