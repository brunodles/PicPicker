package com.github.brunodles.compressor;

import android.graphics.Bitmap;

/**
 * Created by bruno on 05/08/16.
 */
public interface Compressor {
    Bitmap compress(Bitmap bitmap, int targetSize);
}
