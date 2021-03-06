package com.example.prototype.HuntAR;

/*
 * Copyright 2018 Google LLC. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.Manifest;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.prototype.R;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.Sun;
import com.google.ar.sceneform.rendering.Light;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//A class that reads images from a folder creates an augmented image database
//and then is able to anchor related models on these images when they are detected by the camera
public class ARActivity extends AppCompatActivity implements Scene.OnUpdateListener {

    private static final String TAG = "ARActivity";

    private ArSceneView arView;
    private Session session;
    private boolean shouldConfigureSession = false;
    private HashMap<String, Integer> IDmap;
    private String[] files;
    private String filepath;
    private File folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "images");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            //add images to file
            toaster("folder was created");
        } else {
           //kill activity folder not created
            toaster("folder was not created");
        }

        filepath = "animals";

        IDmap = new HashMap<String, Integer>();
        IDmap.put("dino.jpg", R.raw.dino);
        IDmap.put("lion.jpg", R.raw.lion);


        //View
        arView = (ArSceneView)findViewById(R.id.arView);
        arView.setLightEstimationEnabled(false);

        //Request Permision
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setupSession();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText( ARActivity.this, "camera permission required", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();




        initSceneView();
    }

    private void initSceneView() {
        arView = (ArSceneView)findViewById(R.id.arView);
        arView.getScene().addOnUpdateListener(this);
    }

    private void setupSession() {
        if (session == null) {
            try {
                session = new Session(this);
            } catch (UnavailableArcoreNotInstalledException e) {
                e.printStackTrace();
            } catch (UnavailableApkTooOldException e) {
                e.printStackTrace();
            } catch (UnavailableSdkTooOldException e) {
                e.printStackTrace();
            } catch (UnavailableDeviceNotCompatibleException e) {
                e.printStackTrace();
            }
            shouldConfigureSession = true;
        }
        if (shouldConfigureSession) {
            configSession();
            shouldConfigureSession = false;
            arView.setupSession(session);
        }

        try {
            session.resume();
            arView.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
            session = null;
            return;
        }
    }

    //sets session config to auto focus camera and latest camera image update mode
    private void configSession()  {
        Config config = new Config(session);
        if(!buildDatabase(config)) {
            Toast.makeText(this,"Error database",Toast.LENGTH_LONG).show();
        }
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        config.setFocusMode(Config.FocusMode.AUTO);
        config.setLightEstimationMode(Config.LightEstimationMode.DISABLED);
        session.configure(config);
    }

    //creates an augmentedimage database from the given images file
    private boolean buildDatabase(Config config) {
        AugmentedImageDatabase augmentedImageDatabase;
        augmentedImageDatabase = new AugmentedImageDatabase(session);

        AssetManager manager = getAssets();
        try {
            files = manager.list(filepath);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "buildDatabase: could not create file");
            return false;
        }

        for ( int i = 0; i < files.length; i++ ) {
            Bitmap bitmap = loadImage( filepath +"/"+ files[i]);
            if (bitmap == null)
                return false;
            System.out.println(files[i]);
            augmentedImageDatabase.addImage(files[i], bitmap);
        }

        config.setAugmentedImageDatabase(augmentedImageDatabase);

        return true;
    }

    //buildDatabase helper method, creates bitmap of given image and name
    private Bitmap loadImage( String name) {
        try {
            InputStream is = getAssets().open(  name);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        Frame frame = arView.getArFrame();


        Collection<AugmentedImage> updateAugmentedImg = frame.getUpdatedTrackables(AugmentedImage.class);

        //adds the tracked nodes as a child to the scene with anchor at center of image
        for (AugmentedImage image:updateAugmentedImg) {
            if (image.getTrackingState() == TrackingState.TRACKING) {
                for ( int i = 0; i < files.length; i++ ) {
                    if (image.getName().equals(files[i])) {
                        MyARNode node = new MyARNode(this, IDmap.get(files[i]));
                        node.setImage(image);
                        arView.getScene().addChild(node);
                    }
                }
            }
            if (image.getTrackingMethod() == AugmentedImage.TrackingMethod.LAST_KNOWN_POSE) {
                List<Node> list = new ArrayList<>(arView.getScene().getChildren());
                for ( Node node : list ) {
                    if ( node instanceof AnchorNode && ((MyARNode)node).getImage().getName().equals(image.getName())) {
                        if ( ((AnchorNode) node).getAnchor() != null ) {
                            ((AnchorNode) node).getAnchor().detach();
                            ((AnchorNode) node).setParent(null);
                        }
                    }
                }
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //checks camera permissions
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setupSession();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText( ARActivity.this, "camera permission required", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

    }

    @Override
    protected void onPause() {
        super.onPause();

        //pauses the scene and the session
        if (session != null) {
            arView.pause();
            session.pause();
        }
    }

    //Helper Toast method for testing purposes
    public void toaster( String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT).show();
    }
}
