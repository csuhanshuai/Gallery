/*
 * Copyright (C) 2011-2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file is auto-generated. DO NOT MODIFY!
 * The source Renderscript file: packages/apps/Gallery2/src/com/android/gallery3d/filtershow/filters/grey.rs
 */

package com.android.gallery3d.filtershow.filters;

/**
 * @hide
 */
public class greyBitCode {
    // return byte array representation of the 32-bit bitcode.
    public static byte[] getBitCode32() {
        return getBitCode32Internal();
    };

    private static byte[] getSegment32_0() {
        byte[] data = {
             -34,  -64,   23,   11,    0,    0,    0,    0,   44,    0,    0,    0,  100,    3,    0,    0,
               0,    0,    0,    0,   21,    0,    0,    0,    0,    0,    0,    0,    1,   64,    4,    0,
              -4,    8,    0,    0,    2,   64,    4,    0,    3,    0,    0,    0,   66,   67,  -64,  -34,
              33,   12,    0,    0,  -42,    0,    0,    0,    1,   16,    0,    0,   18,    0,    0,    0,
               7, -127,   35, -111,   65,  -56,    4,   73,    6,   16,   50,   57, -110,    1, -124,   12,
              37,    5,    8,   25,   30,    4, -117,   98, -128,   12,   69,    2,   66, -110,   11,   66,
             100,   16,   50,   20,   56,    8,   24,   73,   10,   50,   68,   36,   72,   10, -112,   33,
              35,  -60,   82, -128,   12,   25,   33,  114,   36,    7,  -56,  -56,   16,   98,  -88,  -96,
             -88,   64,  -58,  -16,    1,    0,    0,    0,   73,   24,    0,    0,    3,    0,    0,    0,
              11,    8,   32,   -8,   -1,   -1,   -1,   -1,    1, -116,    0,    0, -119,   32,    0,    0,
              12,    0,    0,    0,   50,   34,  -56,    8,   32,  100, -123,    4, -109,   33,  -92, -124,
               4, -109,   33,  -29, -124,  -95, -112,   20,   18,   76, -122, -116,   11, -124,  100,   76,
              16,   28,  115,    4,  -56,   48,    2,    1,   20,    1,   33,   13,    4,  -52,   17, -128,
             -63,    8,    0,    0,   19,  -80,  112, -112, -121,  118,  -80, -121,   59,  104,    3,  119,
             120,    7,  119,   40, -121,   54,   96, -121,  116,  112, -121,  122,  -64, -121,   54,   56,
               7,  119,  -88, -121,  114,    8,    7,  113,   72, -121,   13,  100,   80,   14,  109,    0,
              15,  122,   48,    7,  114,  -96,    7,  115,   32,    7,  109, -112,   14,  118,   64,    7,
             122,   96,    7,  116,  -48,    6,  -10,   16,    7,  114, -128,    7,  122,   96,    7,  116,
             -96,    7,  113,   32,    7,  120,  -48,    6,  -18,   48,    7,  114,  -48,    6,  -77,   96,
               7,  116,   48,   68,   25,    0,    0,    8,    0,    0,    0, -128,   44,   16,    0,    0,
               5,    0,    0,    0,   50,   30, -104,    8,   25,   17,   76, -112, -116,    9,   38,   71,
             -58,    4,   67,  106,    9,   20,    2,    0,  121,   24,    0,    0,   71,    0,    0,    0,
              26,    3,   76, -112,   70,    2,   19,   52,   68,   40,    2,   42,  119,   99,  104,   97,
             114,   95,  115,  105,  122,  101,   67, -124,   66,   24,   98,   20,    1,   17,   20,    2,
            -101,  -74,   52,  -73,  -81,   50,  -73,  -70,  -74,  -81,  -71,   52,  -67,  -78,   33,   70,
              17,   16,   68,   33,   80,   26,   99,   11,  115,   59,    3,  -79,   43, -109, -101,   75,
             123,  115,    3, -103,  113,  -79,    1,   25,   34,   16,    6,   15,  -69,   50,  -71,  -71,
             -76,   55,   55,    6,   49,   67,    8,    2,   33,   18,   70,  106,   97,  118,   97,   95,
             112,   97,   99,  107,   97,  103,  101,   95,  110,   97,  109,  101,   68, -125,  -79,  -73,
              54, -105,   48,   55,   50,  -71,  -73,   52,   50, -105,  -77,   48,   54,  -74,   50,  -71,
             -68,   25,   50,   23,  -77,   52,   54,  -70,   50,  -71,   57,  -76,  -73,   59,   23,  -77,
              52,   54,  -70,   50,  -71,  -71,   33,    4,  -79,   16,   12,    9,  -71,  -73,   55,  -70,
              33,    2,  -31,  -16, -112,   58,   18,   10,  -94,  123,   11,   26,   34,   16,   16,    3,
            -104,   33,    2,   33,   81, -104,  -87,   25,   34,   16,   84,   35,   54,   54,  -69,   54,
            -105,  -74,   55,  -78,   58,  -74,   50,   23,   51,  -74,  -80,  -77,  -71,   41,  -62,   80,
              84,   97,   99,  -77,  107,  115,   73,   35,   43,  115,  -93, -101,   18,   28,   61,   70,
             -32,  -28,  -62,  -50,  -38,  -62,  -90,    8,   74,  -45,  107,   68,  110,  -18,  -85,   12,
              15,  -18,   77, -114,  -18,  -53,  -20,   77,  -82,   44,  108,   12,  -19,  -53,   45,  -84,
             -83,  108, -118,  -16,   68, -107,   70,  -28,  -26,  -66,  -54,  -16,  -32,  -34,  -28,  -24,
             -66,  -52,  -34,  -28,  -54,  -62,  -58,  -48,  -90,    8,   83,    5,  121,   24,    0,    0,
              50,    0,    0,    0,   51,    8, -128,   28,  -60,  -31,   28,  102,   20,    1,   61, -120,
              67,   56, -124,  -61, -116,   66, -128,    7,  121,  120,    7,  115, -104,  113,   12,  -26,
               0,   15,  -19,   16,   14,  -12, -128,   14,   51,   12,   66,   30,  -62,  -63,   29,  -50,
             -95,   28,  102,   48,    5,   61, -120,   67,   56, -124, -125,   27,  -52,    3,   61,  -56,
              67,   61, -116,    3,   61,  -52,  120, -116,  116,  112,    7,  123,    8,    7,  121,   72,
            -121,  112,  112,    7,  122,  112,    3,  118,  120, -121,  112,   32, -121,   25,  -52,   17,
              14,  -20, -112,   14,  -31,   48,   15,  110,   48,   15,  -29,  -16,   14,  -16,   80,   14,
              51,   16,  -60,   29,  -34,   33,   28,  -40,   33,   29,  -62,   97,   30,  102,   48, -119,
              59,  -68, -125,   59,  -48,   67,   57,  -76,    3,   60,  -68, -125,   60, -124,    3,   59,
             -52,  -16,   20,  118,   96,    7,  123,  104,    7,   55,  104, -121,  114,  104,    7,   55,
            -128, -121,  112, -112, -121,  112,   96,    7,  118,   40,    7,  118,   -8,    5,  118,  120,
            -121,  119, -128, -121,   95,    8, -121,  113,   24, -121,  114, -104, -121,  121, -104, -127,
              44,  -18,  -16,   14,  -18,  -32,   14,  -11,  -64,   14,  -20,    0,  113,   32,    0,    0,
               2,    0,    0,    0,    6,  112,  -84,  -32,  -90,   77, -114,    6,   97,   32,    0,    0,
              10,    0,    0,    0,   19,    4,   65,   44,   16,    0,    0,    0,    1,    0,    0,    0,
             -44,   17,    0,    0,   99,    8,    3, -111,   69,   56,   16,    0,    2,    0,    0,    0,
              54,   32,   32,   13,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
        };
        return data;
    }

