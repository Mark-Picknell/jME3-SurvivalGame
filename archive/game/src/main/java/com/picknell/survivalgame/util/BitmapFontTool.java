package com.picknell.survivalgame.util;

import java.io.*;

public class BitmapFontTool {

    public static class BitmapFontHeader {
        private String face;
        private int size;
        private boolean bold;
        private boolean italic;

    }

    private static final String HEADER_ONE = "info face=\"%s\" size=%d bold=%d italic=%d charset=\"%d\" unicode=%d stretchH=%d smooth=%d aa=%d padding=%d,%d,%d,%d spacing=%d,%d outline=%d";

    public static void createFont(String filePath, String font, int size, boolean bold, boolean italic, String charset, boolean unicode, int stretchH, int smooth, int aa, int paddingT, int paddingB, int paddingL, int paddingR, int outline) {

    }

}
