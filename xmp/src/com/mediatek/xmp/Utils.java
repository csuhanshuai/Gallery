package com.mediatek.xmp;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * XMP utils function.
 */
public class Utils {
    private static final String TAG = "Utils";
    private static final int DEFAULT_COMPRESS_QUALITY = 100;

    /**
     * encode mask to PNG, first generate bitmap, then compress to bitmap.
     *
     * @param data
     *            mask data
     * @param width
     *            mask width
     * @param height
     *            mask height
     * @return encode data
     */
    public static byte[] encodePng(byte[] data, int width, int height) {
        Bitmap alphaBitmap = Bitmap.createBitmap(width, height, Config.ALPHA_8);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        alphaBitmap.compress(CompressFormat.PNG, DEFAULT_COMPRESS_QUALITY, stream);
        return stream.toByteArray();
    }

    /**
     * <1>decode PNG to bitmap. <2>get mask buffer from bitmap.
     *
     * @param data
     *            PNG data
     * @return success->mask buffer, fail->null
     */
    public static byte[] decodePng(byte[] data) {
        if (data == null) {
            Log.d(TAG, "<decodePng> data is null!!!");
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        if (bitmap == null) {
            Log.d(TAG, "decode png fail!!!");
            return null;
        }
        int length = bitmap.getWidth() * bitmap.getHeight();
        ByteBuffer buffer = ByteBuffer.allocate(length);
        byte[] dst = new byte[length];
        bitmap.copyPixelsToBuffer(buffer);
        buffer.rewind();
        buffer.get(dst);
        return dst;
    }
}
