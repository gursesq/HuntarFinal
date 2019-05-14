package com.example.prototype.Event;


import com.example.prototype.Event.MyEvent;

//An event that has been tapped odd times which means it will have an adapter_open segment below it
//in the listview
public class OpenEvent extends MyEvent {

    //properties


    //consturctrors

    public OpenEvent( MyEvent event, boolean bool) {
        super(event.getName(),event.getDate(),event.getDistance(),event.getDescription());
        super.isFollowed = bool;
    }

    //methods
}