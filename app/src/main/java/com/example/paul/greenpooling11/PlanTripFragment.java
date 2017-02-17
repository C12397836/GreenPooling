package com.example.paul.greenpooling11;

/**
 * Created by Paul on 08/11/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PlanTripFragment extends Fragment {

    public PlanTripFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_plan_trip, container, false);

        Button driverButton = (Button) rootView.findViewById(R.id.driverButton);
        Button passengerButton = (Button) rootView.findViewById(R.id.passengerButton);

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PlanDriverTrip.class);
                startActivity(i);
            }
        });

        passengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PlanPassengerTrip.class);
                startActivity(i);
            }
        });


        return rootView;
    }

}