    private static int bitCode32Length = 912;

    private static byte[] getBitCode32Internal() {
        byte[] bc = new byte[bitCode32Length];
        int offset = 0;
        byte[] seg;
        seg = getSegment32_0();
        System.arraycopy(seg, 0, bc, offset, seg.length);
        offset += seg.length;
        return bc;
    }

    // return byte array representation of the 64-bit bitcode.
    public static byte[] getBitCode64() {
        return getBitCode64Internal();
    };

    private static byte[] getSegment64_0() {
        byte[] data = {
             -34,  -64,   23,   11,    0,    0,    0,    0,   44,    0,    0,    0,   20,    3,    0,    0,
               0,    0,    0,    0,   21,    0,    0,    0,    0,    0,    0,    0,    1,   64,    4,    0,
              -4,    8,    0,    0,    2,   64,    4,    0,    3,    0,    0,    0,   66,   67,  -64,  -34,
              33,   12,    0,    0,  -62,    0,    0,    0,    1,   16,    0,    0,   18,    0,    0,    0,
               7, -127,   35, -111,   65,  -56,    4,   73,    6,   16,   50,   57, -110,    1, -124,   12,
              37,    5,    8,   25,   30,    4, -117,   98, -128,   16,   69,    2,   66, -110,   11,   66,
            -124,   16,   50,   20,   56,    8,   24,   73,   10,   50,   68,   36,   72,   10, -112,   33,
              35,  -60,   82, -128,   12,   25,   33,  114,   36,    7,  -56,    8,   17,   98,  -88,  -96,
             -88,   64,  -58,  -16,    1,    0,    0,    0,   73,   24,    0,    0,    3,    0,    0,    0,
              11, -124,   -1,   -1,   -1,   -1,   31,  -64,    8,    0,    0,    0, -119,   32,    0,    0,
              13,    0,    0,    0,   50,   34,    8,    9,   32,  100, -123,    4,   19,   34,  -92, -124,
               4,   19,   34,  -29, -124,  -95, -112,   20,   18,   76, -120, -116,   11, -124, -124,   76,
              16,   32,  115,    4,  -56,   28,    1,   24,   20,    1, -126,  100,   32,   96,   24, -127,
               0,  -26,    8,   64,   97,    4,    0,    0,   19,  -76,  112,    8,    7,  121,   24,    7,
             116,  -80,    3,   58,  104,    3,  119,  120,    7,  119,   40, -121,   54,   96, -121,  116,
             112, -121,  122,  -64, -121,   54,   56,    7,  119,  -88, -121,  114,    8,    7,  113,   72,
            -121,   13,   97,   80,   14,  109,  -48,   14,  122,   80,   14,  109, -112,   14,  118,   64,
               7,  122,   96,    7,  116,  -48,    6,  -23,   16,    7,  114, -128,    7,  122,   16,    7,
             114, -128,    7,  109,  -32,   14,  115,   32,    7,  122,   96,    7,  116,  -48,    6,  -77,
              16,    7,  114, -128,    7,   67, -108,    1,    0, -128,    0,    0,    0,    0,  -56,  -61,
               0,    0,    0,    0,   55,    0,    0,    0,   26,    3,   76, -112,   70,    2,   19,   68,
             105, -116,   45,  -52,  -19,   12,  -60,  -82,   76,  110,   46,  -19,  -51,   13,  100,  -58,
             -59,    6,  100, -120,   64,    0,   60,  -20,  -54,  -28,  -26,  -46,  -34,  -36,   24,  -60,
              12,   33,    8, -127,   24,   24,  -87, -123,  -39, -123,  125,  -63, -123, -115,  -83, -123,
             -99, -107,  125,  -71, -123,  -75, -107,   17,   13,  -58,  -34,  -38,   92,  -62,  -36,  -56,
             -28,  -34,  -46,  -56,   92,  -50,  -62,  -40,  -40,  -54,  -28,  -14,  102,  -56,   92,  -52,
             -46,  -40,  -24,  -54,  -28,  -26,  -48,  -34,  -18,   92,  -52,  -46,  -40,  -24,  -54,  -28,
             -26, -122,   16,   68,   65,   24,   36,  -28,  -34,  -34,  -24, -122,    8,    4,  -62,   67,
             -22,   72,   40, -120,  -18,   45,  104, -120,   64,   40,   12,   96, -122,    8,    4,   67,
              97,  -90,  102, -120,   64,   56,   85,  -40,  -40,  -20,  -38,   92,  -46,  -56,  -54,  -36,
             -24,  -90,    4,   65, -113,   17,   56,  -71,  -80,  -77,  -74,  -80,   41,    2,  113,  -12,
              26, -111, -101,   -5,   42,  -61, -125,  123, -109,  -93,   -5,   50,  123, -109,   43,   11,
              27,   67,   -5,  114,   11,  107,   43, -101,   34,   36,   75,  -91,   17,  -71,  -71,  -81,
              50,   60,  -72,   55,   57,  -70,   47,  -77,   55,  -71,  -78,  -80,   49,  -76,   41,   66,
             -13,    0,    0,    0,  121,   24,    0,    0,   50,    0,    0,    0,   51,    8, -128,   28,
             -60,  -31,   28,  102,   20,    1,   61, -120,   67,   56, -124,  -61, -116,   66, -128,    7,
             121,  120,    7,  115, -104,  113,   12,  -26,    0,   15,  -19,   16,   14,  -12, -128,   14,
              51,   12,   66,   30,  -62,  -63,   29,  -50,  -95,   28,  102,   48,    5,   61, -120,   67,
              56, -124, -125,   27,  -52,    3,   61,  -56,   67,   61, -116,    3,   61,  -52,  120, -116,
             116,  112,    7,  123,    8,    7,  121,   72, -121,  112,  112,    7,  122,  112,    3,  118,
             120, -121,  112,   32, -121,   25,  -52,   17,   14,  -20, -112,   14,  -31,   48,   15,  110,
              48,   15,  -29,  -16,   14,  -16,   80,   14,   51,   16,  -60,   29,  -34,   33,   28,  -40,
              33,   29,  -62,   97,   30,  102,   48, -119,   59,  -68, -125,   59,  -48,   67,   57,  -76,
               3,   60,  -68, -125,   60, -124,    3,   59,  -52,  -16,   20,  118,   96,    7,  123,  104,
               7,   55,  104, -121,  114,  104,    7,   55, -128, -121,  112, -112, -121,  112,   96,    7,
             118,   40,    7,  118,   -8,    5,  118,  120, -121,  119, -128, -121,   95,    8, -121,  113,
              24, -121,  114, -104, -121,  121, -104, -127,   44,  -18,  -16,   14,  -18,  -32,   14,  -11,
             -64,   14,  -20,    0,  113,   32,    0,    0,    2,    0,    0,    0,    6,  112,  -84,  -32,
             -90,   77, -114,    6,   97,   32,    0,    0,   12,    0,    0,    0,   19,    4,   65,   44,
              16,    0,    0,    0,    1,    0,    0,    0,  100,   35,    0,    0,   23,  -44, -114,   33,
              12,   66,   18,  -31,   64,    0,    0,    0,    3,    0,    0,    0,   22, -112,   32, -115,
              47,   56,   68,   36,   16,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
        };
        return data;
    }

    private static int bitCode64Length = 832;

    private static byte[] getBitCode64Internal() {
        byte[] bc = new byte[bitCode64Length];
        int offset = 0;
        byte[] seg;
        seg = getSegment64_0();
        System.arraycopy(seg, 0, bc, offset, seg.length);
        offset += seg.length;
        return bc;
    }

}

