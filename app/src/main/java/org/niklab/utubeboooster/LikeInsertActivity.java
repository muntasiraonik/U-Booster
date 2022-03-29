package org.niklab.utubeboooster;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.VideoGetRatingResponse;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LikeInsertActivity extends AppCompatActivity {

    private String Uid;
    private GoogleAccountCredential mCredential;
    private String VID;
    private String Email;
    private String UIID;
    DatabaseReference coinsRef,viewsRef,ExchangeRef;
    private Long cost;
    private ProgressDialog progressDialog;
    private int Date;
    private Button Like,Done;
    private TextView textView;
    private com.facebook.ads.InterstitialAd interstitialAd;
    private AdView adView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_insert);


        AudienceNetworkAds.initialize(this);
        adView = new com.facebook.ads.AdView(this, "2580157178767459_2580188352097675", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();



        interstitialAd = new com.facebook.ads.InterstitialAd(this, "2580157178767459_2580190278764149");
        interstitialAd.loadAd();

        VID = Objects.requireNonNull(getIntent().getExtras()).get("Link").toString();


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

        Like = findViewById(R.id.B_Like);
        Done = findViewById(R.id.Done);
        textView = findViewById(R.id.textView7);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();



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



        Picasso.get().load(image).into(imageView);





        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(Uid).child("Coins");
        viewsRef = rootRef.child("LikeList").child(UIID).child("viewsDone");
        ExchangeRef = rootRef.child(Uid).child("Exchange");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Like");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        }


        Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                rootRef.child(Uid).child("Date").child("time1").setValue(Date);

                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                new LikeInsertActivity.Check().execute();

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
                        new LikeInsertActivity.Check2().execute();
                    }
                }, 5000);

            }
        });







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

    @Keep
    @SuppressLint("StaticFieldLeak")
    class Check extends AsyncTask<Object,Object,Objects> {

        @Override
        protected Objects doInBackground(Object... objects) {

            if (Email!=null){

                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(YouTubeScopes.YOUTUBE))
                        .setBackOff(new ExponentialBackOff());






                mCredential.setSelectedAccountName(Email);


                HttpTransport transport = new NetHttpTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                YouTube youtubeService = new YouTube.Builder(
                        transport, jsonFactory, mCredential)
                        .setApplicationName(LikeInsertActivity.this.getResources().getString(R.string.app_name))
                        .build();
                // Define and execute the API request

                try {
                    YouTube.Videos.GetRating request = youtubeService.videos()
                            .getRating(VID);
                    VideoGetRatingResponse response = request.execute();

                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray snippet = jsonObject.getJSONArray("items");




                    JSONObject item = (JSONObject) snippet.get(0);

                    String t = item.getString("rating");



                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (t.equals("like")){
                                progressDialog.dismiss();
                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                rootRef.child("LikeList").child(UIID).child(Uid).setValue("1");
                                Toast.makeText(LikeInsertActivity.this, "Already Liked", Toast.LENGTH_SHORT).show();
                                finish();


                                if (interstitialAd.isAdLoaded()){
                                    interstitialAd.show();
                                }

                            }else if (t.equals("none")){


                                progressDialog.dismiss();
                                Like.setVisibility(View.GONE);
                                Done.setVisibility(View.VISIBLE);

                                startActivity(new Intent(Intent.ACTION_VIEW,   Uri.parse("http://www.youtube.com/watch?v="+VID)));
                                Toast.makeText(LikeInsertActivity.this, "Like the Video and press back button.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }else {
                progressDialog.dismiss();
            }


            return null;
        }
    }



    private void showAlert(String paramString) {
        new AlertDialog.Builder(this).setTitle("Message").setMessage(paramString).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.dismiss();
            }
        }).setIcon(R.drawable.alert_circle_outline).show();
    }

    @Keep
    @SuppressLint("StaticFieldLeak")
    class Check2 extends AsyncTask<Object,Object,Objects> {

        @Override
        protected Objects doInBackground(Object... objects) {

            if (Email!=null){

                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(YouTubeScopes.YOUTUBE))
                        .setBackOff(new ExponentialBackOff());






                mCredential.setSelectedAccountName(Email);


                HttpTransport transport = new NetHttpTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                YouTube youtubeService = new YouTube.Builder(
                        transport, jsonFactory, mCredential)
                        .setApplicationName(LikeInsertActivity.this.getResources().getString(R.string.app_name))
                        .build();
                // Define and execute the API request

                try {
                    YouTube.Videos.GetRating request = youtubeService.videos()
                            .getRating(VID);
                    VideoGetRatingResponse response = request.execute();

                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray snippet = jsonObject.getJSONArray("items");




                    JSONObject item = (JSONObject) snippet.get(0);

                    String t = item.getString("rating");



                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (t.equals("like")){
                                progressDialog.dismiss();
                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                rootRef.child("LikeList").child(UIID).child(Uid).setValue("1");
                                incrementCoins(cost);
                                incrementExchange(1);
                                incrementViews();
                                Toast.makeText(LikeInsertActivity.this, "you have got  "+cost+" Points", Toast.LENGTH_SHORT).show();
                                finish();
                                if (interstitialAd.isAdLoaded()){
                                    interstitialAd.show();
                                }

                            }else if (t.equals("none")){
                                showAlert("you didn't like the video or email on your youtube app doesn't match with app email.");
                                Like.setVisibility(View.VISIBLE);
                                Done.setVisibility(View.INVISIBLE);


                            }

                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                progressDialog.dismiss();
            }


            return null;
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
            DatabaseReference UBoosterRef = rootRef.child("LikeList").child(UIID);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(Uid).exists()){
                        Like.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);

                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {


                }
            };
        UBoosterRef.addValueEventListener(eventListener);






    }



    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss(); // try this
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


}
