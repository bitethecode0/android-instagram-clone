package com.example.joon.instagramclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.joon.instagramclone.R;
import com.example.joon.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.joon.instagramclone.Utils.Permissions;
import com.example.joon.instagramclone.Utils.SectionPagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG ="ShareActivity";

    //constants
    private static final int ACTIVITY_NUM =2;
    private static final int PEMISSION_REQUEST_CODE=1;

    private Context mContext;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        mContext= ShareActivity.this;

        if(checkPermissionsArray(Permissions.permissions)){
            setupViewpager();
        } else{
            verifyPermissions(Permissions.permissions);
        }
        //setupBottomNavigationView();
    }

    private void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(ShareActivity.this,permissions,PEMISSION_REQUEST_CODE);
    }

    private boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array ");

        for(int i=0; i<permissions.length; i++){
            String check = permissions[i];
            if(!checkPermission(check)){
                return false;
            }
        }
        return true;
    }

    public boolean checkPermission(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this, permission );

        if(permissionRequest!= PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermission: permission was not gratned for : "+permission);
            return false;
        } else{
            Log.d(TAG, "checkPermission: permission was granted for : "+permission);
            return true;
        }
    }

    private void setupViewpager() {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));


    }

    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    /**
     * return the task (check it has flag or not)
     * @return
     */
    public int getTask() {
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setup bottom navigation view");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
