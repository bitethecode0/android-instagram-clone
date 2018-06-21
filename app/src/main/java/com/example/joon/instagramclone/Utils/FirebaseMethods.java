package com.example.joon.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.joon.instagramclone.Home.HomeActivity;
import com.example.joon.instagramclone.Model.Photo;
import com.example.joon.instagramclone.Model.User;
import com.example.joon.instagramclone.Model.UserAccountSettings;
import com.example.joon.instagramclone.Model.UserSettings;
import com.example.joon.instagramclone.Profile.AccountSettingActivity;
import com.example.joon.instagramclone.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getChildren()) {
            count++;
        }
        return count;
    }

    public void uploadNewPhoto(String photoType, final String caption, final int imageCount, final String imgURL, Bitmap bm) {

        /**
         * save profile photo into the firebase database
         */

        Log.d(TAG, "uploadNewPhoto: uploading new image photo");

        FilePaths filepath = new FilePaths();

        // case1) new photo
        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            Log.d(TAG, "uploadNewPhoto:  uploading new 'photo'.");
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid(); // same as userID, just not to cause another error

            final StorageReference storageReference = mStorageReference.child(filepath.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (imageCount + 1));

            //conver image url to bitmap
            if (bm == null) {
                bm = ImageManager.getBitmap(imgURL);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure:  photo update faild" + e.getMessage());
                    Toast.makeText(mContext, "Photo update failed.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    taskSnapshot.getTask().continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();
                                /**
                                 * add the new photo to 'photos' and 'user_photos'
                                 */
                                addNewPhotoToDatabase(caption, downloadUri.toString());

                                //setProfilePhoto(downloadUri.toString());
                            } else {
                                Toast.makeText(mContext, "failed to downlaod url", Toast.LENGTH_SHORT).show();
                                // Handle failures
                                // ...
                            }
                        }
                    });


                    /**
                     * navigate to the main so the user can see updated photos
                     */
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                }
            });

        } else if (photoType.equals(mContext.getString(R.string.profile_photo))) {
            // case2) new profile photo
            Log.d(TAG, "uploadNewPhoto: uploading new PROFILE photo");



            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final StorageReference storageReference = mStorageReference
                    .child(filepath.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if (bm == null) {
                bm = ImageManager.getBitmap(imgURL);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getTask().continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return storageReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                setProfilePhoto(downloadUri.toString());
                            } else {
                                Toast.makeText(mContext, "failed to downlaod url", Toast.LENGTH_SHORT).show();
                                // Handle failures
                                // ...
                            }
                        }
                    });
                    ((AccountSettingActivity) mContext).setViewPager(
                            ((AccountSettingActivity) mContext).pagerAdapter
                                    .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle any errors
                    Toast.makeText(mContext, "updating the profile photo failed..", Toast.LENGTH_SHORT).show();


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                }

            });




        }
    }

    /**
     * @param URL
     */
    private void setProfilePhoto(String URL) {
        Log.d(TAG, "setProfilePhoto: setting new profile photo" + URL);

        myRef.child(mContext.getString(R.string.dbname_user_accounting_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(URL);

    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        return sdf.format(new Date());
    }

    private void addNewPhotoToDatabase(String caption, String url) {
        Log.d(TAG, "addNewPhotoToDatabase: add photo to the dataabse.");

        String tags = StringManipulation.getTags(caption);
        // save in the photo model
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        // insert data to the firebase

        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);


        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(newPhotoKey).setValue(photo);


    }

    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: updating username to : " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_accounting_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);


    }

    public void updateEmail(String email) {
        Log.d(TAG, "updateUsername: updating email to : " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);


    }

    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber) {
        if (displayName != null) {
            myRef.child(mContext.getString(R.string.dbname_user_accounting_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }

        if (website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_accounting_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }

        if (description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_accounting_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

        if (phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.dbname_users))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }
    }

//    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot) {
//        Log.d(TAG, "checkIfUsernameExists: check if username already exists");
//        User user = new User();
//
//        for (DataSnapshot ds : dataSnapshot.child(userID).getChildren()) {
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot" + ds);
//
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());
//
//            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//        return false;
//    }

    public void registerNewEmail(final String username, final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(mContext, "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();
                            // Send verification email
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: auth state changed" + userID);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    /**
     * send verification when registration is successful
     */

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Verification email is sent, check your email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Can't send verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Add new user's info to the users node in the firebase database
     * Add new user's info to the user_account_settings node in the firebase database
     *
     * @param email
     * @param username
     * @param description
     * @param website
     * @param profile_photo
     */
    public void addNewUser(String email, String username, String description, String website, String profile_photo) {
        User user = new User(userID, 1, email, StringManipulation.condenseUsername(username));
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(description, username, 0, 0, 0,
                StringManipulation.condenseUsername(username), profile_photo, website,userID);

        myRef.child(mContext.getString(R.string.dbname_user_accounting_settings))
                .child(userID)
                .setValue(settings);

    }

    /**
     * retrieve the account settings for the user currently logged in.
     * data base : user_account_settings
     *
     * @param dataSnapshot
     * @return
     */

    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        //Log.d(TAG, "getUserAccountSettings: retrieving user account settings from database"+dataSnapshot);


        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        // loop through nodes(users and user_account_settings)
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_accounting_settings))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);
                try {
                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );
                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );
                    settings.setWebsite(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite()
                    );
                    settings.setDescription(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()
                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setPost(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPost()
                    );
                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );
                    settings.setFollowing(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing()
                    );

                    Log.d(TAG, "getUserSettings: retrieved user_account_settings information " + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException" + e.getMessage());
                }
            }
            // users node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                try {
                    Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds);

                    user.setUsername(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUsername()
                    );
                    user.setEmail(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getEmail()
                    );
                    user.setPhone_number(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getPhone_number()
                    );
                    user.setUser_id(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUser_id()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved users information: " + user.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException" + e.getMessage());
                }
            }
        }
        /**
         * return as UserSettings.. work?? ;;
         */

        return new UserSettings(user, settings);
    }


}
