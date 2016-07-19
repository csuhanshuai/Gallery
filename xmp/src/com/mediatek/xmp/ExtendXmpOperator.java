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
package com.mediatek.xmp;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Extend XmpOperator.
 * Used to write extend xmp data to APP1.
 */
public class ExtendXmpOperator {
    private static final String TAG = "mtkGallery2/Xmp/ExtendXmpOperator";
    public static final String XMP_EXT_HEADER = "http://ns.adobe.com/xmp/extension/";
    public static final int MD5_BYTE_COUNT = 32;
    public static final int TOTAL_LENGTH_BYTE_COUNT = 4;
    public static final int PARTITION_OFFSET_BYTE_COUNT = 4;
    public static final int EXT_XMP_COMMON_HEADER_TOTAL_LEN_OFFSET = XMP_EXT_HEADER.length() + 1
            + MD5_BYTE_COUNT;
    public static final int EXT_XMP_COMMON_HEADER_REAL_DATA_OFFSET =
            EXT_XMP_COMMON_HEADER_TOTAL_LEN_OFFSET
            + TOTAL_LENGTH_BYTE_COUNT + PARTITION_OFFSET_BYTE_COUNT;

    private static final String NS_GIMAGE = "http://ns.google.com/photos/1.0/image/";
    private static final String NS_GDEPTH = "http://ns.google.com/photos/1.0/depthmap/";

    private static final String ATTRIBUTE_GIMAGE_DATA = "Data";
    private static final String ATTRIBUTE_GDEPTH_DATA = "Data";

    private static final String PRIFIX_GIMAGE = "GImage";
    private static final String PRIFIX_GDEPTH = "GDepth";

    private static final float BYTE_TO_MB = 1024 * 1024;
    private static final int XMP_HEADER_TAIL_BYTE = 0X0;
    private static final int LOW_HALF_BYTE_MASK = 0X0F;
    private static final int HIGH_HALF_BYTE_MASK = 0XF0;
    private static final int BYTE_MASK = 0XFF;
    private static final int SHIFT_BIT_COUNT_4 = 4;
    private static final int SHIFT_BIT_COUNT_8 = 8;
    private static final int BYTE_COUNT_4 = 4;
    private static final int MAX_BYTE_PER_APP1 = 0XFFB2;
    private static final int XMP_COMMON_HEADER_LEN = XMP_EXT_HEADER.getBytes().length + 1
            + MD5_BYTE_COUNT + TOTAL_LENGTH_BYTE_COUNT + PARTITION_OFFSET_BYTE_COUNT;
    private static final int MAX_LEN_FOR_REAL_XMP_DATA_PER_APP1 = MAX_BYTE_PER_APP1
            - XMP_COMMON_HEADER_LEN;

    private ExtendXmpInterface mXmpInterface;
    private boolean mIsDumpEnabled;
    private String mDumpPath;
    private String mValueOfMd5;

    /**
     * ExtendXmpOperator constructor.
     */
    public ExtendXmpOperator() {
        mXmpInterface = new ExtendXmpInterface();
    }

    /**
     * Enable or disable debuging.
     *
     * @param enableDump
     *            enable or disable debuging
     * @param dumpPath
     *            dump path when enable dumping
     */
    public void debugConfig(boolean enableDump, String dumpPath) {
        mIsDumpEnabled = enableDump;
        mDumpPath = dumpPath;
    }

    /**
     * Make extend xmp data.
     *
     * @param clearImage
     *            clear image
     * @param gDepthMapSmall
     *            google small depthMap
     * @return extend xmp which has been divided into section
     */
    public ArrayList<byte[]> makeXmpExtData(byte[] clearImage, byte[] gDepthMapSmall) {
        if (mXmpInterface == null || (clearImage == null && gDepthMapSmall == null)) {
            Log.d(TAG, "<makeXmpExtData> mXmpInterface is null or jade implement!!");
            return new ArrayList<byte[]>();
        }
        mXmpInterface.prepareSerialization();
        mXmpInterface.registerNameSpace(NS_GIMAGE, PRIFIX_GIMAGE);
        mXmpInterface.registerNameSpace(NS_GDEPTH, PRIFIX_GDEPTH);
        mXmpInterface.setProperty(PRIFIX_GIMAGE, ATTRIBUTE_GIMAGE_DATA, base64Encode(clearImage));
        mXmpInterface.setProperty(PRIFIX_GDEPTH, ATTRIBUTE_GDEPTH_DATA,
                base64Encode(gDepthMapSmall));
        byte[] serializedData = mXmpInterface.serializeXml();
        if (mIsDumpEnabled) {
            XmpInterface.writeBufferToFile(mDumpPath + "ExtendXMP_RealData_Written.xml",
                    serializedData);
        }
        return makeExtXmpDataInternal(serializedData);
    }

