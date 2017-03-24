package com.example.paul.greenpooling11;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ProfileFragment extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final String userId = mAuth.getCurrentUser().getUid().toString();

        Log.d("User","user:"+userId);
        //ProfilePictureView userImage = (ProfilePictureView) rootView.findViewById(R.id.userImage);
        //userImage.setProfileId(userId);

        final ImageView userImage = (ImageView) rootView.findViewById(R.id.userImage);

        final TextView nameView = (TextView) rootView.findViewById(R.id.name);
        final TextView ageView = (TextView) rootView.findViewById(R.id.ageEdit);
        final TextView genderView = (TextView) rootView.findViewById(R.id.genderEdit);
        final TextView locationView = (TextView) rootView.findViewById(R.id.locationEdit);
        final TextView emailView = (TextView) rootView.findViewById(R.id.emailEdit);
        final TextView bioView = (TextView) rootView.findViewById(R.id.bio);

        final TextView chattyPref = (TextView) rootView.findViewById(R.id.chattyPref);
        final TextView smokingPref = (TextView) rootView.findViewById(R.id.smokingPref);
        final TextView carMake = (TextView) rootView.findViewById(R.id.carMake);
        final TextView carModel = (TextView) rootView.findViewById(R.id.carModel);
        final TextView seats = (TextView) rootView.findViewById(R.id.carSeat);

        Button signout = (Button) rootView.findViewById(R.id.signout);
        FloatingActionButton edit = (FloatingActionButton) rootView.findViewById(R.id.edit);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Uri imageUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                Picasso.with(getActivity())
                        .load(imageUri)
                        .into(userImage);
                nameView.setText(dataSnapshot.child("users").child(userId).child("name").getValue().toString());
                ageView.setText(dataSnapshot.child("users").child(userId).child("age").getValue().toString());
                genderView.setText(dataSnapshot.child("users").child(userId).child("gender").getValue().toString());
                locationView.setText(dataSnapshot.child("users").child(userId).child("location").getValue().toString());
                emailView.setText(dataSnapshot.child("users").child(userId).child("email").getValue().toString());
                bioView.setText("\""+dataSnapshot.child("users").child(userId).child("bio").getValue().toString()+"\"");
                chattyPref.setText(dataSnapshot.child("users").child(userId).child("preferences").child("chatty").getValue().toString());
                smokingPref.setText(dataSnapshot.child("users").child(userId).child("preferences").child("smoking").getValue().toString());
                carMake.setText(dataSnapshot.child("users").child(userId).child("car").child("make").getValue().toString());
                carModel.setText(dataSnapshot.child("users").child(userId).child("car").child("model").getValue().toString());
                seats.setText(dataSnapshot.child("users").child(userId).child("car").child("seats").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                mAuth.signOut();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("Logged-in-status:", "Listener Firing");
                if (user == null) {
                    Log.d("Logged-in-status:", "Signed out");
                    Intent i = new Intent(getActivity(), SplashScreen.class);
                    startActivity(i);
                }else{
                    Log.d("Logged-in-status:", "Signed in");
                }

            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfile.class);
                startActivity(i);
            }
        });

        return rootView;
    }
}
