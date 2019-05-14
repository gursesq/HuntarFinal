package com.example.prototype.User;

import android.net.Uri;

import com.example.prototype.Event.MyEvent;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class User implements Serializable {

    private String name;
    //private ArrayList<MyEvent> completedEvents;

    //şimdilik reward string type
    private ArrayList<String> rewards;
    private String email;
    private String uid;
    private Uri photoUrl;

    public User() {}

    public User(String name, String email, String uid, Uri photoUrl) {
        this.name = name;
        //this.completedEvents = completedEvents;
        this.rewards = new ArrayList<String>();
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    //public ArrayList<MyEvent> getCompletedEvents() {return completedEvents;}
    //public boolean isCompleted(MyEvent event) { if () }


    //change reward type from string once its obj class is created - cemre
    public ArrayList<String> getRewards() {return rewards;}
    public void addReward(String reward) { rewards.add(reward);}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getUid() {return uid;}

    public Uri getPhotoUrl() { return photoUrl;}
}