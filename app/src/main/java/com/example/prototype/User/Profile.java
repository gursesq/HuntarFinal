package com.example.prototype.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prototype.CreatAR.EventCreator;
import com.example.prototype.ExplorAR.ExplorAR;
import com.example.prototype.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private User userFound;

    private String uid;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageReference proRef;
    private StorageReference proImageRef;
    private String generatedFilePath;
    private UploadTask.TaskSnapshot taskSnapshot;


    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FirebaseApp.initializeApp(Profile.this);

        //Realtime
        myRef = mFirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //proRef = storageReference.child("lion.jpg");
        //proImageRef = storageReference.child("animals/lion.jpg");
        //String filePath = proImageRef.getPath();

        StorageReference storageRef = storage.getReferenceFromUrl("gs://huntar-3e939.appspot.com").child("dino.jpg");

        ImageView image = (ImageView) findViewById(R.id.imgProfile) ;



        //add file on Firebase and got Download Link
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    image.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}



        //updates the app according to data change/loads user data
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = mAuth.getCurrentUser();
                uid = user.getUid();

                //search may be improved, there is a sec delay time while loading user data
                for(DataSnapshot ds:dataSnapshot.child("Users").getChildren()){
                    //System.out.println(ds.child("uid").getValue());

                    if ( uid.equals(ds.child("uid").getValue())) {
                        userFound = ds.getValue(User.class);
                        break;
                        //System.out.println(userFound.getEmail());

                    }

                }

                TextView txtusername = (TextView) findViewById(R.id.txtUsername);
                txtusername.setText(userFound.getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());

            }
        });

        //initialise buttons
        Button btnExplorar = (Button) findViewById(R.id.btnExplorar);
        btnExplorar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, ExplorAR.class);
                startActivity(intent);
            }
        });

        Button btnCreatar = (Button) findViewById(R.id.btnEventCreator);
        btnCreatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Profile.this, EventCreator.class);
                startActivity(intent);
            }
        });

    }


}