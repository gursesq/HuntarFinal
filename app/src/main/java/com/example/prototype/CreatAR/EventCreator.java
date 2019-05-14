package com.example.prototype.CreatAR;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.prototype.Event.ARPoint;
import com.example.prototype.Event.MyEvent;
import com.example.prototype.ExplorAR.ExplorAR;
import com.example.prototype.R;
import com.example.prototype.User.Profile;
import com.example.prototype.User.User;
import com.example.prototype.start.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//a class that lets the user create an event and publish it (arpoints part does not work yet)
public class EventCreator extends AppCompatActivity {

    private static final String TAG = "EventCreator";

    //properties
    final static ArrayList<ARPoint> list = new ArrayList<ARPoint>();

    //firebase properties
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference dbEvents;
    private FirebaseUser user;
    private String uid;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private static final int GALLERY_INTENT = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_creator);

        final EditText txtDescription = (EditText) findViewById(R.id.txtDescription);
        final EditText txtEventName = (EditText) findViewById(R.id.txtEventName);
        final EditText txtDate = (EditText) findViewById(R.id.txtDate);

        //initialize firebase properties
        myRef = mFirebaseDatabase.getInstance().getReference();
        dbEvents = myRef.child("Events");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        //firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        //initialize buttons
        //leads user to explorAR page from eventcreatAR
        Button btnExplorar = findViewById(R.id.btnExplorar);
        btnExplorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventCreator.this, ExplorAR.class);
                startActivity(intent);
            }
        });

        //leads user to profile from eventcreatAR
        Button btnProfile = (Button) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventCreator.this, Profile.class);
                startActivity(intent);
            }
        });

        //the publish button function - adds new custom event to explorAR hub
        Button btnPublish = findViewById(R.id.btnPublish);
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to add event to database");
                FirebaseApp.initializeApp(EventCreator.this);

                String description = txtDescription.getText().toString();
                String evtName = txtEventName.getText().toString();
                String sdate = txtDate.getText().toString();
                Date date = null;
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy").parse(sdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //we should be able to get distance to create this object
                //I have put a magic number for the sake of creating a database & testing it - cemre
                //distance uygun hale geldiği zaman, date in yanına virgül distance ekleyiniz.
                MyEvent eventObj;
                eventObj = new MyEvent(evtName, date, description);
                dbEvents.push().setValue(eventObj);

                Intent intent = new Intent(EventCreator.this, ExplorAR.class);
                startActivity(intent);

            }
        });

        //the plus button for image
        Button btnAddEvent = (Button) findViewById(R.id.btnAddEvent);
        ProgressDialog progressDialog = new ProgressDialog(EventCreator.this);

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);

            }
        });

        /*@Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);


            //Upload the image to Firebase Update
            if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
                progressDialog.setMessage("Uploading Image...");
                progressDialog.show();
                Uri uri = data.getData();

                StorageReference filepath = storageRef.child("Photos").child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(EventCreator.this, "Upload done", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                });
            }
*/

            /*//initialize list
            list.add(new ARPoint("sa1"));
            list.add(new ARPoint("sa2"));
            list.add(new ARPoint("sa3"));
            list.add(new ARPoint("sa4"));

            ListView listView = findViewById(R.id.pointsList);
            String[] vals = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                vals[i] = " ";
            }
            CreatorAdapter adapter = new CreatorAdapter(this, list, vals, listView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });


        }*/

        /*public static void remove( int position) {
            list.remove(position);
        }*/
    }
}