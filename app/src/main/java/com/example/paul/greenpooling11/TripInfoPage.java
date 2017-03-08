package com.example.paul.greenpooling11;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripInfoPage extends Activity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info_page);

        Intent i = getIntent();
        tripId = i.getStringExtra("tripId");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        final TextView userName = (TextView) findViewById(R.id.userName);
        final TextView leaving = (TextView) findViewById(R.id.leaving);
        final TextView origin = (TextView) findViewById(R.id.origin);
        final TextView destination = (TextView) findViewById(R.id.destination);
        final TextView availableSeats = (TextView) findViewById(R.id.availableSeats);
        final TextView detour = (TextView) findViewById(R.id.detour);
        final TextView returing = (TextView) findViewById(R.id.returning);
        final Button submit = (Button) findViewById(R.id.submit);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.userImage);
                String userId = dataSnapshot.child("trips").child(tripId).child("driver").child("userId").getValue().toString();
                userImage.setProfileId(dataSnapshot.child("users").child(userId).child("fbId").getValue().toString());
                userName.setText(dataSnapshot.child("users").child(userId).child("name").getValue().toString());

                String date = dataSnapshot.child("trips").child(tripId).child("driver").child("date").getValue().toString();
                String time = dataSnapshot.child("trips").child(tripId).child("driver").child("time").getValue().toString();
                leaving.setText(date + " - "+ time);

                origin.setText(dataSnapshot.child("trips").child(tripId).child("driver").child("origin").getValue().toString());
                destination.setText(dataSnapshot.child("trips").child(tripId).child("driver").child("destination").getValue().toString());
                availableSeats.setText(dataSnapshot.child("trips").child(tripId).child("driver").child("availableSeats").getValue().toString()+" seats left");
                detour.setText("Detour: "+dataSnapshot.child("trips").child(tripId).child("driver").child("detour").getValue().toString());

                if(dataSnapshot.child("trips").child(tripId).child("driver").child("returnDate").getValue() == null){
                    returing.setVisibility(View.INVISIBLE);
                }else{
                    String returnDate = dataSnapshot.child("trips").child(tripId).child("driver").child("returnDate").getValue().toString();
                    String returnTime = dataSnapshot.child("trips").child(tripId).child("driver").child("returnTime").getValue().toString();
                    returing.setText(returnDate + " - "+ returnTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(TripInfoPage.this, RouteMatch.class);
                i.putExtra("tripId", tripId);
                startActivity(i);
            }
        });

    }
}
