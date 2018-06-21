package com.example.joon.instagramclone.Utils;

import android.os.Environment;

public class FilePaths {

    // default location
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String RESTORED = ROOT_DIR + "/DCIM/restored";
    public String  SCREENSHOTS= ROOT_DIR + "/DCIM/screenshots";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
}
