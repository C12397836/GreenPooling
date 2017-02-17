package com.example.paul.greenpooling11;

import android.support.v4.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class PlanDriverTrip extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_driver_trip);

        final AutoCompleteTextView originAutocompleteView = (AutoCompleteTextView) findViewById(R.id.originAutocomplete);
        originAutocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));

        originAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(PlanDriverTrip.this, description, Toast.LENGTH_SHORT).show();
            }
        });

        final AutoCompleteTextView destAutocompleteView = (AutoCompleteTextView) findViewById(R.id.destAutocomplete);
        destAutocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));

        destAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Toast.makeText(PlanDriverTrip.this, description, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

}
