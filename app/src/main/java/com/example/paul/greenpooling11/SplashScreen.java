package com.example.paul.greenpooling11;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import static com.facebook.AccessToken.getCurrentAccessToken;

public class SplashScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private TextView info;
    private CallbackManager callbackManager;

    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog pg;
    Boolean requestPage=false;
    String tripId, passengerId;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.splash_screen);

        mAuth = FirebaseAuth.getInstance();

        pg= new ProgressDialog(SplashScreen.this);

        if(getIntent().getExtras() != null && getIntent().getExtras().getString("tripId") != null) {
            Log.d("jjjjj", "Extras and tripid");
            tripId = getIntent().getExtras().getString("tripId");
            passengerId = getIntent().getExtras().getString("passengerId");
            requestPage = true;
            LoginManager.getInstance().logOut();
            mAuth.signOut();
        }


        if(mAuth.getCurrentUser() != null){
            pg.setMessage("Signing in ...");
            pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pg.setMax(100);
            pg.show();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        if (loginButton != null) {
            loginButton.setReadPermissions(Arrays.asList(
                   "public_profile", "email", "user_about_me" ,"user_location", "user_birthday", "user_friends"));

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d("TAG", "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                    pg.setMessage("Signin with Facebook ...");
                    pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pg.setMax(100);
                    pg.setCancelable(false);
                    pg.show();
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
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                pg.setProgress(30);

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    pg.setProgress(40);
                    new LogInUserTask().execute();

                } else {
                    // User is signed out
                    LoginManager.getInstance().logOut();
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase

                pg.setMessage("Signin with Google ...");
                pg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pg.setMax(100);
                pg.setCancelable(false);
                pg.show();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Log.d("jjjj", "FAILED: "+result.getStatus().getStatusMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Signin", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Signin", "signInWithCredential:onComplete:" + task.isSuccessful());



                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("Signin", "signInWithCredential", task.getException());
                            Toast.makeText(SplashScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private class LogInUserTask extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg.setProgress(45);
        }

        @Override
        protected String doInBackground(String[] params) {

            FirebaseUser user = mAuth.getCurrentUser();

            pg.setProgress(50);

            Log.d("jjjj", "onAuthStateChanged:signed_in:" + user.getUid());

            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    pg.setProgress(60);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userId = mAuth.getCurrentUser().getUid();

                    String imageUri = user.getPhotoUrl().toString();
                    mDatabase.child("users").child(userId).child("userImage").setValue(imageUri);

                    Intent intent = new Intent(SplashScreen.this, RegistrationIntentService.class);
                    startService(intent);

                    pg.setProgress(70);

                    if(dataSnapshot.child("users").hasChild(userId)) {
                        //normal login
                        Intent i;
                        if(requestPage){
                            Log.d("jjj", "TripRequest....");
                            i = new Intent(SplashScreen.this, TripRequestPage.class);
                            i.putExtra("tripId",tripId);
                            i.putExtra("passengerId",passengerId);
                        }else {
                            i = new Intent(SplashScreen.this, LoggedInActivity.class);
                        }
                        pg.setProgress(100);
                        pg.dismiss();
                        startActivity(i);
                    }else{
                        //first time login

                        String name="";
                        String email="";

                        pg.setProgress(75);

                        name = user.getDisplayName();
                        email = user.getEmail();

                        Intent i = new Intent(SplashScreen.this, EditProfile.class);

                        pg.setProgress(80);

                        mDatabase.child("users").child(userId).child("name").setValue(name);
                        mDatabase.child("users").child(userId).child("age").setValue("");
                        mDatabase.child("users").child(userId).child("gender").setValue("");
                        mDatabase.child("users").child(userId).child("location").setValue("");
                        mDatabase.child("users").child(userId).child("email").setValue(email);
                        mDatabase.child("users").child(userId).child("bio").setValue("");
                        mDatabase.child("users").child(userId).child("preferences").child("chatty").setValue("");
                        mDatabase.child("users").child(userId).child("preferences").child("smoking").setValue("");

                        pg.setProgress(90);

                        mDatabase.child("users").child(userId).child("car").child("make").setValue("");
                        mDatabase.child("users").child(userId).child("car").child("model").setValue("");
                        mDatabase.child("users").child(userId).child("car").child("seats").setValue("");
                        mDatabase.child("users").child(userId).child("rtiLocation").child("lat").setValue("0");
                        mDatabase.child("users").child(userId).child("rtiLocation").child("lng").setValue("0");

                        pg.setProgress(100);
                        pg.dismiss();
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}