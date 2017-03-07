package com.example.paul.greenpooling11;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RTIFragment extends Fragment {

    //private GoogleMap mMap;
    Marker mCurrLocationMarker;

    MapView mMapView;
    private GoogleMap googleMap;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    Marker marker;
    int x=0;

    public RTIFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_rti, container, false);

        /*SupportMapFragment mapFragment =
                (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/


        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String lat = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("rtiLocation").child("lat").getValue().toString();
                        String lng = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("rtiLocation").child("lng").getValue().toString();
                        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        //updateMarker(latLng);

                        if(marker!=null){
                            marker.remove();
                        }

                        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("name").getValue().toString()).snippet("User info: "));
                        if(x==0){
                            CameraPosition cameraPosition = new CameraPosition.Builder().zoom(12).target(latLng).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                        x++;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // For dropping a marker at a point on the Map
                /*LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("HEEEERE", "HERE0000000");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }*/

    /*public void updateMarker(LatLng latLng){
        Log.d("HEEEERE", "HERE1");

        Log.d("HEEEERE", "HERE2");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        Log.d("HEEEERE", "HERE3"+ mMap);

        mCurrLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }*/



    /*public void onLocationChanged(Location location) {


        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }*/
}