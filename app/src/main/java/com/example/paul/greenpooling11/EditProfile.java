package com.example.paul.greenpooling11;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EditProfile extends Activity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    ProgressDialog pd;
    TextView errorText;
    Spinner carMakeSpinner;
    Spinner carModelSpinner;
    String dbCarMake,dbCarModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = mAuth.getCurrentUser().getUid();

        final EditText nameView = (EditText) findViewById(R.id.name);
        final EditText ageView = (EditText) findViewById(R.id.ageEdit);
        final EditText locationView = (EditText) findViewById(R.id.locationEdit);
        final EditText emailView = (EditText) findViewById(R.id.emailEdit);
        final EditText bioView = (EditText) findViewById(R.id.bio);
        Button submit = (Button) findViewById(R.id.submit);
        final RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderRadio);
        genderGroup.check(R.id.maleRadio);

        //Bundle extras = getIntent().getExtras();

        new EditProfile.JsonTask().execute("https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json", "getMakes");
        //new JsonTask().execute("https://api.edmunds.com/api/vehicle/v2/"+"nissan"+"?state="+"used"+"&year="+"1999"+"&view=basic&fmt=json&api_key=tv2b9bnddtksqg6pwxfz6875");
        final Spinner chattyPref = (Spinner) findViewById(R.id.chattyPref);
        String[] chattyPrefArray = {"--Nothing Selected--","I'm chatty","I'm quiet"};//getResources().getStringArray(R.array.chatty_pref_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, chattyPrefArray);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        chattyPref.setAdapter(adapter);

        final Spinner smokingPref = (Spinner) findViewById(R.id.smokingPref);
        String[] smokingPrefArray = {"--Nothing Selected--","I allow smoking in my car","I don't allow smoking in my car"};//getResources().getStringArray(R.array.smoking_pref_array);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, smokingPrefArray);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        smokingPref.setAdapter(adapter);

        Integer[] seats = new Integer[]{0,1,2,3,4,5,6,7,8,9,10};
        final Spinner seatSpinner = (Spinner) findViewById(R.id.carSeat);
        ArrayAdapter<Integer> adapter1 = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, seats);
        adapter1.setDropDownViewResource(R.layout.custom_spinner_item);
        seatSpinner.setAdapter(adapter1);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ProfilePictureView userImage = (ProfilePictureView) findViewById(R.id.userImage);
                userImage.setProfileId(dataSnapshot.child("users").child(userId).child("fbId").getValue().toString());
                nameView.setText(dataSnapshot.child("users").child(userId).child("name").getValue().toString());
                ageView.setText(dataSnapshot.child("users").child(userId).child("age").getValue().toString());
                locationView.setText(dataSnapshot.child("users").child(userId).child("location").getValue().toString());
                emailView.setText(dataSnapshot.child("users").child(userId).child("email").getValue().toString());
                bioView.setText(dataSnapshot.child("users").child(userId).child("bio").getValue().toString());

                String g = dataSnapshot.child("users").child(userId).child("gender").getValue().toString();

                chattyPref.setSelection(getIndex(chattyPref, dataSnapshot.child("users").child(userId).child("preferences").child("chatty").getValue().toString()));
                smokingPref.setSelection(getIndex(smokingPref, dataSnapshot.child("users").child(userId).child("preferences").child("smoking").getValue().toString()));

                dbCarMake=dataSnapshot.child("users").child(userId).child("car").child("make").getValue().toString();
                dbCarModel=dataSnapshot.child("users").child(userId).child("car").child("model").getValue().toString();

                seatSpinner.setSelection(getIndex(seatSpinner, dataSnapshot.child("users").child(userId).child("car").child("seats").getValue().toString()));

                RadioButton maleRadio =(RadioButton)findViewById(R.id.maleRadio);
                RadioButton femaleRadio =(RadioButton)findViewById(R.id.femaleRadio);

                if(g.equalsIgnoreCase("Male"))
                {
                    maleRadio.setChecked(true);
                }
                else if(g.equalsIgnoreCase("Female")){

                    femaleRadio.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( nameView.getText().toString().trim().equals(""))
                {
                    nameView.setError( "Name is required!" );

                }else if( ageView.getText().toString().trim().equals(""))
                {
                    ageView.setError( "Age is required!" );

                }else if( locationView.getText().toString().trim().equals(""))
                {
                    locationView.setError( "Location is required!" );

                }else if( emailView.getText().toString().trim().equals(""))
                {
                    emailView.setError( "Email is required!" );

                }else if( bioView.getText().toString().trim().equals(""))
                {
                    bioView.setError( "Bio is required!" );

                }else if( chattyPref.getSelectedItem().toString().equals("--Nothing Selected--")) {
                    errorText = (TextView) chattyPref.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Chatty Preference is required!");
                }else if( smokingPref.getSelectedItem().toString().equals("--Nothing Selected--")) {
                    errorText = (TextView) smokingPref.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Smoking Preference is required!");
                }else if( carMakeSpinner.getSelectedItem().toString().equals("--Nothing Selected--")) {
                    errorText = (TextView) carMakeSpinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Car Make is required!");
                }else if( carModelSpinner.getSelectedItem().toString().equals("--Nothing Selected--")) {
                    errorText = (TextView) carModelSpinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Car Model is required!");
                }else if( seatSpinner.getSelectedItem().toString().equals("0") ) {
                    errorText = (TextView) seatSpinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("Car seats is required!");
                }else{
                    mDatabase.child("users").child(userId).child("name").setValue(nameView.getText().toString());
                    mDatabase.child("users").child(userId).child("age").setValue(ageView.getText().toString());

                    int index = genderGroup.indexOfChild(findViewById(genderGroup.getCheckedRadioButtonId()));
                    if(index==0){
                        mDatabase.child("users").child(userId).child("gender").setValue("Male");
                    }else{
                        mDatabase.child("users").child(userId).child("gender").setValue("Female");
                    }
                    mDatabase.child("users").child(userId).child("location").setValue(locationView.getText().toString());
                    mDatabase.child("users").child(userId).child("email").setValue(emailView.getText().toString());
                    mDatabase.child("users").child(userId).child("bio").setValue(bioView.getText().toString());
                    mDatabase.child("users").child(userId).child("preferences").child("chatty").setValue(chattyPref.getSelectedItem().toString());
                    mDatabase.child("users").child(userId).child("preferences").child("smoking").setValue(smokingPref.getSelectedItem().toString());
                    mDatabase.child("users").child(userId).child("car").child("make").setValue(carMakeSpinner.getSelectedItem().toString());
                    mDatabase.child("users").child(userId).child("car").child("model").setValue(carModelSpinner.getSelectedItem().toString());
                    mDatabase.child("users").child(userId).child("car").child("seats").setValue(seatSpinner.getSelectedItem().toString());

                    Toast.makeText(EditProfile.this, "Updated User Profile", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(EditProfile.this, LoggedInActivity.class);
                    startActivity(i);

                }
            }
        });

    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        ArrayList carMakes, carModels;

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(EditProfile.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                if(params[1].equals("getMakes")) {
                    carMakes = new ArrayList();
                    JSONObject jo = new JSONObject(buffer.toString());
                    JSONArray ja = jo.getJSONArray("Results");
                    Log.d("CARS", "Cars" + ja);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject car = ja.getJSONObject(i);
                        carMakes.add(car.getString("MakeName"));
                    }

                    Log.d("CARS", "Car makes: " + carMakes.toString());

                    return "getMakes";

                }else{
                    carModels = new ArrayList();
                    JSONObject jo = new JSONObject(buffer.toString());
                    JSONArray ja = jo.getJSONArray("Results");
                    Log.d("CARS", "Cars" + ja);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject car = ja.getJSONObject(i);
                        carModels.add(car.getString("Model_Name"));
                    }

                    Log.d("CARS", "Car models: " + carModels.toString());

                    return "getModels";
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("RESULT","Result"+result);
            if(result!=null)
            {
                pd.dismiss();
            }
            if(result.equals("getMakes")) {
                Set<String> hs = new HashSet<>();
                hs.addAll(carMakes);
                carMakes.clear();
                carMakes.addAll(hs);
                carMakes.remove("null");
                carMakes.add(0, "--Nothing Selected--");
                Collections.sort(carMakes, String.CASE_INSENSITIVE_ORDER);

                carMakeSpinner = (Spinner) findViewById(R.id.carMake);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfile.this, android.R.layout.simple_spinner_item, carMakes);
                adapter.setDropDownViewResource(R.layout.custom_spinner_item);
                carMakeSpinner.setAdapter(adapter);
                if(dbCarMake!=null) {
                    carMakeSpinner.setSelection(getIndex(carMakeSpinner, dbCarMake));
                    dbCarMake=null;
                }

                /*mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("car").child("make").getValue().toString().equals("")) {
                            Log.d("CARMAKECHECK", "" + getIndex(carMakeSpinner, "Audi"));
                            carMakeSpinner.setSelection(getIndex(carMakeSpinner, dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("car").child("make").getValue().toString()));
                        }

                        if(!dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("car").child("model").getValue().toString().equals("")) {
                            carModelSpinner.setSelection(getIndex(carModelSpinner, dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("car").child("model").getValue().toString()));
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

                carMakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        String makeSelected = parent.getItemAtPosition(pos).toString();
                        makeSelected = makeSelected.replace(" ", "%20");
                        makeSelected = makeSelected.replace(".", "");
                        makeSelected = makeSelected.replace(",", "");
                        Log.d("ITEM", "Make:"+makeSelected);
                        if (!makeSelected.equals("--Nothing Selected--")) {
                            new EditProfile.JsonTask().execute("https://vpic.nhtsa.dot.gov/api/vehicles/getmodelsformake/"+makeSelected+"?format=json", "getModel");
                        }
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }else{
                Set<String> hs = new HashSet<>();
                hs.addAll(carModels);
                carModels.clear();
                carModels.addAll(hs);
                carModels.remove("null");
                carModels.add(0, "--Nothing Selected--");
                Collections.sort(carModels, String.CASE_INSENSITIVE_ORDER);

                carModelSpinner = (Spinner) findViewById(R.id.carModel);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfile.this, android.R.layout.simple_spinner_item, carModels);
                adapter.setDropDownViewResource(R.layout.custom_spinner_item);
                carModelSpinner.setAdapter(adapter);
                if(dbCarModel!=null) {
                    carModelSpinner.setSelection(getIndex(carModelSpinner, dbCarModel));
                    dbCarModel=null;
                }
            }
            //Log.d("JSONResult",result);
        }
    }

}
