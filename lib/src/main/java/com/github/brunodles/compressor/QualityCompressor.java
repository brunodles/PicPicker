package com.github.brunodles.compressor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by bruno on 05/08/16.
 */
public class QualityCompressor implements Compressor {
    private static final String TAG = "QualityCompressor";
    private static final Bitmap.CompressFormat DEFAULT_FILE_FORMAT = Bitmap.CompressFormat.JPEG;

    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Override
    public Bitmap compress(Bitmap bitmap, int targetSize) {
        int quality = 100;
        int interactions = 0;
        while (quality >= 0) {
            try {
                Log.d(TAG, "compress: quality " + quality);
                out.reset();
                bitmap.compress(DEFAULT_FILE_FORMAT, quality, out);
                int currentSize = out.size() / 1024;
                Log.d(TAG, "compress: currentSize " + currentSize);
                if (currentSize <= targetSize)
                    break;
                int nextQuality = ((targetSize * quality) / currentSize);
                if (nextQuality == quality)
                    break;
                else
                    quality = nextQuality;
            } catch (Exception e) {
                Log.e(TAG, "Error compressing bitmap " + quality, e);
            }
            interactions++;
            if (interactions > 5)
                break;
        }
        if (out.size() > 0)
            return BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
        return bitmap;
    }
}
