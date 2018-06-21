package com.example.joon.instagramclone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        try{
            fis = new FileInputStream(imageFile);
            bm = BitmapFactory.decodeStream(fis);
        }catch (FileNotFoundException e){
            Log.e(TAG, "getBitmap: FileNotFoundException"+ e.getMessage());
        }finally {
            try{
                fis.close();
            } catch (IOException e){
                Log.e(TAG, "getBitmap: IOException"+ e.getMessage());
            }
        }
        return bm;
    }

    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }
}
