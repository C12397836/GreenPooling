package com.example.paul.greenpooling11;

import android.app.Application;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;

    public TripAdapter(List<Trip> tripList, Application app) {
        this.tripList = tripList;
    }

    @Override
    public TripAdapter.TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.plan_passenger_trip_layout, parent, false);

        return new TripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TripAdapter.TripViewHolder holder, int position) {

        Trip trip = tripList.get(position);
        holder.origin.setText(trip.origin);
        holder.destination.setText(trip.destination);
        holder.leaving.setText("Leaving: "+trip.date+" "+trip.time);
        if(trip.returnDate == null || trip.returnTime==null){
            holder.returning.setVisibility(View.INVISIBLE);
        }else{
            holder.returning.setText("Returning: "+trip.returnDate+" "+trip.returnTime);
        }

        //holder.driverName.setText(trip.userName);

        holder.tripId.setText(trip.tripId);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users/"+trip.userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.driverName.setText(dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {

        protected TextView tripId, origin, destination, leaving, returning, driverName;

        public TripViewHolder(View v) {
            super(v);
            tripId = (TextView) v.findViewById(R.id.tripId);
            origin = (TextView) v.findViewById(R.id.origin);
            destination = (TextView) v.findViewById(R.id.destination);
            leaving = (TextView) v.findViewById(R.id.leaving);
            returning = (TextView) v.findViewById(R.id.returning);
            driverName = (TextView) v.findViewById(R.id.driverName);
        }
    }
}
