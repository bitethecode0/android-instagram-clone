package com.example.joon.instagramclone.Login;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.example.joon.instagramclone.Home.HomeActivity;
import com.example.joon.instagramclone.R;
import com.example.joon.instagramclone.Register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private Context mContext=LoginActivity.this ;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = findViewById(R.id.loginRequestLoadingProgressbar);

        Log.d(TAG, "onCreate: started");
        mProgressBar.setVisibility(View.GONE);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);

        setupFirebaseAuth();
        init();
    }
    /**
     * ----------------------firebase------------------------
     */
    private void init(){
        // initiate login button -> log in process
        Button mLoginBtn = findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempt to login");
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(email.equals("")||password.equals("")){
                    Toast.makeText(mContext,"You need to fill all fields to login", Toast.LENGTH_SHORT).show();

                } else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Authentication Success.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext,HomeActivity.class);
                                startActivity(intent);
                                /**
                                 *

                                try{
                                    if(user.isEmailVerified()){
                                        Log.d(TAG, "onComplete: email is verified");
                                        Intent intent = new Intent(mContext,HomeActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Toast.makeText(mContext, "Email is not verified.\n check your email.", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                } catch (NullPointerException e){
                                    Log.e(TAG, "onComplete: Nullpoint exception :" +e.getMessage());

                                }
                                 */
                                mProgressBar.setVisibility(View.GONE);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            }
        });


        TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
        Log.d("Chat", "before click");
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the register activity");
                Log.d("Chat", "navigating to the register activity");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /**
         * if user signed in, navigate to HomeActivity
         */
        if(mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
    /**
     *
     * check to see if @param user is logged in.
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebase: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();

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
