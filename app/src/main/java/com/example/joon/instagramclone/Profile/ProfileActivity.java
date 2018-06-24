package com.example.joon.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.joon.instagramclone.Model.Photo;
import com.example.joon.instagramclone.R;
import com.example.joon.instagramclone.Utils.ViewCommentFragment;
import com.example.joon.instagramclone.Utils.ViewPostFragment;
import com.example.joon.instagramclone.Utils.ViewProfileFragment;

public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener,
        ViewProfileFragment.OnGridImageSelectedListener,
        ViewPostFragment.OnCommentThreadSelectedListener{
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

    @Override
    public void OnCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "OnCommentThreadSelectedListener: going to the comment page with bundles.");
        ViewCommentFragment fragment = new ViewCommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comment_fragment));
        transaction.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    private void init() {
        Log.d(TAG, "init:  " + getString(R.string.profile_fragment));

        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.calling_activity))){
            if(intent.hasExtra(getString(R.string.intent_user))){
                Log.d(TAG, "init: searching for user object attached as intent extra.");

                ViewProfileFragment fragment = new ViewProfileFragment();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.intent_user),
                        intent.getParcelableExtra(getString(R.string.intent_user)));

                fragment.setArguments(args);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(getString(R.string.view_profile_fragment));
                transaction.commit();
            } else{
                Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            }

        } else{
            Log.d(TAG, "init: inflating profile (go to your own profile)");
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }
    }
}
