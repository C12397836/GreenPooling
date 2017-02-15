package com.example.paul.greenpooling11;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ProfileFragment extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final String userId = mAuth.getCurrentUser().getUid().toString();

        Log.d("User","user:"+userId);
        ProfilePictureView userImage = (ProfilePictureView) rootView.findViewById(R.id.userImage);
        userImage.setProfileId(userId);

        final TextView nameView = (TextView) rootView.findViewById(R.id.name);
        final TextView ageView = (TextView) rootView.findViewById(R.id.ageEdit);
        final TextView genderView = (TextView) rootView.findViewById(R.id.genderEdit);
        final TextView locationView = (TextView) rootView.findViewById(R.id.locationEdit);
        final TextView emailView = (TextView) rootView.findViewById(R.id.emailEdit);
        final TextView bioView = (TextView) rootView.findViewById(R.id.bio);

        final TextView chattyPref = (TextView) rootView.findViewById(R.id.chattyPref);
        final TextView smokingPref = (TextView) rootView.findViewById(R.id.smokingPref);
        final TextView carMake = (TextView) rootView.findViewById(R.id.carMakeText);
        final TextView carModel = (TextView) rootView.findViewById(R.id.carModelText);
        final TextView seats = (TextView) rootView.findViewById(R.id.carSeatText);

        Button signout = (Button) rootView.findViewById(R.id.signout);
        Button edit = (Button) rootView.findViewById(R.id.edit);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                nameView.setText(dataSnapshot.child("users").child(userId).child("name").getValue().toString());
                ageView.setText(dataSnapshot.child("users").child(userId).child("age").getValue().toString());
                locationView.setText(dataSnapshot.child("users").child(userId).child("location").getValue().toString());
                emailView.setText(dataSnapshot.child("users").child(userId).child("email").getValue().toString());
                bioView.setText(dataSnapshot.child("users").child(userId).child("bio").getValue().toString());
                chattyPref.setText(dataSnapshot.child("users").child(userId).child("preferences").child("chatty").getValue().toString());
                smokingPref.setText(dataSnapshot.child("users").child(userId).child("preferences").child("smoking").getValue().toString());
                carMake.setText(dataSnapshot.child("users").child(userId).child("car").child("make").getValue().toString());
                carModel.setText(dataSnapshot.child("users").child(userId).child("car").child("model").getValue().toString());
                seats.setText(dataSnapshot.child("users").child(userId).child("car").child("seats").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                mAuth.signOut();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("Logged-in-status:", "Listener Firing");
                if (user == null) {
                    Log.d("Logged-in-status:", "Signed out");
                    Intent i = new Intent(getActivity(), SplashScreen.class);
                    startActivity(i);
                }else{
                    Log.d("Logged-in-status:", "Signed in");
                }

            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditProfile.class);
                startActivity(i);
            }
        });


        /*if(nameView.getText()==null) {
            //nameView.setText(name);
            //ageView.setText("0");
            //locationView.setText("Dublin");
            //emailView.setText(email);
        }else{
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.d("USERDATA",""+dataSnapshot.getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //addListenerForSingleValueEvent()
        }*/

        /*ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("Firebase-data", "DATA: "+dataSnapshot.getValue());
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.addValueEventListener(userListener);
        */
        /*info.setText("Name: " + name
                + "\nGender: " + gender
                + "\nEmail: " + email
                + "\nBirthday: " + birthday);*/

        /*loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {


                    }*/

        /*
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                mAuth.signOut();
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("Logged-in-status:", "Listener Firing");
                if (user == null) {
                    Log.d("Logged-in-status:", "Signed out");
                    Intent i = new Intent(getActivity(), SplashScreen.class);
                    startActivity(i);
                }else{
                    Log.d("Logged-in-status:", "Signed in");
                }

            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        new JsonTask().execute("https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json", "getMakes");
        //new JsonTask().execute("https://api.edmunds.com/api/vehicle/v2/"+"nissan"+"?state="+"used"+"&year="+"1999"+"&view=basic&fmt=json&api_key=tv2b9bnddtksqg6pwxfz6875");
        final Spinner chattyPref = (Spinner) rootView.findViewById(R.id.chattyPref);
        String[] chattyPrefArray = {"--Nothing Selected--","I'm chatty","I'm quiet"};//getResources().getStringArray(R.array.chatty_pref_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, chattyPrefArray);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        chattyPref.setAdapter(adapter);

        final Spinner smokingPref = (Spinner) rootView.findViewById(R.id.smokingPref);
        String[] smokingPrefArray = {"--Nothing Selected--","I allow smoking in my car","I don't allow smoking in my car"};//getResources().getStringArray(R.array.smoking_pref_array);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, smokingPrefArray);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        smokingPref.setAdapter(adapter);

        Integer[] seats = new Integer[]{0,1,2,3,4,5,6,7,8,9,10};
        final Spinner seatSpinner = (Spinner) rootView.findViewById(R.id.carSeatSpinner);
        ArrayAdapter<Integer> adapter1 = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, seats);
        adapter1.setDropDownViewResource(R.layout.custom_spinner_item);
        seatSpinner.setAdapter(adapter1);

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

                    int index = genderGroup.indexOfChild(rootView.findViewById(genderGroup.getCheckedRadioButtonId()));
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

                    Toast.makeText(getActivity(), "Updated User Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


        return rootView;
    }

    /*private class JsonTask extends AsyncTask<String, String, String> {

        ArrayList carMakes, carModels;

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getActivity());
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

                carMakeSpinner = (Spinner) getActivity().findViewById(R.id.carMakeSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, carMakes);
                adapter.setDropDownViewResource(R.layout.custom_spinner_item);
                carMakeSpinner.setAdapter(adapter);

                carMakeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        String makeSelected = parent.getItemAtPosition(pos).toString();
                        makeSelected = makeSelected.replace(" ", "%20");
                        makeSelected = makeSelected.replace(".", "");
                        makeSelected = makeSelected.replace(",", "");
                        Log.d("ITEM", "Make:"+makeSelected);
                        if (!makeSelected.equals("--Nothing Selected--")) {
                            new JsonTask().execute("https://vpic.nhtsa.dot.gov/api/vehicles/getmodelsformake/"+makeSelected+"?format=json", "getModel");
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

                carModelSpinner = (Spinner) getActivity().findViewById(R.id.carModelSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, carModels);
                adapter.setDropDownViewResource(R.layout.custom_spinner_item);
                carModelSpinner.setAdapter(adapter);
            }


            //Log.d("JSONResult",result);
        }
    }*/
}
