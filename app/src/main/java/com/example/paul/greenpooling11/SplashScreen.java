package com.example.paul.greenpooling11;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

import static com.facebook.AccessToken.getCurrentAccessToken;

public class SplashScreen extends Activity {

    private TextView info;
    private CallbackManager callbackManager;
    ProfilePictureView userImage;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.splash_screen);
        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        //info = (TextView)findViewById(R.id.info);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        //userImage = (ProfilePictureView) findViewById(R.id.userImage);

        if (loginButton != null) {
            loginButton.setReadPermissions(Arrays.asList(
                   "public_profile", "email", "user_about_me" ,"user_location", "user_birthday", "user_friends"));
            /*loginButton.setReadPermissions(Arrays.asList(
                    "public_profile", "email"));*/

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("TAG", "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                    /*final String userId = loginResult.getAccessToken().getUserId();

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        String name = object.getString("name");
                                        String gender = object.getString("gender");
                                        String email = object.getString("email");
                                        String birthday = object.getString("birthday");

                                        Intent i = new Intent(SplashScreen.this, LoggedInActivity.class);
                                        i.putExtra("userId", userId);
                                        i.putExtra("name", name);
                                        i.putExtra("gender", gender);
                                        i.putExtra("email", email);
                                        i.putExtra("birthday", birthday);

                                        startActivity(i);

                                        Log.i("Profile info:- ", "Name: " + name
                                                + "\nGender: " + gender
                                                + "\nEmail: " + email
                                                + "\nBirthday: " + birthday);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();*/
                }

                @Override
                public void onCancel() {
                    info.setText("Login attempt canceled.");
                }

                @Override
                public void onError(FacebookException e) {
                    info.setText("Login attempt failed.");
                }  });

        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());

                    GraphRequest request = GraphRequest.newMeRequest(
                            getCurrentAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    // Application code
                                    try {
                                        //final String userId = getCurrentAccessToken().getUserId();
                                        final String uid = object.getString("id");
                                        final String name = object.getString("name");
                                        final String gender = object.getString("gender");
                                        final String location = "";//object.getJSONObject("location").getString("name");;
                                        final String email = object.getString("email");
                                        final String birthday = object.getString("birthday");
                                        final String bio = "";//object.getString("about");
                                        final Boolean b;

                                        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String userId = mAuth.getCurrentUser().getUid();
                                                //Log.d("USERDATA",""+dataSnapshot.getValue().toString());
                                                if(dataSnapshot.child("users").hasChild(userId)) {
                                                    //normal login
                                                    Intent i = new Intent(SplashScreen.this, LoggedInActivity.class);
                                                    startActivity(i);
                                                }else{
                                                    //first time login
                                                    Intent i = new Intent(SplashScreen.this, EditProfile.class);
                                                    mDatabase.child("users").child(userId).child("fbId").setValue(uid);

                                                    mDatabase.child("users").child(userId).child("name").setValue(name);
                                                    mDatabase.child("users").child(userId).child("age").setValue("");
                                                    mDatabase.child("users").child(userId).child("gender").setValue(gender);
                                                    mDatabase.child("users").child(userId).child("location").setValue("");
                                                    mDatabase.child("users").child(userId).child("email").setValue(email);
                                                    mDatabase.child("users").child(userId).child("bio").setValue(bio);
                                                    mDatabase.child("users").child(userId).child("preferences").child("chatty").setValue("");
                                                    mDatabase.child("users").child(userId).child("preferences").child("smoking").setValue("");
                                                    mDatabase.child("users").child(userId).child("car").child("make").setValue("");
                                                    mDatabase.child("users").child(userId).child("car").child("model").setValue("");
                                                    mDatabase.child("users").child(userId).child("car").child("seats").setValue("");


                                                    /*i.putExtra("name", name);
                                                    i.putExtra("gender", gender);
                                                    i.putExtra("email", email);
                                                    i.putExtra("birthday", birthday);
                                                    i.putExtra("location", location);
                                                    i.putExtra("bio", bio);*/
                                                    startActivity(i);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        //addListenerForSingleValueEvent()

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender,birthday");
                    request.setParameters(parameters);
                    request.executeAsync();

                    /*for (UserInfo profile : user.getProviderData()) {
                        String profileDisplayName = profile.getDisplayName();
                        Log.d("INFO", "Provider data:"+ user.getProviderData());
                        String gender =profile.getString("");
                        //String birthday;
                        String profileEmail = profile.getEmail();

                    }*/

                } else {
                    // User is signed out
                    LoginManager.getInstance().logOut();
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "signInWithCredential", task.getException());
                            Toast.makeText(SplashScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}