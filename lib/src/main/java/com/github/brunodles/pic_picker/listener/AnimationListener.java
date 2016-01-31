package com.github.brunodles.pic_picker.listener;

import android.widget.ImageView;

/**
 * Created by bruno on 31/01/16.
 */
public interface AnimationListener {

    void onPreExecute(ImageView imageView);
    void onBeforeSetBitmap(ImageView imageView);
    void onAfterSetBitmap(ImageView imageView);
    void onFail(ImageView imageView);
}
