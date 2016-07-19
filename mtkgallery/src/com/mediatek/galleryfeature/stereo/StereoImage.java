package com.mediatek.galleryfeature.stereo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.mediatek.xmp.XmpOperator;

/**
 * decode stereo image.
 *
 */
public class StereoImage {
    private static final String TAG = "StereoImage";

    /**
     * <1>get JPEG from app1(XMP). <2>if app1 contain JPEG, decode app1 JPEG,
     * els decode main image.
     *
     * @param filePath
     *            source file path
     * @param sampSize
     *            decode sample size
     * @return success->bitmap, fail->null
     */
    public static Bitmap decodeStereoImage(String filePath, int sampSize) {
        Log.d(TAG, "<decodeStereoImage> filePath:" + filePath + ",sampSize:" + sampSize);
        byte[] jpegBuf = null;
        final XmpOperator xmp = new XmpOperator();
        jpegBuf = xmp.getClearImage(filePath);
        if (jpegBuf == null) {
            return decodeJpeg(filePath, sampSize);
        } else {
            return decodeJpeg(jpegBuf, sampSize);
        }
    }

    /**
     * get clear image from XMP.
     * @param filePath source file path
     * @return success->clear image, fail->null
     */
    public static byte[] getClearImage(String filePath) {
        final XmpOperator xmp = new XmpOperator();
        return xmp.getClearImage(filePath);
    }

    private static Bitmap decodeJpeg(String path, int sampSize) {
        Log.d(TAG, "<decodeJpeg> path:" + path + ",sampSize:" + sampSize);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = sampSize;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path, options);
    }

    private static Bitmap decodeJpeg(byte[] data, int sampSize) {
        Log.d(TAG, "<decodeJpeg> data:" + data + ",sampSize:" + sampSize);
        if (data == null) {
            Log.d(TAG, "null jpeg buffer");
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        options.inSampleSize = sampSize;
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }
}
