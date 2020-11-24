package com.example.picstagram;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends ArrayAdapter<Post> {

    private static final String TAG = "PostAdapter";
    private Context mContext;
    int mResource;

    public PostAdapter(@NonNull Context context, int resource, @NonNull List<Post> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String caption = getItem(position).getCaption();
        String url = getItem(position).getUrl();
        GeoPoint location = getItem(position).getLocation();
        Post post = new Post(url, caption, location);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView captionTextView = (TextView) convertView.findViewById(R.id.caption);
        ImageView postImg = (ImageView) convertView.findViewById(R.id.postImage);

        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToMainActivity = new Intent(mContext.getApplicationContext(), MapsActivity.class);
                goToMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(location != null) {
                    goToMainActivity.putExtra("longtitude", location.getLongitude());
                    goToMainActivity.putExtra("latitude", location.getLatitude());
                    mContext.startActivity(goToMainActivity);
                }
            }
        });

        captionTextView.setText(caption);
        Picasso.get().load(url).into(postImg);
        return convertView;
    }
}
