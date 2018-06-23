package com.example.joon.instagramclone.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.joon.instagramclone.Model.Comment;
import com.example.joon.instagramclone.Model.Photo;
import com.example.joon.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ViewCommentFragment extends Fragment {
    private static final String TAG = "ViewCommentFragment";

    public ViewCommentFragment(){
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;


    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private ListView mListView;

    //vars
    private Photo mPhoto;
    private ArrayList<Comment> mCommentsList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container,false);

        mBackArrow = view.findViewById(R.id.backArrow);
        mCheckMark = view.findViewById(R.id.ivPostComment);
        mComment = view.findViewById(R.id.comment);
        mListView = view.findViewById(R.id.listView);

        mCommentsList = new ArrayList<>();



        try{
            mPhoto = getPhotoFromBundle();
        } catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException"+ e.getMessage());
        }

        setupFirebaseAuth();
        return view;
    }



    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: adding new comment "+newComment);
        Comment comment = new Comment();
        String commentID = myRef.push().getKey(); // generate random number

        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        myRef.child(getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

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

        myRef.child(getActivity().getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getActivity().getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        x

                                    photo.setComments(mCommentsList);
                                    mPhoto = photo;
                                    setupWidgets();
//                    List<Like> likeList = new ArrayList<>();
//                    for(DataSnapshot subSnapshot : ds.child(getString(R.string.field_likes)).getChildren()){
//                        Like like = new Like();
//                        like.setUser_id(subSnapshot.getValue(Like.class).getUser_id());
//                        likeList.add(like);
//
//                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void setupWidgets() {
        CommentListAdapter adapter = new CommentListAdapter(getActivity(), R.layout.layout_commnet, mCommentsList);
        mListView.setAdapter(adapter);

        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mComment.getText().toString().equals("")){

                    addNewComment(mComment.getText().toString());
                    mComment.setText("");
                    closeKeyboard();
                } else{
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        mAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuthStateListener!=null) mAuth.removeAuthStateListener(mAuthStateListener);

    }


    /**
     * ----------------------firebase------------------------
     */

    /**
     * ----------------------extra-----------------------
     */

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view!=null){
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments"+getArguments());

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            return bundle.getParcelable(getString(R.string.photo));
        } else {
            return null;
        }
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        return sdf.format(new Date());
    }




}
