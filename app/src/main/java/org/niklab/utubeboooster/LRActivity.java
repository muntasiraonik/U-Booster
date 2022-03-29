package org.niklab.utubeboooster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class LRActivity extends AbsRuntimePermission implements GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_PERMISSION = 10;
    TelephonyManager telephonyManager;
    private String android_id;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 1212;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr);

        requestAppPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.GET_ACCOUNTS},

                R.string.msg, REQUEST_PERMISSION);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
            startActivity(new Intent(LRActivity.this, MainActivity.class));
        }

        SignInButton signInButton = findViewById(R.id.B_subscribe);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))  // you can request scope here OR at the time of subscribe
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



    }

    private void signIn() {

        progressDialog.setMessage("Signing In Please Wait...");
        progressDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @SuppressLint("HardwareIds")
    @Override
    public void onPermissionsGranted(int requestCode) {
        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        int myversion = Integer.parseInt(android.os.Build.VERSION.SDK);
        if(myversion < 29){
            android_id = telephonyManager.getDeviceId();
        }else {
            android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }




    }


    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss(); // try this
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                assert account != null;

                firebaseAuthWithGoogle(account);
            } else {
                progressDialog.dismiss();
                // check internet connection, display a message to the user.
                Toast.makeText(LRActivity.this, "We need the permission for auto Subscription", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information


                        DatabaseReference rootRef1 = FirebaseDatabase.getInstance().getReference();

                            DatabaseReference UBoosterRef = rootRef1.child("UserId").child(android_id);
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String email = dataSnapshot.getValue(String.class);
                                        assert email != null;

                                        if (email.equals(firebaseAuth.getCurrentUser().getEmail())){
                                            progressDialog.dismiss();
                                            finish();
                                            startActivity(new Intent(LRActivity.this,MainActivity.class));
                                        }else {
                                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                            final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                            assert currentUser != null;
                                            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();

                                                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                                                        Toast.makeText(LRActivity.this,"Please Signin With "+ email,Toast.LENGTH_LONG).show();

                                                    } else {
                                                        progressDialog.dismiss();
                                                       firebaseAuth.signOut();
                                                       mGoogleApiClient.clearDefaultAccountAndReconnect();
                                                        Toast.makeText(LRActivity.this,"Please Signin With "+ email,Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }





                                    }else {
                                        progressDialog.dismiss();
                                        String email = firebaseAuth.getCurrentUser().getEmail();
                                        final String name   = email.substring(0, email.lastIndexOf("@"));
                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                        rootRef.child("UserId").child(android_id).setValue(firebaseAuth.getCurrentUser().getEmail());
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Coins").setValue(0);
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("email").setValue(firebaseAuth.getCurrentUser().getEmail());
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Rewarded").setValue(0);
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Exchange").setValue(0);
                                        rootRef.child("UserName").child(name).setValue(firebaseAuth.getCurrentUser().getUid());
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(name);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                        UBoosterRef.addValueEventListener(eventListener);





                        }
                        else
                        {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LRActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    });





    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
    }



    }








