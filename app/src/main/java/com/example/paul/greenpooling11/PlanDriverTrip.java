package com.example.paul.greenpooling11;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.UUID;

public class PlanDriverTrip extends FragmentActivity{

    static Button startDate;
    static Button startTime;
    static Button endDate;
    static Button endTime;
    static int datePickerInput;
    static int timePickerInput;
    TextView errorText;
    String tripId;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            if(datePickerInput ==R.id.startDate){
                startDate.setText(day+"/"+month+"/"+year);
            }else if(datePickerInput == R.id.endDate){
                endDate.setText(day+"/"+month+"/"+year);

            }else{

            }
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(timePickerInput ==R.id.startTime){
                startTime.setText(hourOfDay+":"+ String.valueOf(minute));
            }else if(timePickerInput == R.id.endTime){
                endTime.setText(hourOfDay+":"+minute);
            }else{

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_driver_trip);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        final AutoCompleteTextView originAutocompleteView = (AutoCompleteTextView) findViewById(R.id.originAutocomplete);
        final AutoCompleteTextView destAutocompleteView = (AutoCompleteTextView) findViewById(R.id.destAutocomplete);
        final CheckBox roundTrip = (CheckBox) findViewById(R.id.roundTrip);

        final NumberPicker seatNumber = (NumberPicker) findViewById(R.id.seatNumber);

        final Spinner detour = (Spinner) findViewById(R.id.detour);
        Button submit = (Button) findViewById(R.id.tripSubmit);

        startDate = (Button) findViewById(R.id.startDate);
        startTime = (Button) findViewById(R.id.startTime);
        endDate = (Button) findViewById(R.id.endDate);
        endTime = (Button) findViewById(R.id.endTime);

        originAutocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));

        originAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(PlanDriverTrip.this, description, Toast.LENGTH_SHORT).show();
            }
        });

        destAutocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));

        destAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(PlanDriverTrip.this, description, Toast.LENGTH_SHORT).show();
            }
        });

        roundTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(roundTrip.isChecked()){
                    endDate.setVisibility(View.VISIBLE);
                    endTime.setVisibility(View.VISIBLE);
                }else{
                    endDate.setVisibility(View.INVISIBLE);
                    endTime.setVisibility(View.INVISIBLE);
                }
            }
        });


        seatNumber.setMinValue(0);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seatNumber.setMaxValue(Integer.parseInt(dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("car").child("seats").getValue().toString()));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        seatNumber.setWrapSelectorWheel(true);
        seatNumber.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                //tv.setText("Selected Number : " + newVal);
            }
        });

        String[] detourArray = {"--Nothing Selected--","Close","Medium", "Far"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(PlanDriverTrip.this, android.R.layout.simple_spinner_item, detourArray);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        detour.setAdapter(adapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( originAutocompleteView.getText().toString().trim().equals(""))
                {
                    originAutocompleteView.setError( "Origin is required!" );
                }else if( destAutocompleteView.getText().toString().trim().equals("")) {
                    destAutocompleteView.setError("Destination is required!");
                }else if(startDate.getText().toString().trim().equals("Pick Date")){
                    startDate.setError("Date required");
                }else if(endDate.getText().toString().trim().equals("Pick Return Date") && roundTrip.isChecked()){
                    endDate.setError("Date required");
                }else if(startTime.getText().toString().trim().equals("Time")){
                    startTime.setError("Time required");
                }else if(endTime.getText().toString().trim().equals("Time") && roundTrip.isChecked()){
                    endTime.setError("Time required");
                }else if( detour.getSelectedItem().toString().equals("--Nothing Selected--")) {
                    errorText = (TextView) detour.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Detour is required!");
                }else{

                    tripId= String.valueOf(UUID.randomUUID());
                    mDatabase.child("trips").child(tripId).child("driver").child("userId").setValue(mAuth.getCurrentUser().getUid().toString());
                    mDatabase.child("trips").child(tripId).child("driver").child("origin").setValue(originAutocompleteView.getText().toString());
                    mDatabase.child("trips").child(tripId).child("driver").child("destination").setValue(destAutocompleteView.getText().toString());
                    mDatabase.child("trips").child(tripId).child("driver").child("date").setValue(startDate.getText().toString());
                    mDatabase.child("trips").child(tripId).child("driver").child("time").setValue(startTime.getText().toString());
                    if(roundTrip.isChecked()){
                        mDatabase.child("trips").child(tripId).child("driver").child("returnDate").setValue(endDate.getText().toString());
                        mDatabase.child("trips").child(tripId).child("driver").child("returnTime").setValue(endTime.getText().toString());
                    }
                    mDatabase.child("trips").child(tripId).child("driver").child("availableSeats").setValue(""+seatNumber.getValue());
                    mDatabase.child("trips").child(tripId).child("driver").child("detour").setValue(detour.getSelectedItem().toString());

                    Toast.makeText(PlanDriverTrip.this, "Creating Trip...", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(PlanDriverTrip.this, LoggedInActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    public void showDatePickerDialog(View v) {
        datePickerInput =v.getId();
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void showTimePickerDialog(View v) {
        timePickerInput =v.getId();
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
