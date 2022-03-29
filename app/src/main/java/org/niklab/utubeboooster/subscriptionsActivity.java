package org.niklab.utubeboooster;


import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import pub.devrel.easypermissions.EasyPermissions;

public class subscriptionsActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final int RC_SIGN_IN = 12311;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private String Uid;
    private GoogleAccountCredential mCredential;
    private String Channelid;
    private String Email;
    private String UIID;
    DatabaseReference coinsRef,viewsRef,ExchangeRef;
    private Long cost;
    private ProgressDialog progressDialog;
    private int Date;
    private Button Sub,Done;
    private TextView textView;
    private AdView adView;
    private com.facebook.ads.InterstitialAd interstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subscriptions);
        Channelid = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("Id")).toString();
        String image = Objects.requireNonNull(getIntent().getExtras().get("Image")).toString();
        UIID = Objects.requireNonNull(getIntent().getExtras().get("UId")).toString();
        cost = Long.valueOf(Objects.requireNonNull(getIntent().getExtras().get("cost")).toString());
        String name = Objects.requireNonNull(getIntent().getExtras().get("name")).toString();
        TextView sCoins = findViewById(R.id.Coins);
        sCoins.setText(String.valueOf(cost));
        TextView watch = findViewById(R.id.watching);
        ImageView imageView = findViewById(R.id.imageView3);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Uid = firebaseUser.getUid();
        watch.setText(name);
        Sub = findViewById(R.id.B_subscribe);
        Done = findViewById(R.id.Done);


        Picasso.get().load(image).into(imageView);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        textView = findViewById(R.id.textView7);


        AudienceNetworkAds.initialize(this);
        adView = new AdView(this, "2580157178767459_2580188352097675", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();



        interstitialAd = new com.facebook.ads.InterstitialAd(this, "2580157178767459_2580190278764149");
        interstitialAd.loadAd();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Checkuid();
                progressDialog.dismiss();

            }
        }, 2000);







        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(YouTubeScopes.YOUTUBE))
                .setBackOff(new ExponentialBackOff());



        Sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                rootRef.child(Uid).child("Date").child("time1").setValue(Date);

                progressDialog.setMessage("Please Wait...");
                progressDialog.show();


                new Check().execute();

            }
        });



        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new Check2().execute();
                    }
                }, 5000);
            }
        });









        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(Uid).child("Coins");
        viewsRef = rootRef.child("SubsList").child(UIID).child("viewsDone");
        ExchangeRef = rootRef.child(Uid).child("Exchange");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Subscription");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss(); // try this


    }


    private void showAlert(String paramString) {
        new AlertDialog.Builder(this).setTitle("Message").setMessage(paramString).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.dismiss();
            }
        }).setIcon(R.drawable.alert_circle_outline).show();
    }



    void incrementCoins(final long value) {
        coinsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(value);
                } else {
                    Long points = currentData.getValue(Long.class);
                    assert points != null;
                    int x = points.intValue();
                    Long incrementedPoints = x + value;
                    currentData.setValue(incrementedPoints);

                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot currentData) {
                if (databaseError != null) {
                    Log.d("TAG", "Firebase counter increment failed!");
                } else {
                    Log.d("TAG", "Firebase counter increment succeeded!");
                }
            }
        });
    }


    void incrementExchange(final long value) {
        ExchangeRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(value);
                } else {
                    Long points = currentData.getValue(Long.class);
                    assert points != null;
                    int x = points.intValue();
                    Long incrementedPoints = x + value;
                    currentData.setValue(incrementedPoints);

                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot currentData) {
                if (databaseError != null) {
                    Log.d("TAG", "Firebase counter increment failed!");
                } else {
                    Log.d("TAG", "Firebase counter increment succeeded!");
                }
            }
        });
    }



    void incrementViews(){
        viewsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                if (mutableData.getValue()==null){
                    mutableData.setValue((long) 1);
                }else {
                    Long ViewsDone = mutableData.getValue(Long.class);
                    assert ViewsDone !=null;
                    int x = ViewsDone.intValue();
                    Long incrementViews = x+ (long) 1;
                    mutableData.setValue(incrementViews);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.d("TAG", "Firebase counter increment failed!");
                } else {
                    Log.d("TAG", "Firebase counter increment succeeded!");
                }

            }
        });
    }



    @Keep
    @SuppressLint("StaticFieldLeak")
    class Check extends AsyncTask<Void,Void,String> {


        @Override
        protected String doInBackground(Void... voids) {

            String t = "";




            if (Email!=null){


                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(YouTubeScopes.YOUTUBE))
                        .setBackOff(new ExponentialBackOff());

                mCredential.setSelectedAccountName(Email);


                HttpTransport transport = new NetHttpTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                YouTube youtubeService = new YouTube.Builder(
                        transport, jsonFactory, mCredential)
                        .setApplicationName(subscriptionsActivity.this.getResources().getString(R.string.app_name))
                        .build();
                // Define and execute the API request



                try {

                    YouTube.Subscriptions.List request = youtubeService.subscriptions()
                            .list("snippet,contentDetails");
                    SubscriptionListResponse response = request.setForChannelId(Channelid)
                            .setMine(true)
                            .execute();
                    Log.d("myTag", String.valueOf(response));


                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject snippet = jsonObject.getJSONObject("pageInfo");

                    t = snippet.getString("totalResults");



                } catch (IOException e) {
                    Log.d("myTag", String.valueOf(e));
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                progressDialog.dismiss();
            }


            return t;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                int result = Integer.parseInt(s);

                if (result!=0){
                    progressDialog.dismiss();
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.child("SubsList").child(UIID).child(Uid).setValue("1");
                    Toast.makeText(subscriptionsActivity.this, "Already Subscribed", Toast.LENGTH_SHORT).show();
                    finish();

                    if (interstitialAd.isAdLoaded()){
                        interstitialAd.show();
                    }


                }else if (result==0){
                    progressDialog.dismiss();
                    Sub.setVisibility(View.GONE);
                    Done.setVisibility(View.VISIBLE);
                    startActivity(new Intent(Intent.ACTION_VIEW,   Uri.parse("http://www.youtube.com/channel/"+Channelid)));
                    Toast.makeText(subscriptionsActivity.this, "Subscribe the channel and press back button.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }




        }
    }



    @Keep
    @SuppressLint("StaticFieldLeak")
    class Check2 extends AsyncTask<Void,Void,String> {


        @Override
        protected String doInBackground(Void... voids) {

            String t = "";



            if (Email!=null){


                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(YouTubeScopes.YOUTUBE))
                        .setBackOff(new ExponentialBackOff());

                mCredential.setSelectedAccountName(Email);


                HttpTransport transport = new NetHttpTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                YouTube youtubeService = new YouTube.Builder(
                        transport, jsonFactory, mCredential)
                        .setApplicationName(subscriptionsActivity.this.getResources().getString(R.string.app_name))
                        .build();




                try {

                    YouTube.Subscriptions.List request = youtubeService.subscriptions()
                            .list("snippet,contentDetails");
                    SubscriptionListResponse response = request.setForChannelId(Channelid)
                            .setMine(true)
                            .execute();
                    Log.d("myTag", String.valueOf(response));


                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject snippet = jsonObject.getJSONObject("pageInfo");

                    t = snippet.getString("totalResults");


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                progressDialog.dismiss();
            }


            return t;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int result = Integer.parseInt(s);

            if (result!=0){
                progressDialog.dismiss();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                rootRef.child("SubsList").child(UIID).child(Uid).setValue("1");
                incrementCoins(cost);
                incrementExchange(1);
                incrementViews();
                Toast.makeText(subscriptionsActivity.this, "you have got  "+cost+" Points", Toast.LENGTH_SHORT).show();
                finish();


                if (interstitialAd.isAdLoaded()){
                    interstitialAd.show();
                }


            }else if (result==0){
                progressDialog.dismiss();
                showAlert("you didn't subscribe the channel or email on your youtube app doesn't match with app email.");
                Done.setVisibility(View.GONE);
                Sub.setVisibility(View.VISIBLE);
            }

        }
    }






    @Override
    protected void onStart() {
        super.onStart();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UBoosterRef = rootRef.child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Email = dataSnapshot.child("email").getValue(String.class);

                if (dataSnapshot.child("Date").child("time2").exists()){

                    Long date = dataSnapshot.child("Date").child("time2").getValue(Long.class);
                    assert date != null;
                    Date = date.intValue();

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        };
        UBoosterRef.addValueEventListener(eventListener);
    }




    private void Checkuid(){

            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference UBoosterRef = rootRef.child("SubsList").child(UIID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(Uid).exists()){
                        Sub.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);

                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            };
        UBoosterRef.addValueEventListener(eventListener);




    }






    private void getResultsFromApi() {

        if (! isGooglePlayServicesAvailable())
        {
            acquireGooglePlayServices();
        }
        else if (mCredential.getSelectedAccountName() == null)
        {
            chooseAccount();
        }
        else
        {
            ProgressDialog pDialog = new ProgressDialog(subscriptionsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();
            // handing subscribe task by presenter
        }

    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (apiAvailability.isUserResolvableError(connectionStatusCode))
        {
            Dialog dialog = apiAvailability.getErrorDialog(
                    subscriptionsActivity.this,  // showing dialog to user for getting google play service
                    connectionStatusCode,
                    REQUEST_GOOGLE_PLAY_SERVICES);
            dialog.show();
        }
    }




    private void chooseAccount() {

        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.GET_ACCOUNTS))
        {

            if (Email != null)
            {
                mCredential.setSelectedAccountName(Email);
                getResultsFromApi();

            }

        }
        else
        {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account for YouTube channel subscription.",
                    REQUEST_PERMISSION_GET_ACCOUNTS, android.Manifest.permission.GET_ACCOUNTS);
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {




            case REQUEST_GOOGLE_PLAY_SERVICES: // if user don't have google play service
                if (resultCode != RESULT_OK)
                {
                    Toast.makeText(this, "This app requires Google Play Services. Please " +
                            "install Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    getResultsFromApi();

                }
                break;


            case REQUEST_ACCOUNT_PICKER: // when user select google account
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null)
                {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null)
                    {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();


                    }
                }
                break;


            case REQUEST_AUTHORIZATION: // when your grant account access permission
                if (resultCode == RESULT_OK)
                {
                    getResultsFromApi();

                }
                break;


            case RC_SIGN_IN: // if user do sign in
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess())
                {
                    getResultsFromApi();

                }
                else
                {
                    Toast.makeText(this, "Permission Required if granted then check internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        getResultsFromApi(); // user have granted permission so continue

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "This app needs to access your Google account for YouTube channel subscription.", Toast.LENGTH_SHORT).show();

    }








}
