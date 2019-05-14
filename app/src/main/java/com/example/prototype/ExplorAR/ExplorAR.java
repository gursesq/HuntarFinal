package com.example.prototype.ExplorAR;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototype.CreatAR.EventCreator;
import com.example.prototype.Event.MyEvent;
import com.example.prototype.Event.OpenEvent;
import com.example.prototype.R;
import com.example.prototype.User.Profile;
import com.example.prototype.User.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

//gets events from database and puts them in the listview, then the user can follow these events or
//choose to play one of them
public class ExplorAR extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private User userFound;
    private String sampleDes;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorar);

        Button btnCreatar = findViewById(R.id.btnEventCreator);
        Button btnProfile = (Button) findViewById(R.id.btnProfile);
        final ArrayList<MyEvent> list = new ArrayList<MyEvent>();

        myRef = mFirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        sampleDes = "This is a description.";

        list.add( new MyEvent("sa1", new Date(), 100, sampleDes));
        list.add( new MyEvent("sa2", new Date(), 110, sampleDes));
        list.add( new MyEvent("sa3", new Date(), 90, sampleDes));
        list.add( new MyEvent("sa4", new Date(), 170, sampleDes));
        list.add( new MyEvent("sa5", new Date(), 70, sampleDes));
        list.add( new MyEvent("sa6", new Date(), 120, sampleDes));
        list.add( new MyEvent("sa7", new Date(), 140, sampleDes));

        final ListView listView = findViewById(R.id.eventsList);
        String[] vals = new String[list.size()];
        for ( int i = 0; i < list.size(); i++ ) {
            vals[i] = " ";
        }
        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter( this,list,vals, listView);
        listView.setAdapter(adapter);
        Collections.sort(list);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                //search may be improved, there is a sec delay time while loading user data
                for(DataSnapshot ds:dataSnapshot.child("Events").getChildren()){
                    System.out.println(ds.child("name").getValue());

                    if( !list.contains(ds.getValue(MyEvent.class))){
                        list.add( ds.getValue(MyEvent.class));
                        System.out.println(ds.getValue(MyEvent.class).getName());

                    }

                }
                MySimpleArrayAdapter adapter = new MySimpleArrayAdapter( ExplorAR.this,list,vals, listView);
                listView.setAdapter(adapter);
                Collections.sort(list);
                adapter.notifyDataSetChanged();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());

            }
        });



        btnCreatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ExplorAR.this, EventCreator.class);
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( ExplorAR.this, Profile.class);
                startActivity(intent);
            }
        });





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                makeToast("clicked: " + position);
                list.get(position).toggleOpen();
                if ( list.get(position).getOpen() ) {
                    list.add( (position+1), new OpenEvent(list.get(position),list.get(position).getFollowed()));
                    String[] vals = new String[list.size()];
                    for ( int i = 0; i < list.size(); i++ ) {
                        vals[i] = " ";
                    }
                    MySimpleArrayAdapter adapter = new MySimpleArrayAdapter( ExplorAR.this,list,vals, listView);
                    listView.setAdapter(adapter);
                    Collections.sort(list);
                }
                else {
                    System.out.println(list.get(position+1));
                    list.remove(position+1);
                    String[] vals = new String[list.size()];
                    for ( int i = 0; i < list.size(); i++ ) {
                        vals[i] = " ";
                    }
                    MySimpleArrayAdapter adapter = new MySimpleArrayAdapter( ExplorAR.this,list,vals, listView);
                    listView.setAdapter(adapter);
                    Collections.sort(list);
                }
                Collections.sort(list);
                adapter.notifyDataSetChanged();
            }
        });


    }

    public void makeToast( String message) {
        Toast.makeText( getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}