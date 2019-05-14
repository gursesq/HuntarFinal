package com.example.prototype.Event;

import android.net.Uri;

public class ARPoint {

    //porperties

    String name;
    Uri arImage;
    String location;

    //constructors

    public ARPoint(String name, Uri arImage, String location) {
        this.name = name;
        this.arImage = arImage;
        this.location = location;


    }

    //test constructor - delete later
    public ARPoint(String name) {
        this.name = name;


    }


    //methods

    public String getName() { return name; }

    public String getLocation() {
        return location;
    }

    public Uri getArImage() {
        return arImage;
    }
}
