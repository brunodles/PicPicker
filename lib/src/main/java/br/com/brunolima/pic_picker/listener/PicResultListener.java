package br.com.brunolima.pic_picker.listener;

import android.graphics.Bitmap;

/**
 * Created by brunodles on 10/11/14.
 */
public interface PicResultListener {
    void onPictureResult(Bitmap bitmap);

    void onException(Exception e);
}
