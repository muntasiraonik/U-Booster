package org.niklab.utubeboooster;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

public class ViewVideos extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private String Link;
    private Long cost;
    private TextView sCount;
    private boolean ispaused = false;
    private boolean iscancel = false;
    private long remainingtime = 0;
    DatabaseReference coinsRef,viewsRef;
    private String Uid,UIID;
    private int Date;
    YouTubePlayerView youTubePlayerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_videos);

        Link = getIntent().getExtras().get("Link").toString();
        cost = Long.valueOf(getIntent().getExtras().get("cost").toString());
        String name = getIntent().getExtras().get("name").toString();
        UIID = getIntent().getExtras().get("UId").toString();
        youTubePlayerView = findViewById(R.id.playVideo);
        youTubePlayerView.initialize(getString(R.string.service_link),this);
        sCount = findViewById(R.id.secCount);
        Button close = findViewById(R.id.Close);
        TextView sCoins = findViewById(R.id.Coins);
        sCoins.setText(String.valueOf(cost));
        TextView watch = findViewById(R.id.watching);
        watch.setText("Watching :"+ name);


        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Uid = firebaseUser.getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(Uid).child("Coins");
        viewsRef = rootRef.child("ViewsList").child(UIID).child("viewsDone");




        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Views Exchange");
        toolbar.setTitleTextColor(Color.WHITE);



        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ViewVideos.this, R.style.MyDialogTheme)
                        .setTitle("Warning")
                        .setMessage("Are you sure to close it?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });




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

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);

        if (!b){
            String link =  Link.substring(Link.indexOf("=") + 1);
            youTubePlayer.loadVideo(link);

        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_LONG).show();

    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
            ispaused = false;

            long milisec = remainingtime;
            long countdowninterval = 1000;


            new CountDownTimer(milisec,countdowninterval){

                @Override
                public void onTick(long millisUntilFinished) {
                    if (ispaused || iscancel){
                        cancel();
                    }else {
                        sCount.setText(""+millisUntilFinished/1000);
                        remainingtime = millisUntilFinished;
                    }

                }

                @Override
                public void onFinish() {





                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.child(Uid).child("Date").child("time1").setValue(Date);
                    rootRef.child("ViewsList").child(UIID).child(Uid).setValue(Date);
                    incrementViews();
                    incrementCoins(cost);
                    Toast.makeText(ViewVideos.this,"You have got "+ cost+" Coins",Toast.LENGTH_LONG).show();
                    finish();


                }

            }.start();

        }

        @Override
        public void onPaused() {
            ispaused =true;

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UBoosterRef = rootRef.child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {
            ispaused = false;
            iscancel = false;

            long milisec = 62000;
            long countdowninterval = 1;



            new CountDownTimer(milisec,countdowninterval){

                @Override
                public void onTick(long millisUntilFinished) {
                    if (ispaused || iscancel){
                        cancel();
                    }else {
                        sCount.setText(""+millisUntilFinished/1000);
                        remainingtime = millisUntilFinished;
                    }

                }

                @Override
                public void onFinish() {


                }

            }.start();

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };


    @Override
    public void onBackPressed() {


        new AlertDialog.Builder(ViewVideos.this, R.style.MyDialogTheme)
                .setTitle("Warning")
                .setMessage("Are you sure to close it?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }


}
