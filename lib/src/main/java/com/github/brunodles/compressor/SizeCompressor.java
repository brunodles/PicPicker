package com.github.brunodles.compressor;

import android.graphics.Bitmap;
import android.util.Log;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * Created by bruno on 05/08/16.
 */
public class SizeCompressor implements Compressor {
    private static final String TAG = "SizeCompressor";

    @Override
    public Bitmap compress(Bitmap bitmap, int targetSize) {
        float ratio = 1;
        Bitmap result = bitmap;
        do {
            try {
                int currentSize = result.getByteCount() / 1024;
                Log.d(TAG, "compress: currentSize " + currentSize);
                if (currentSize <= targetSize) return result;

                ratio = targetSize / (float) currentSize;

                if (ratio <= 0) ratio = 0.1F;

                Log.d(TAG, "compress: ratio " + ratio);
                int width = Math.round(ratio * bitmap.getWidth());
                int height = Math.round(ratio * bitmap.getHeight());
                result = createScaledBitmap(bitmap, width, height, false);
            } catch (Exception e) {
                Log.e(TAG, "Error resizing bitmap " + ratio, e);
            }
        } while (ratio >= 0);
        return result;
    }
}
