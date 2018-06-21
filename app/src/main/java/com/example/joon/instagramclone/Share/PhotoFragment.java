package com.example.joon.instagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.joon.instagramclone.Profile.AccountSettingActivity;
import com.example.joon.instagramclone.R;
import com.example.joon.instagramclone.Utils.Permissions;

public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";
    //private static final int GALLERY_FRAGMENT_NUM = 0;
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int CAMERA_REQUEST_CODE = 234;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        Button launchCameraBtn = view.findViewById(R.id.launchCameraBtn);
        launchCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ShareActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM ){
                    if(((ShareActivity)getActivity()).checkPermission(Permissions.CAMERA_PERMISSION[0])){
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                    } else{
                        Intent intent = new Intent(getActivity(),ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: done taking photo");
            Log.d(TAG, "onActivityResult:  attempting to navigate to final share screen");
        }

        Bitmap bitmap;
        bitmap = (Bitmap)data.getExtras().get("data");

        if(isRootTask()){
            /**
             * coming from share activity
             */

            try{
                Log.d(TAG, "onActivityResult: received new photo from camera.");
                Intent intent = new Intent(getActivity(), NextActivity.class);
                intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                startActivity(intent);


            } catch (NullPointerException e){
                Log.e(TAG, "onActivityResult: NullPointerException "+e.getMessage() );
            }

        } else{
            /**
             * coming from edit profile fragment
             */

            try{
                Log.d(TAG, "onActivityResult: received new photo from camera.");
                Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                startActivity(intent);
                getActivity().finish();

            } catch (NullPointerException e){
                Log.e(TAG, "onActivityResult: NullPointerException "+e.getMessage() );
            }
        }


    }

    private boolean isRootTask() {
        if (((ShareActivity) getActivity()).getTask() == 0) {
            return true;
        } else {
            return false;
        }
    }

}
