package com.example.joon.instagramclone.Share;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.joon.instagramclone.Profile.AccountSettingActivity;
import com.example.joon.instagramclone.R;
import com.example.joon.instagramclone.Utils.FilePaths;
import com.example.joon.instagramclone.Utils.FileSearch;
import com.example.joon.instagramclone.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;


public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMS = 3;

    //widgets
    private ImageView galleryImage;
    private GridView gridView;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;


    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);


        galleryImage= view.findViewById(R.id.galleryImageview);
        gridView = view.findViewById(R.id.gridView);
        mProgressBar = view.findViewById(R.id.progressBar);
        directorySpinner = view.findViewById(R.id.spinnerDir);
        directories = new ArrayList<>();

        Log.d(TAG, "onCreateView: started.");

        ImageView shareClose = view.findViewById(R.id.cancleTv);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                getActivity().finish();
            }
        });

        TextView nextScreen = view.findViewById(R.id.nextTv);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRootTask()){
                    Log.d(TAG, "onClick: navigating to the final share screen.");
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                } else{
                    Log.d(TAG, "onClick: navigating to the account setting activity.");
                    Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });
        init();
        return view;
    }

    private boolean isRootTask() {
        if(((ShareActivity)getActivity()).getTask()==0){
            return true;
        } else{
            return false;
        }
    }

    private void init(){
        FilePaths filePaths = new FilePaths();

        if(FileSearch.getDirectoryPaths(filePaths.PICTURES)!=null){
            directories= FileSearch.getDirectoryPaths(filePaths.PICTURES);

        }

        directories.add(filePaths.CAMERA);
        directories.add(filePaths.RESTORED);
        directories.add(filePaths.SCREENSHOTS);
        ArrayList<String> directoryNames = new ArrayList<>();
        for(int i=0; i<directories.size(); i++){
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //directorySpinner.setPrompt("Title");
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: item selected : "+directories.get(position));
                /**
                 * setup grid imaage for the directory chosen
                 */
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setupGridView(String selectedDirectory) {
        Log.d(TAG, "setupGridView: directory chosen : "+selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMS;
        gridView.setColumnWidth(imageWidth);

        GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        // Set FIRST image to be displayed when the fragmet view is inflated.
        try {
            setImage(imgURLs.get(0), galleryImage, mAppend);
            mSelectedImage = imgURLs.get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));
                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });

    }

    private void setImage(String imgURL, ImageView image, String append) {
        Log.d(TAG, "setImage: setting first image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
