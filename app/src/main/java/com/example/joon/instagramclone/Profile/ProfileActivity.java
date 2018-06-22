package com.example.joon.instagramclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.example.joon.instagramclone.Model.Photo;
import com.example.joon.instagramclone.R;
import com.example.joon.instagramclone.Utils.ViewPostFragment;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener, ViewPostFragment.OnCommnetThreadSelectedListener{
    private static final String TAG ="ProfileActivity";
    private final static int ACTIVITY_NUM=4;
    private static final int NUM_GRID_COLUMS =3;
    private Context mContext= ProfileActivity.this;
    //progress bar
    private ProgressBar mProgressBar;
    private ImageView mProfilePhoto;


    /**
     * implenments the interface
     * @param photo
     * @param activityNumber
     */
    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview"+photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();


    }

    /**
     * implements interface for when comment selected
     * @param photo
     */

    @Override
    public void OnCommnetThreadSelectedListener(Photo photo) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    private void init() {
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }



}
