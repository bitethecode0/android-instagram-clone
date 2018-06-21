package com.example.joon.instagramclone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {
    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgURL){
        File imageFile = new File(imgURL);
        FileInputStream fis = null;
        Bitmap bm = null;
        Bitmap rotateBm =null;
        try{
            fis = new FileInputStream(imageFile);
            bm = BitmapFactory.decodeStream(fis);
            rotateBm = rotate(bm);
        }catch (FileNotFoundException e){
            Log.e(TAG, "getBitmap: FileNotFoundException"+ e.getMessage());
        }finally {
            try{
                fis.close();
            } catch (IOException e){
                Log.e(TAG, "getBitmap: IOException"+ e.getMessage());
            }
        }

        /**
         *  control the rotation here
         */



        return rotateBm;
    }

    private static Bitmap rotate(Bitmap bm) {
        int w = bm.getWidth();
        int h = bm.getHeight();

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bm,0,0,w,h,matrix,true);
    }


    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
}
