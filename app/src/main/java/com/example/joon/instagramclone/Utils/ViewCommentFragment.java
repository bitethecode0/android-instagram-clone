package com.example.joon.instagramclone.Utils;

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
import android.widget.ListView;

import com.example.joon.instagramclone.Model.Comment;
import com.example.joon.instagramclone.Model.Photo;
import com.example.joon.instagramclone.R;

import java.util.ArrayList;

public class ViewCommentFragment extends Fragment {
    private static final String TAG = "ViewCommentFragment";

    public ViewCommentFragment(){
        super();
        setArguments(new Bundle());
    }

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

        Comment firstLineComment = new Comment();
        firstLineComment.setComment(mPhoto.getCaption());
        firstLineComment.setUser_id(mPhoto.getUser_id());
        firstLineComment.setDate_created(mPhoto.getDate_created());

        mCommentsList.add(firstLineComment);
        CommentListAdapter adapter = new CommentListAdapter(getActivity(), R.layout.layout_commnet, mCommentsList);
        mListView.setAdapter(adapter);

        return view;
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
}
