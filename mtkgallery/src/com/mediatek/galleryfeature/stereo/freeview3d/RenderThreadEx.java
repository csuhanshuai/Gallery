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
package com.mediatek.galleryfeature.stereo.freeview3d;

import android.os.Process;

import com.mediatek.galleryframework.util.MtkLog;

/**
 * RenderThreadEx should advance animation and draw frame After wake up from sleep.
 * which should enter waiting state, while the animation is finished.
 */
public class RenderThreadEx extends Thread {
    private static final String TAG = "MtkGallery2/RenderThreadEx";
    private OnDrawFrameListener mOnDrawMavFrameListener = null;
    private Object mRenderLock = new Object();
    private boolean mRenderRequested = false;
    private boolean mIsActive = true;

    /**
     * The listener for update animation state.
     */
    public interface OnDrawFrameListener {
        /**
         * Update animation.
         * @return whether the animation is finished or not.
         */
        public boolean advanceAnimation();

        /**
         * Draw the current frame.
         */
        public void drawFrame();

        /**
         * Get the thread sleep time for interval.
         * @return The sleep time.
         */
        public int getSleepTime();
    }

    /**
     * Set flag for update animation.
     * @param request whether this should update animation.
     */
    public void setRenderRequester(boolean request) {
        synchronized (mRenderLock) {
            mRenderRequested = request;
            mRenderLock.notifyAll();
        }
    }

    /**
     * Quit the thread.
     */
    public void quit() {
        mIsActive = false;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
        while (mIsActive && !Thread.currentThread().isInterrupted()) {
            MtkLog.d(TAG, "<run>~~~~~~~~~~~~~~~~~"
                    + Thread.currentThread().getId() + " mRenderRequested=="
                    + mRenderRequested);
            if (!mIsActive) {
                MtkLog.v(TAG, "<run>MavRenderThread:run: exit MavRenderThread");
                return;
            }
            boolean isFinished = false;
            synchronized (mRenderLock) {
                if (mRenderRequested && mOnDrawMavFrameListener != null) {
                    isFinished = mOnDrawMavFrameListener.advanceAnimation();
                    mRenderRequested = (!isFinished) ? true : false;
                    mOnDrawMavFrameListener.drawFrame();
                } else {
                    try {
                        mRenderLock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            if (!isFinished) {
                try {
                    if (mOnDrawMavFrameListener != null) {
                        Thread.sleep(mOnDrawMavFrameListener.getSleepTime());
                    }
                } catch (InterruptedException e) {
                    MtkLog.e(
                            TAG,
                            " <run> sleep have InterruptedException:"
                                    + e.getMessage());
                }
            }
        }
    }

    public void setOnDrawFrameListener(OnDrawFrameListener lisenter) {
        mOnDrawMavFrameListener = lisenter;
    }
}
