package com.example.prototype.Event;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class MyEvent implements Comparable {

    //properties
    String name;
    Date date;
    int distance;
    String description;
    boolean isFollowed;
    boolean open;
    private DatabaseReference dbEvents;



    //constructors

    public MyEvent( String name, Date date, int dist, String description) {
        this.date = date;
        this.name = name;
        this.distance = dist;
        isFollowed = false;
        this.description = description;
        open = false;

    }

    //emtpy constructor for firebase inquiry
    public MyEvent(){}

    //testing constructor without distance integer, once we can calculate distance, delete this constructor - cemre
    public MyEvent( String name, Date date, String description) {
        this.name = name;
        this.date = date;
        isFollowed = false;
        this.description = description;
        open = false;

    }

    //methods
    public String toString() {
        return name;
    }

    public void setFollowed(boolean bool) {
        isFollowed = bool;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate()
    {
        return date;
    }

    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }
    public boolean getOpen() { return open; }

    public boolean getFollowed() { return isFollowed; }

    public void toggleOpen() { open = !open; }

    public void toggleFollowed() {
        isFollowed = !isFollowed;
    }

    public int compareTo( Object o) {
        if ( isFollowed && !((MyEvent) o).getFollowed() )
            return -1;

        if ( !isFollowed && ((MyEvent) o).getFollowed() )
            return 1;

        return distance - ((MyEvent) o).getDistance();
    }
}