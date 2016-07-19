/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to MediaTek Inc. and/or its licensors. Without
 * the prior written permission of MediaTek inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of MediaTek Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * MediaTek Inc. (C) 2015. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN MEDIATEK
 * SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE
 * MEDIATEK SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 *
 * The following software/firmware and/or related documentation ("MediaTek
 * Software") have been modified by MediaTek Inc. All revisions are subject to
 * any receiver's applicable license agreements with MediaTek Inc.
 */
package com.mediatek.galleryfeature.stereo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;

import com.mediatek.galleryfeature.stereo.refocus.RefocusHelper;
import com.mediatek.galleryframework.util.MtkLog;
import com.mediatek.xmp.XmpOperator;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * communicate with refocus JNI and XMP. get JPS,debug info, mask, depth from
 * XMP, generate new image from algorithm.
 */
public class RefocusImageJni {
    private final static String TAG = "mtkGallery2/Refocus/RefocusImage";
    public final static Object SLOCK = new Object();
    public final static int INSAMPLESIZE = 2;
    private static final int TEMP_NUM4 = 4;

    private final Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;
    private static final String TIME_STAMP_NAME = "'IMG'_yyyyMMdd_HHmmss";
    private final XmpOperator mXmpOperator;

    private XmpOperator.DepthBufferInfo mDepthBufferInfo = null;
    private XmpOperator.StereoDebugInfo mStereoDebugInfo;
    private Bitmap mBitmap = null;
    private ByteBuffer mBitmapBuf;
    private String mFilePath;

    private int mTargetWidth;
    private int mTargetHeight;
    private int mDepthBufferWidth;
    private int mDepthBufferHeight;
    private byte[] mDepthBuffer = null;

    /**
     * refocus algorithm mode. REFOCUS_MODE_FULL REFOCUS_MODE_DEPTH_AND_XMP.
     */
    public enum RefocusMode {
        REFOCUS_MODE_FULL,
        REFOCUS_MODE_DEPTH_AND_XMP,
    }

    /**
     * initialize XmpOperator.
     */
    public RefocusImageJni() {
        mXmpOperator = new XmpOperator();
    }

    /**
     * create image refocus.
     *
     * @param targetFilePath
     *            file path
     * @param mode
     *            refocus mode <1> REFOCUS_MODE_FULL
     *            <2>REFOCUS_MODE_DEPTH_AND_XMP
     * @return success->true,fail->false
     */
    public boolean createImageRefocus(String targetFilePath, RefocusMode mode) {
        synchronized (SLOCK) {
            int refocusMode = 0;
            boolean res = mXmpOperator.initialize(targetFilePath);
            if (!res) {
                MtkLog.d(TAG, "mXmpOperator.initialize fail!!!!!");
                return false;
            }
            mStereoDebugInfo = mXmpOperator.getStereoDebugInfoFromFile(targetFilePath);
            MtkLog.d(TAG, "StereoDebugInfo:" + mStereoDebugInfo.toString());
            switch (mode) {
            case REFOCUS_MODE_FULL:
                refocusMode = 0;
                break;

            case REFOCUS_MODE_DEPTH_AND_XMP:
                refocusMode = 2;
                break;

            default:
                break;
            }
            nativeImageRefocus(mStereoDebugInfo.jpsWidth, mStereoDebugInfo.jpsHeight,
                    mStereoDebugInfo.maskWidth, mStereoDebugInfo.maskHeight, mStereoDebugInfo.posX,
                    mStereoDebugInfo.posY, mStereoDebugInfo.viewWidth, mStereoDebugInfo.viewHeight,
                    mStereoDebugInfo.orientation, mStereoDebugInfo.mainCamPos,
                    mStereoDebugInfo.touchCoordX1st, mStereoDebugInfo.touchCoordY1st, refocusMode,
                    mStereoDebugInfo.depthRotation);
            return true;
        }
    }

    /**
     * initialize image refocus, if no depth, generate depth, if include.
     * depth->transfer it to JNI
     *
     * @param targetFilePath
     *            file path
     * @param targetWidth
     *            target image width
     * @param targetHeight
     *            target image height
     * @return success->true,fail->false
     */
    public boolean initImageRefocus(String targetFilePath, int targetWidth, int targetHeight) {
        synchronized (SLOCK) {
            boolean initResult = false;
            if (getDepthInfoFromFile(targetFilePath)) {
                initResult = initRefocusWithDepthMap(targetFilePath, targetWidth, targetHeight);
            } else {
                initResult = initRefocusNoDepthMap(targetFilePath, targetWidth, targetHeight);
            }
            MtkLog.d(TAG, "<initImageRefocus> initResult:" + initResult);
            return initResult;
        }
    }

    /**
     * get depth from file.
     *
     * @param targetFilePath
     *            file path
     * @return success->true,fail->false
     */
    public boolean getDepthInfoFromFile(String targetFilePath) {
        synchronized (SLOCK) {
            mDepthBufferInfo = mXmpOperator.getDepthBufferInfoFromFile(targetFilePath);
            if (mDepthBufferInfo != null) {
                mDepthBuffer = mDepthBufferInfo.depthData;
                mDepthBufferWidth = mDepthBufferInfo.depthBufferWidth;
                mDepthBufferHeight = mDepthBufferInfo.depthBufferHeight;
                return true;
            }
            return false;
        }
    }

    /**
     * initialize refocus, get JPS from file and generate depth.
     *
     * @param targetFilePath
     *            file path
     * @param targetWidth
     *            target width
     * @param targetHeight
     *            target height
     * @return success->true,fail->false
     */
    public boolean initRefocusNoDepthMap(String targetFilePath, int targetWidth, int targetHeight) {
        synchronized (SLOCK) {
            int jpsBufferSize = 0;
            int maskBufferSize = 0;
            boolean initResult = false;
            mTargetWidth = targetWidth;
            mTargetHeight = targetHeight;
            byte[] jpsBuffer = mXmpOperator.getJpsDataFromJpgFile(targetFilePath);
            if (jpsBuffer != null) {
                jpsBufferSize = jpsBuffer.length;
            } else {
                MtkLog.d(TAG, "<initRefocusNoDepthMap> null jps buffer");
                return false;
            }
            MtkLog.d(TAG, "jpsBufferSize:" + jpsBufferSize);
            byte[] maskBuffer = mXmpOperator.getJpsMaskFromJpgFile(targetFilePath);
            if (maskBuffer != null) {
                maskBufferSize = maskBuffer.length;
            } else {
                MtkLog.d(TAG, "<initRefocusNoDepthMap> null mask buffer");
                return false;
            }
            byte[] jpgBuf = StereoImage.getClearImage(targetFilePath);
            if (jpgBuf != null) {
                initResult = nativeInitRefocusNoDepthMapUseJpgBuf(jpgBuf, jpgBuf.length,
                        targetWidth, targetHeight, mStereoDebugInfo.orientation, jpsBuffer,
                        jpsBufferSize, mStereoDebugInfo.jpsWidth, mStereoDebugInfo.jpsHeight,
                        maskBuffer, maskBufferSize, mStereoDebugInfo.maskWidth,
                        mStereoDebugInfo.maskHeight);
            } else {
                initResult = nativeInitRefocusNoDepthMap(targetFilePath, targetWidth,
                        targetHeight, mStereoDebugInfo.orientation, jpsBuffer, jpsBufferSize,
                        mStereoDebugInfo.jpsWidth, mStereoDebugInfo.jpsHeight, maskBuffer,
                        maskBufferSize, mStereoDebugInfo.maskWidth, mStereoDebugInfo.maskHeight);
            }

            if (!initResult) {
                MtkLog.d(TAG, "<initRefocusNoDepthMap> initRefocusNoDepthMap error!!");
                return false;
            }
            return saveDepthBufToJpg(targetFilePath);
        }
    }

    /**
     * get depth buffer.
     *
     * @return depth
     */
    public XmpOperator.DepthBufferInfo getDepthInfo() {
        return mDepthBufferInfo;
    }

    /**
     * generate new refocus image under the new image coordinates and depth of
     * filed.
     *
     * @param xCoord
     *            x coordinates
     * @param yCoord
     *            y coordinates
     * @param depthofFiled
     *            depth of filed
     */
    public void generateRefocusImage(int xCoord, int yCoord, int depthofFiled) {
        xCoord = xCoord / INSAMPLESIZE;
        yCoord = yCoord / INSAMPLESIZE;
        long createBmpStart = System.currentTimeMillis();
        int length = mTargetWidth * mTargetHeight * TEMP_NUM4;
        mBitmapBuf = ByteBuffer.allocate(length);
        long ssCreatBmp = System.currentTimeMillis();
        mBitmap = Bitmap.createBitmap(mTargetWidth, mTargetHeight, mConfig);
        MtkLog.d(TAG, "ssCreatBmp Time =    " + (System.currentTimeMillis() - ssCreatBmp)
                + "|| width = " + mTargetWidth + "  *  height = " + mTargetHeight);
        byte[] byteArray = new byte[length];
        MtkLog.d(TAG, "Performance Create Bitmap Time =    "
                + (System.currentTimeMillis() - createBmpStart));
        MtkLog.d(TAG, "generateRefocusImage,begin  x " + xCoord + " y " + yCoord
                + " depthoffiled " + depthofFiled);
        long generStartTime = System.currentTimeMillis();
        nativeGenerateRefocusImage(byteArray, xCoord, yCoord, depthofFiled);
        MtkLog.d(TAG, "Performance  DepthInfoJni.generateRefocusImage pend time ="
                + (System.currentTimeMillis() - generStartTime));
        long bmpRewindStart = System.currentTimeMillis();

        mBitmapBuf.put(byteArray);
        mBitmapBuf.rewind();
        mBitmap.copyPixelsFromBuffer(mBitmapBuf);

        MtkLog.d(TAG, "Performance bmpRewind Spend Time =     "
                + (System.currentTimeMillis() - bmpRewindStart));
    }

    /**
     * get new bitmap that get from algorithm.
     *
     * @return bitmap
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * save refocus image to specified URI.
     *
     * @param sourceUri
     *            source URI
     * @param context
     *            context
     * @param replaceBlurImage
     *            if set as true, clear image will be replaced with blur image
     * @return new Uri
     */
    public Uri saveRefocusImage(Uri sourceUri, Context context, boolean replaceBlurImage) {
        String filename = new SimpleDateFormat(TIME_STAMP_NAME).format(new Date(System
                .currentTimeMillis()));
        File file = RefocusHelper.getNewFile(context, sourceUri, filename);
        long begin = System.currentTimeMillis();
        nativeSaveRefocusImage(file.getAbsolutePath(), INSAMPLESIZE);
        MtkLog.d(TAG, "performance saveRefocusImage costs " + (System.currentTimeMillis() - begin));

        if (replaceBlurImage) {
            String srcPath = RefocusHelper.getFilePathFromUri(context, sourceUri);
            mXmpOperator.replaceBlurImage(srcPath, file.getAbsolutePath());
            file.delete();
            return RefocusHelper.updateContent(context, sourceUri, new File(srcPath));
        } else {
            return RefocusHelper.insertContent(context, sourceUri, file, filename);
        }
    }

    /**
     * get depth buffer.
     *
     * @return depth buffer
     */
    public byte[] getDepthBuffer() {
        return mDepthBuffer;
    }

    /**
     * get depth buffer width.
     *
     * @return depth width
     */
    public int getDepBufWidth() {
        return mDepthBufferWidth;
    }

    /**
     * get depth height.
     *
     * @return depth height
     */
    public int getDepBufHeight() {
        return mDepthBufferHeight;
    }

    /**
     * get default focus x coordinate.
     *
     * @return x coordinate
     */
    public int getDefaultFocusCoordX() {
        return mStereoDebugInfo.touchCoordX1st;
    }

    /**
     * get default focus y coordinate.
     *
     * @return y coordinate
     */
    public int getDefaultFocusCoordY() {
        return mStereoDebugInfo.touchCoordY1st;
    }

    /**
     * get face region.
     *
     * @param imageWidth
     *            image width
     * @param imageHeight
     *            image height
     * @return face region
     */
    public Rect getDefaultFaceRect(int imageWidth, int imageHeight) {
        if (mStereoDebugInfo.faceCount >= 1) {
            XmpOperator.FaceDetectionInfo fdInfo = mStereoDebugInfo.fdInfoArray.get(0);
            return StereoUtils.getFaceRect(imageWidth, imageHeight, fdInfo.mFaceLeft,
                    fdInfo.mFaceTop, fdInfo.mFaceRight, fdInfo.mFaceBottom);
        }
        return null;
    }

    /**
     * release memory.
     */
    public void release() {
        MtkLog.d(TAG, "refocusRelease");
        if (mXmpOperator != null) {
            mXmpOperator.deInitialize();
        }
        nativeRelease();
    }

    private boolean initRefocusWithDepthMap(String targetFilePath, int targetWidth,
            int targetHeight) {
        synchronized (SLOCK) {
            boolean initResult = false;
            mTargetWidth = targetWidth;
            mTargetHeight = targetHeight;
            byte[] jpgBuf = StereoImage.getClearImage(targetFilePath);

            if (jpgBuf != null) {
                initResult = nativeInitRefocusWithDepthMapUseJpgBuf(jpgBuf, jpgBuf.length,
                        targetWidth,
                        targetHeight, mStereoDebugInfo.orientation, mDepthBuffer,
                        mDepthBuffer.length,
                        mStereoDebugInfo.jpsWidth, mStereoDebugInfo.jpsHeight);
            } else {
                initResult = nativeInitRefocusWithDepthMap(targetFilePath, targetWidth,
                        targetHeight, mStereoDebugInfo.orientation, mDepthBuffer,
                        mDepthBuffer.length,
                        mStereoDebugInfo.jpsWidth, mStereoDebugInfo.jpsHeight);
            }

            MtkLog.d(TAG, "<initRefocusWithDepthMap> initResult: " + initResult);
            return initResult;
        }
    }

    private boolean saveDepthBufToJpg(String targetFilePath) {
        int depthBufferSize = nativeGetDepthBufferSize();
        int xmpDepthBufferSize = nativeGetXMPDepthBufferSize();
        int depthBufferWidth = nativeGetDepthBufferWidth();
        int depthBufferHeight = nativeGetDepthBufferHeight();
        int xmpDepthBufferWidth = nativeGetXMPDepthBufferWidth();
        int xmpDepthBufferHeight = nativeGetXMPDepthBufferHeight();
        int metaBufferWidth = nativeGetMetaBufferWidth();
        int metaBufferHeight = nativeGetMetaBufferHeight();
        if (depthBufferSize <= 0 || xmpDepthBufferSize <= 0) {
            MtkLog.d(TAG, "<initRefocusNoDepthMap> depthbuffer<=0, error!!");
            return false;
        }
        byte[] byteArray = new byte[depthBufferSize];
        byte[] xmpByteArray = new byte[xmpDepthBufferSize];
        mDepthBufferInfo = new XmpOperator.DepthBufferInfo();
        nativeSaveDepthMapInfo(byteArray, xmpByteArray);
        mDepthBufferInfo.depthData = byteArray;
        mDepthBufferInfo.xmpDepthData = xmpByteArray;
        mDepthBufferInfo.depthBufferHeight = depthBufferHeight;
        mDepthBufferInfo.depthBufferWidth = depthBufferWidth;
        mDepthBufferInfo.xmpDepthHeight = xmpDepthBufferHeight;
        mDepthBufferInfo.xmpDepthWidth = xmpDepthBufferWidth;
        mDepthBufferInfo.metaBufferHeight = metaBufferHeight;
        mDepthBufferInfo.metaBufferWidth = metaBufferWidth;
        mXmpOperator.writeDepthBufferToJpg(targetFilePath, mDepthBufferInfo, true);
        mDepthBufferWidth = depthBufferWidth;
        mDepthBufferHeight = depthBufferHeight;
        mDepthBuffer = byteArray;
        return true;
    }

    static {
        System.loadLibrary("jni_image_refocus");
    }

    // image refocus JNI API
    private static native void nativeImageRefocus(int jpsWidth, int jpsHeight, int maskWidth,
            int maskHeight, int posX, int posY, int viewWidth, int viewHeight, int orientation,
            int mainCamPos, int touchCoordX1st, int touchCoordY1st, int refocusMode,
            int depthRotation);

    private static native boolean nativeInitRefocusNoDepthMap(String sourceFilePath,
            int outImgWidth, int outImgHeight, int imgOrientation, byte[] jpsBuffer,
            int jpsBufferSize, int inStereoImgWidth, int inStereoImgHeight, byte[] maskBuffer,
            int maskBufferSize, int maskWidth, int maskHeight);

    private static native boolean nativeInitRefocusNoDepthMapUseJpgBuf(byte[] jpgBuffer,
            int jpgBufferSize,
            int outImgWidth, int outImgHeight, int imgOrientation, byte[] jpsBuffer,
            int jpsBufferSize, int inStereoImgWidth, int inStereoImgHeight, byte[] maskBuffer,
            int maskBufferSize, int maskWidth, int maskHeight);

    private static native boolean nativeInitRefocusWithDepthMap(String targetFilePath,
            int outImgWidth, int outImgHeight, int imgOrientation, byte[] depthMapBuffer,
            int depthMapBufferSize, int inStereoImgWidth, int inStereoImgHeight);

    private static native boolean nativeInitRefocusWithDepthMapUseJpgBuf(byte[] jpegBuffer,
            int jpgBufferSize,
            int outImgWidth, int outImgHeight, int imgOrientation, byte[] depthMapBuffer,
            int depthMapBufferSize, int inStereoImgWidth, int inStereoImgHeight);

    private static native boolean nativeGenerateRefocusImage(byte[] resultBuffer, int touchX,
            int touchY, int depthOfField);

    private static native int nativeGetDepthBufferSize();

    private static native int nativeGetDepthBufferWidth();

    private static native int nativeGetDepthBufferHeight();

    private static native int nativeGetXMPDepthBufferSize();

    private static native int nativeGetXMPDepthBufferWidth();

    private static native int nativeGetXMPDepthBufferHeight();

    private static native int nativeGetMetaBufferWidth();

    private static native int nativeGetMetaBufferHeight();

    private static native void nativeSaveDepthMapInfo(byte[] depthBuffer, byte[] xmpDepthBuffer);

    private static native void nativeSaveRefocusImage(String saveFileName, int inSampleSize);

    private static native void nativeRelease();
}
