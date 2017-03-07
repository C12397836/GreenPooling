package com.example.paul.greenpooling11;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanPassengerTrip extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_passenger_trip);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        final List<Trip> data = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("trips");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Trip trip = snapshot.child("driver").getValue(Trip.class);
                    Log.d("jjjjj", "Key: "+snapshot.getKey());
                    trip.setTripId(snapshot.getKey().toString());
                    data.add(trip);
                    TripAdapter adapter = new TripAdapter(data, getApplication());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PlanPassengerTrip.this));
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        TextView tripIdTx = (TextView) view.findViewById(R.id.tripId);
                        String tripId = tripIdTx.getText().toString();
                        //Toast.makeText(PlanPassengerTrip.this, ""+tripId, Toast.LENGTH_SHORT).show();

                        //Intent i = new Intent(this, Trip);
                    }
                })
        );

        Log.d("jjjj", "Bottom"+data.toString());
    }

}
