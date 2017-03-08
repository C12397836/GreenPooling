package com.example.paul.greenpooling11;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteMatch extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    LatLng originLatLng;
    LatLng destLatLng;
    ArrayList<LatLng> MarkerPoints;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String origin, destination;
    String tripId;
    String userOrigin;
    LatLng userOriginLatLng;
    ArrayList<LatLng> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_match);

        Intent i = getIntent();
        tripId = i.getStringExtra("tripId");

        AutoCompleteTextView userOriginAuto = (AutoCompleteTextView) findViewById(R.id.userOriginAutoComplete) ;

        userOriginAuto.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));

        userOriginAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userOrigin = (String) parent.getItemAtPosition(position);
                Toast.makeText(RouteMatch.this, userOrigin, Toast.LENGTH_SHORT).show();
                userOriginLatLng = getLocationFromAddress(userOrigin);
                if(userOriginLatLng!=null){
                    MarkerPoints.add(userOriginLatLng);
                    MarkerOptions options2 = new MarkerOptions();
                    options2.position(userOriginLatLng);
                    options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mMap.addMarker(options2);

                    if(getClosestPickupOnRoute()!=null){
                        LatLng closestPoint = getClosestPickupOnRoute();
                        MarkerPoints.add(closestPoint);
                        MarkerOptions options3 = new MarkerOptions();
                        options3.position(closestPoint);
                        options3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(options3);
                    }

                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                origin =dataSnapshot.child("trips").child(tripId).child("driver").child("origin").getValue().toString();
                destination= dataSnapshot.child("trips").child(tripId).child("driver").child("destination").getValue().toString();

                originLatLng = getLocationFromAddress(origin);
                destLatLng = getLocationFromAddress(destination);

                if(originLatLng!=null && destLatLng!=null){
                    MarkerPoints = new ArrayList<>();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(RouteMatch.this);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public LatLng getClosestPickupOnRoute(){
        double distance, closest;
        closest = 100000000;
        LatLng closestPoint = null;

        for(int i =0; i<points.size(); i++){
            distance =distance(points.get(i).latitude, points.get(i).longitude, userOriginLatLng.latitude, userOriginLatLng.longitude);
            if(distance < closest){
                closest = distance;
                closestPoint = points.get(i);
            }
        }

        return closestPoint;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(originLatLng != null && destLatLng !=null) {
            MarkerPoints.add(originLatLng);
            MarkerOptions options = new MarkerOptions();
            options.position(originLatLng);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            MarkerPoints.add(destLatLng);
            MarkerOptions options1 = new MarkerOptions();
            options1.position(destLatLng);
            options1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mMap.addMarker(options);
            mMap.addMarker(options1);

            if (MarkerPoints.size() >= 2) {
                LatLng origin = MarkerPoints.get(0);
                LatLng dest = MarkerPoints.get(1);

                String url = getUrl(origin, dest);
                FetchUrl FetchUrl = new FetchUrl();
                FetchUrl.execute(url);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(8));
            }
        }
    }

    public LatLng getLocationFromAddress(String theAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Log.d("jjjjjj", "getLocation"+ theAddress);

        try {
            address = coder.getFromLocationName(theAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

            Log.d("jjjjjj", "getLocation 2 "+ loc.toString());

            return loc;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                Log.d("jjjjj",""+points.toString());

                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}
