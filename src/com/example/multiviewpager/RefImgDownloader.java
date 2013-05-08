
package com.example.multiviewpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.util.Log;

import net.tsz.afinal.bitmap.download.Downloader;
import net.tsz.afinal.bitmap.download.SimpleHttpDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RefImgDownloader implements Downloader {
    private static final String TAG = RefImgDownloader.class.getSimpleName();
    private static final int IO_BUFFER_SIZE = 8 * 1024; // 8k
    private SimpleHttpDownloader simpleHttpDownloader = new SimpleHttpDownloader();
    private String cacheDir;

    public RefImgDownloader(Context context) {
        cacheDir = context.getFilesDir().getAbsolutePath();
    }

    @Override
    public boolean downloadToLocalStreamByUrl(String urlString, OutputStream outputStream) {
        Log.d(TAG, "download:" + urlString);
        // 通过SimpleHttpDownloader把图片下载到本地
        File orgFile = new File(cacheDir, "org.img");

        if (orgFile.exists()) {
            orgFile.delete();
        }

        try {
            orgFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        OutputStream orgFileOutputStream = null;
        try {
            orgFileOutputStream = new FileOutputStream(orgFile);
            simpleHttpDownloader.downloadToLocalStreamByUrl(urlString, orgFileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try {
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(orgFile));
            if (bm != null) {
                bm = drawReflection(bm);
                // 返回倒影图片到outputStream
                bm.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                outputStream.close();
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private Bitmap drawReflection(Bitmap originalImage) {
        // The gap we want between the reflection and the original image
        final int reflectionGap = 0;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width,
                height / 2, matrix, false);

        // Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width
                , (height + height / 2), Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // Draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width,
                bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }
}
