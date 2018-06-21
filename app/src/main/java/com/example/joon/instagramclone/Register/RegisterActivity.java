package com.example.joon.instagramclone.Register;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.joon.instagramclone.Model.User;
import com.example.joon.instagramclone.R;
import com.example.joon.instagramclone.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity{

    private static final String TAG = "RegisterActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;

    //widgets
    private Context mContext= RegisterActivity.this;
    private ProgressBar mProgressBar;
    private EditText mEmail, mUserName, mPassword;
    private String email, username, password;
    private Button btnRegister;
    private String append="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d("Chat", "register activity starts");

        // initiate widgets
        initWidgets();
        setupFirebaseAuth();
        mFirebaseMethods = new FirebaseMethods(mContext);
        init();
    }
    //
    private void init(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mUserName.getText().toString();
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                if(username.equals("")||email.equals("")||password.equals("")){
                    Toast.makeText(mContext,"You must fill out every field to register", Toast.LENGTH_SHORT).show();

                } else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mFirebaseMethods.registerNewEmail(username,email,password);

                }
            }
        });
    }

    private void initWidgets(){
        Log.d(TAG, "initWidgets: initiate widgets");
        mProgressBar = findViewById(R.id.registerRequestLoadingProgressbar);
        mProgressBar.setVisibility(View.GONE);
        mEmail = findViewById(R.id.input_email);
        mUserName= findViewById(R.id.input_name);
        mPassword = findViewById(R.id.input_password);
        btnRegister = findViewById(R.id.btn_register);
    }

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

                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        Log.d(TAG, "onDataChange: Found a match"+singleSnapshot.getValue(User.class).getUsername());
                         append = mReference.push().getKey().substring(3,7);
                        Log.d(TAG, "onDataChange: username is already in use, random string appended"+append);
                    }
                }

                String mUsername ="";
                mUsername = username+append;
                mFirebaseMethods.addNewUser(email,mUsername,"","","");
                Toast.makeText(mContext,"Sign Up successful. Sending verification email", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    /**
     * ----------------------firebase------------------------
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebase: setting up firebase auth");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mReference = mFirebaseDatabase.getReference();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged: sign in"+user.getUid());
                    mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUsernameExists(username);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // back to the login page
                    finish();
                } else{
                    Log.d(TAG, "onAuthStateChanged: sign out");
                }
            }
        };

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

    /**
     * ----------------------firebase------------------------
     */

}
