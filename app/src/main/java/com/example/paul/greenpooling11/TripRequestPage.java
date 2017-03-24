package com.example.paul.greenpooling11;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TripRequestPage extends Activity {

    String tripId, passengerId, driverId, driverName;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_request_page);

        //signInDriver();

        final TextView leaving = (TextView) findViewById(R.id.leaving);
        final TextView origin = (TextView) findViewById(R.id.origin);
        final TextView destination = (TextView) findViewById(R.id.destination);
        final TextView returing = (TextView) findViewById(R.id.returning);
        final TextView passengerNameView = (TextView) findViewById(R.id.passengerName);

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            tripId = startingIntent.getStringExtra("tripId");
            passengerId = startingIntent.getStringExtra("passengerId");
        }

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                driverId=dataSnapshot.child("trips").child(tripId).child("driver").child("userId").getValue().toString();
                driverName=dataSnapshot.child("users").child(driverId).child("name").getValue().toString();

                if(!mAuth.getCurrentUser().getUid().equals(driverId)){
                    signInDriver();
                }else{
                    ImageView userImage = (ImageView) findViewById(R.id.userImage);
                    String imageUri= dataSnapshot.child("users").child(passengerId).child("userImage").getValue().toString();
                    Picasso.with(TripRequestPage.this)
                            .load(imageUri)
                            .into(userImage);
                    passengerNameView.setText(dataSnapshot.child("users").child(passengerId).child("name").getValue().toString());

                    origin.setText(dataSnapshot.child("trips").child(tripId).child("driver").child("origin").getValue().toString());
                    destination.setText(dataSnapshot.child("trips").child(tripId).child("driver").child("destination").getValue().toString());

                    String date = dataSnapshot.child("trips").child(tripId).child("driver").child("date").getValue().toString();
                    String time = dataSnapshot.child("trips").child(tripId).child("driver").child("time").getValue().toString();
                    leaving.setText("Leaving: \n"+date + " - "+ time);

                    if(dataSnapshot.child("trips").child(tripId).child("driver").child("returnDate").getValue() == null){
                        returing.setVisibility(View.INVISIBLE);
                    }else{
                        String returnDate = dataSnapshot.child("trips").child(tripId).child("driver").child("returnDate").getValue().toString();
                        String returnTime = dataSnapshot.child("trips").child(tripId).child("driver").child("returnTime").getValue().toString();
                        returing.setText("Returning: \n"+returnDate + " - "+ returnTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void signInDriver(){
        Toast.makeText(this, "Please Sign In As "+driverName, Toast.LENGTH_LONG).show();
        /*LoginManager.getInstance().logOut();
        mAuth.signOut();*/

        Intent theIntent =  new Intent(this, SplashScreen.class);
        theIntent.putExtra("tripId", tripId);
        theIntent.putExtra("passengerId", passengerId);
        startActivity(theIntent);
    }

}
