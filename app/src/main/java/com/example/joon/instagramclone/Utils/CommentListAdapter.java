package com.example.joon.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joon.instagramclone.Model.Comment;
import com.example.joon.instagramclone.Model.UserAccountSettings;
import com.example.joon.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;


    public CommentListAdapter(@NonNull Context context, int resource, List<Comment> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext= context;
        layoutResource =resource;
    }


    public static class Viewholder{
        TextView comment, username, timestamp, reply, likes;
        CircleImageView profile_image;
        ImageView heart; // for like toggle

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Viewholder holder;

        if(convertView ==null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new Viewholder();

            holder.comment =convertView.findViewById(R.id.comment);
            holder.username = convertView.findViewById(R.id.comment_username);
            holder.timestamp = convertView.findViewById(R.id.comment_time_posted);
            holder.reply = convertView.findViewById(R.id.comment_reply);
            holder.likes = convertView.findViewById(R.id.comment_likes);
            holder.profile_image = convertView.findViewById(R.id.comment_profile_image);
            holder.heart= convertView.findViewById(R.id.heartInComment);

            convertView.setTag(holder);
        } else{
            holder = (Viewholder)convertView.getTag();
        }

        // set comment
        holder.comment.setText(getItem(position).getComment());
        // set timestamp
        String timestampDiff = getTimestampDifference(getItem(position));
        if(!timestampDiff.equals("0")){
            holder.timestamp.setText(timestampDiff+" d");
        } else{
            holder.timestamp.setText("Today");
        }
        // retrieve data form user_accounting_settings for username and profile photo
        // set the username, profile_photo

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_accounting_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    holder.username.setText(ds.getValue(UserAccountSettings.class).getUsername());

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(
                            ds.getValue(UserAccountSettings.class).getProfile_photo(), holder.profile_image
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return convertView;
    }

    public String getTimestampDifference(Comment comment) {
        Log.d(TAG, "getTimestampDifference: ");

        String differnce = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        Date today = calendar.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = comment.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            differnce = String.valueOf(Math.round(((today.getTime()- timestamp.getTime())/1000/60/60/24)));

        }catch (ParseException e){
            Log.e(TAG, "getTimestampDifference: ParseException"+e.getMessage() );
        }


        return differnce;
    }
}
