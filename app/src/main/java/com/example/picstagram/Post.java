package com.example.picstagram;

import com.google.firebase.firestore.GeoPoint;
import com.google.type.DateTime;

import java.util.Date;
import java.sql.Timestamp;

public class Post {
    private String caption;
    private String url;
    private Timestamp timestamp;
    private GeoPoint location;

    public Post(String url, String caption, GeoPoint location) {
        this.url = url;
        this.caption = caption;
        this.location = location;
        Date date = new Date();
        this.timestamp =new Timestamp(date.getTime());
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
