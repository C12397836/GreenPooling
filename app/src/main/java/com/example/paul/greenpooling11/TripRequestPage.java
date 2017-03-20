package com.example.paul.greenpooling11;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

public class TripRequestPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_request_page);

        Intent startingIntent = getIntent();
        if (startingIntent != null) {
            String tripId = startingIntent.getStringExtra("tripId");
            String passengerId = startingIntent.getStringExtra("passengerId");
            Log.d("jjjjjj","TRIPID: "+tripId +"PASSENGERID"+passengerId);
        }
    }

}
