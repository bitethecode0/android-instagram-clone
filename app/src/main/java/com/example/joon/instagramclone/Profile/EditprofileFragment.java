package com.example.joon.instagramclone.Profile;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joon.instagramclone.Dialogs.ConfirmPasswordDialog;
import com.example.joon.instagramclone.Model.User;
import com.example.joon.instagramclone.Model.UserAccountSettings;
import com.example.joon.instagramclone.Model.UserSettings;
import com.example.joon.instagramclone.R;
import com.example.joon.instagramclone.Share.ShareActivity;
import com.example.joon.instagramclone.Utils.FirebaseMethods;
import com.example.joon.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditprofileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener {

    private static final String TAG = "EditprofileFragment";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String user_id;


    //edit profile fragment widgets
    private CircleImageView mProfilePhoto;
    private TextView mChangeProfilePhoto;
    private EditText mUserName,mDisplayName,mWebsites,mDescription, mEmail, mPhoneNumber;


    //variable
    private ConfirmPasswordDialog dialog;
    private UserSettings mUserSettings;

    /**
     * interface override method
     * @param password
     */
    @Override
    public void OnConfirmPassword(String password) {


        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if(task.isSuccessful()){
                                        try{
                                            if(task.getResult().getProviders().size() ==1){
                                                Log.d(TAG, "onComplete: that email is already in use");
                                                Toast.makeText(getActivity(), "that email is already in use", Toast.LENGTH_SHORT).show();
                                            } else{
                                                Log.d(TAG, "onComplete: that email is available");
                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Log.d(TAG, "onComplete: User email is updated");
                                                            Toast.makeText(getActivity(), "email updated", Toast.LENGTH_SHORT).show();
                                                            mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                });
                                            }

                                        } catch (NullPointerException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });


                        } else {
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mProfilePhoto =(CircleImageView) view.findViewById(R.id.profile_photo);
        mUserName = view.findViewById(R.id.user_name);
        mDisplayName =view.findViewById(R.id.display_name_editprofile);
        mDescription =view.findViewById(R.id.description);
        mWebsites = view.findViewById(R.id.websites);
        mEmail =view.findViewById(R.id.email_info);
        mPhoneNumber =view.findViewById(R.id.phone_info);
        mChangeProfilePhoto = view.findViewById(R.id.changeProfilephoto);

        mFirebaseMethods = new FirebaseMethods(getActivity());

        setupFirebaseAuth();


        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: back to profile activity");
                getActivity().finish();
            }
        });

        ImageView checkMark = view.findViewById(R.id.checkMark);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes");
                saveProfileSettings();
            }
        });

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // not zero
                getActivity().startActivity(intent);
                getActivity().finish();


            }
        });
        return view;
    }





    /**
     * retrieve the data contained in the widgets and submit it to the database
     */
    private void saveProfileSettings(){
        final String username = mUserName.getText().toString();
        final String email = mEmail.getText().toString();
        final String displayName = mDisplayName.getText().toString();
        final String websites = mWebsites.getText().toString();
        final String description = mDescription.getText().toString();
        final long phone_number = Long.parseLong(mPhoneNumber.getText().toString());


        if (!mUserSettings.getUser().getUsername().equals(username)) {
            checkIfUsernameExists(username);
        }

        // user change their email(much more complicated)
        if(!mUserSettings.getUser().getEmail().equals(email)){
            //1, reauthenticate - confirm email, and password

            dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditprofileFragment.this, 1);
            //2, check if the email is already registered - fetchProvidersForemail(String email)
            //3, change the email - submit the new email to the database and authentication
        }

        if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)){
            // update display name
            mFirebaseMethods.updateUserAccountSettings(displayName, null, null, 0);
        }

        if(!mUserSettings.getSettings().getWebsite().equals(websites)){
            // update websites
            mFirebaseMethods.updateUserAccountSettings(null, websites, null, 0);
        }

        if(!mUserSettings.getSettings().getDescription().equals(description)){
            // update description
            mFirebaseMethods.updateUserAccountSettings(null, null, description, 0);
        }

        if(mUserSettings.getUser().getPhone_number()!=phone_number){
            // update phone_number
            mFirebaseMethods.updateUserAccountSettings(null, null, null, phone_number);
        }





    }


    private void setProfileWidgets(UserSettings userSettings){

        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        //Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().toString());

        mUserSettings = userSettings;
        //User user = userSettings.getUser(); not now.
        UserAccountSettings settings = userSettings.getSettings();
        Log.d(TAG,settings.toString());

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");

        mDisplayName.setText(settings.getDisplay_name());
        mUserName.setText(settings.getUsername());
        mWebsites.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));


    }
    /**
     * check @param username exists in the database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if "+username+" already exists");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(),"saved username.",Toast.LENGTH_SHORT).show();
                }

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        Log.d(TAG, "onDataChange: Found a match"+singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(),"That username already exists",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * ----------------------firebase------------------------
     */
    /**
     *
     * check to see if @param user is logged in.
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebase: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user_id = mAuth.getCurrentUser().getUid();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged: sign in"+user.getUid());
                } else{
                    Log.d(TAG, "onAuthStateChanged: sign out");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /**
                 * retrieve user information from the database
                 */
                Log.d(TAG, "onDataChange: data from the dataabse : " + mFirebaseMethods.getUserSettings(dataSnapshot));
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        mAuth.addAuthStateListener(mAuthStateListener);

        //unchecked UI for not
        //updateUI(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuthStateListener!=null) mAuth.removeAuthStateListener(mAuthStateListener);
        //unchecked UI for not
        //updateUI(currentUser);
    }


}