    /**
     * Update ext xmp with new google depth map.
     *
     * @param srcXmpBuffer
     *            old ext xmp buffer
     * @param gDepthMap
     *            new google depth map
     * @return serialized ext xmp data with header
     */
    public ArrayList<byte[]> updateGoogleDepthMap(byte[] srcXmpBuffer, byte[] gDepthMap) {
        if (mXmpInterface == null || srcXmpBuffer == null || gDepthMap == null) {
            Log.d(TAG, "<updateGoogleDepthMap> params error!!");
            return new ArrayList<byte[]>();
        }
        Log.d(TAG, "<updateGoogleDepthMap> begin!!");
        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(srcXmpBuffer);
            mXmpInterface.prepareUpdating(in);
            mXmpInterface
                    .setProperty(PRIFIX_GDEPTH, ATTRIBUTE_GDEPTH_DATA, base64Encode(gDepthMap));
            byte[] serializedData = mXmpInterface.serializeXml();
            if (mIsDumpEnabled) {
                XmpInterface.writeBufferToFile(mDumpPath
                        + "ExtendXMP_Updating_RealData_Written.xml", serializedData);
            }
            Log.d(TAG, "<updateGoogleDepthMap> end!!");
            return makeExtXmpDataInternal(serializedData);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(TAG, "<updateGoogleDepthMap> IOException", e);
                }
            }
        }
    }

    /**
     * Get clear image buffer.
     *
     * @return clear image buffer
     */
    public byte[] getImageBuffer() {
        if (mXmpInterface == null) {
            Log.d(TAG, "<getImageBuffer> mXmpInterface is null, error!!");
            return null;
        }
        String value = mXmpInterface.getProperty(PRIFIX_GIMAGE, ATTRIBUTE_GIMAGE_DATA);
        if (value == null) {
            Log.d(TAG, "<getImageBuffer> value is null, error!!");
            return null;
        }
        return base64Decode(value);
    }

    /**
     * Get google depth map.
     *
     * @return google depth map
     */
    public byte[] getGoogleDepthMap() {
        if (mXmpInterface == null) {
            Log.d(TAG, "<getGoogleDepthMap> mXmpInterface is null, error!!");
            return null;
        }
        String value = mXmpInterface.getProperty(PRIFIX_GDEPTH, ATTRIBUTE_GDEPTH_DATA);
        if (value == null) {
            Log.d(TAG, "<getGoogleDepthMap> value is null, error!!");
            return null;
        }
        return base64Decode(value);
    }

    /**
     * Set extend xmp source buffer.
     *
     * @param srcBuffer
     *            souce buffer
     */
    public void setSrcBuffer(byte[] srcBuffer) {
        if (mXmpInterface == null) {
            Log.d(TAG, "<setSrcBuffer> mXmpInterface is null, error!!");
            return;
        }
        mXmpInterface.prepareParsing(srcBuffer);
    }

    /**
     * Get md5 of extend xmp data.
     *
     * @return 32 bytes md5 string
     */
    public String getMd5Value() {
        if (mValueOfMd5 == null) {
            return "";
        }
        return mValueOfMd5;
    }

    private String base64Encode(byte[] in) {
        float currentTime = System.currentTimeMillis();
        String out = Base64.encodeToString(in, Base64.DEFAULT);
        Log.d(TAG, "<base64Encode> buffer length(MB) " + in.length / BYTE_TO_MB + ", costs "
                + (System.currentTimeMillis() - currentTime));
        return out;
    }

    private byte[] base64Decode(String base64String) {
        float currentTime = System.currentTimeMillis();
        byte[] out = Base64.decode(base64String, Base64.DEFAULT);
        Log.d(TAG, "<base64Decode> string length(MB) " + base64String.length() / BYTE_TO_MB
                + ", costs " + (System.currentTimeMillis() - currentTime));
        return out;
    }

    private static byte[] md5(byte[] in) {
        if (in == null || in.length <= 0) {
            Log.d(TAG, "<md5> input error!!");
            return null;
        }
        byte[] out = null;
        try {
            MessageDigest digestor = MessageDigest.getInstance("MD5");
            digestor.update(in);
            out = digestor.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "<md5> NoSuchAlgorithmException ", e);
        }
        return out;
    }

    private static String getMd5(byte[] in) {
        byte[] md5 = md5(in);
        if (md5 == null || md5.length <= 0) {
            Log.d(TAG, "<getMd5> error!!");
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int len = md5.length;
        for (int i = 0; i < len; i++) {
            int high = (md5[i] & HIGH_HALF_BYTE_MASK) >> SHIFT_BIT_COUNT_4;
            int low = md5[i] & LOW_HALF_BYTE_MASK;
            sb.append(Integer.toHexString(high));
            sb.append(Integer.toHexString(low));
        }
        return sb.toString().toUpperCase();
    }

    private byte[] intToByteBuffer(int value, int byteCount) {
        int in = value;
        byte[] byteBuffer = new byte[byteCount];
        for (int i = byteCount - 1; i >= 0; i--) {
            byteBuffer[i] = (byte) (in & BYTE_MASK);
            in = in >> SHIFT_BIT_COUNT_8;
        }
        return byteBuffer;
    }

    private byte[] getXmpCommonHeader(String md5, int totalLength, int sectionNumber) {
        int offset = MAX_LEN_FOR_REAL_XMP_DATA_PER_APP1 * sectionNumber;
        byte[] header = new byte[XMP_COMMON_HEADER_LEN];
        // 1. copy header
        System.arraycopy(XMP_EXT_HEADER.getBytes(), 0, header, 0, XMP_EXT_HEADER.length());
        int currentPos = XMP_EXT_HEADER.length();
        // 2. copy tail byte
        header[currentPos] = XMP_HEADER_TAIL_BYTE;
        currentPos += 1;
        // 3. copy md5
        byte[] md5Buffer = md5.getBytes();
        System.arraycopy(md5Buffer, 0, header, currentPos, md5Buffer.length);
        currentPos += md5Buffer.length;
        // 4. copy 4 bytes totalLen
        byte[] totalLenBuffer = intToByteBuffer(totalLength, BYTE_COUNT_4);
        System.arraycopy(totalLenBuffer, 0, header, currentPos, totalLenBuffer.length);
        currentPos += totalLenBuffer.length;
        // 5. copy 4 bytes offset
        byte[] offsetBuffer = intToByteBuffer(offset, BYTE_COUNT_4);
        System.arraycopy(offsetBuffer, 0, header, currentPos, offsetBuffer.length);
        return header;
    }

    private ArrayList<byte[]> makeExtXmpDataInternal(byte[] extXmpData) {
        ArrayList<byte[]> extXmpDataArray = new ArrayList<byte[]>();
        if (extXmpData == null) {
            Log.d(TAG, "<makeExtXmpDataInternal> extXmpData is null, error!!");
            return extXmpDataArray;
        }

        mValueOfMd5 = getMd5(extXmpData);
        int sectionCount = extXmpData.length / MAX_LEN_FOR_REAL_XMP_DATA_PER_APP1 + 1;
        byte[] commonHeader = null;
        byte[] section = null;
        int currentPos = 0;
        for (int i = 0; i < sectionCount; i++) {
            commonHeader = getXmpCommonHeader(mValueOfMd5, extXmpData.length, i);
            if (i == sectionCount - 1) {
                int sectionLen = extXmpData.length % MAX_LEN_FOR_REAL_XMP_DATA_PER_APP1
                        + commonHeader.length;
                section = new byte[sectionLen];
                // 1. copy header
                System.arraycopy(commonHeader, 0, section, 0, commonHeader.length);
                // 2. copy data
                System.arraycopy(extXmpData, currentPos, section, commonHeader.length, sectionLen
                        - commonHeader.length);
                currentPos += sectionLen - commonHeader.length;
            } else {
                section = new byte[MAX_BYTE_PER_APP1];
                // 1. copy header
                System.arraycopy(commonHeader, 0, section, 0, commonHeader.length);
                // 2. copy data
                System.arraycopy(extXmpData, currentPos, section, commonHeader.length,
                        MAX_BYTE_PER_APP1 - commonHeader.length);
                currentPos += MAX_BYTE_PER_APP1 - commonHeader.length;
            }
            extXmpDataArray.add(i, section);
        }
        return extXmpDataArray;
    }
}