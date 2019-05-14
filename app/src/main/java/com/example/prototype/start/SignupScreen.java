package com.example.prototype.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prototype.ExplorAR.ExplorAR;
import com.example.prototype.R;
import com.example.prototype.User.Profile;
import com.example.prototype.User.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

//An activity class where the user can sign up and create a new account
public class SignupScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference dbref;
    private DatabaseReference userRef;
    private static final String TAG = "SignupScreen";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseApp.initializeApp(SignupScreen.this);
        mAuth = FirebaseAuth.getInstance();

        dbref = mFirebaseDatabase.getInstance().getReference();
        userRef = dbref.child("Users");


        final EditText mail = findViewById(R.id.txtMailSignup);
        final EditText txtPassword = findViewById(R.id.txtPasswordSignup);
        final EditText txtUsernameSignup = findViewById(R.id.txtUserNameSignup);

        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        Button btnBack = findViewById(R.id.btnBack);

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();
                String password = txtPassword.getText().toString();
                String username = txtUsernameSignup.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupScreen.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);

                                    String name = username;
                                    String email = user.getEmail();
                                    String uid = user.getUid();
                                    Uri photo = user.getPhotoUrl();

                                    User user1 = new User( name, email, uid, photo);
                                    userRef.push().setValue(user1);

                                    Intent intent = new Intent( SignupScreen.this, ExplorAR.class);
                                    startActivity(intent);


                                } else {
                                    // If sign in fails, display a message to the user.
                                    try {
                                        throw task.getException();
                                    } catch(FirebaseAuthWeakPasswordException e) {
                                        Toast.makeText(SignupScreen.this, "Authentication failed.Password is weak.",
                                                Toast.LENGTH_SHORT).show();
                                    } catch(FirebaseAuthUserCollisionException e) {
                                        Toast.makeText(SignupScreen.this, "Authentication failed.Email address is invalid.",
                                                Toast.LENGTH_SHORT).show();
                                    } catch(Exception e) {
                                        Log.e(TAG, e.getMessage());
                                    }


                                }

                                // ...
                            }
                        });
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SignupScreen.this, LoginScreen.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI( FirebaseUser user) {


    }

    public void makeToast( String message) {
        Toast.makeText( getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}