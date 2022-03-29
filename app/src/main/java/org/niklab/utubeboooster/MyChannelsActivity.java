package org.niklab.utubeboooster;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class MyChannelsActivity extends AppCompatActivity {

    private String Uid;
    private DatabaseReference ViewsSubs;
    private TextView War;
    private ImageView imageView;
    TextView Name,Value,Views;
    ImageView imageView1;
    private CardView cardView;
    private DatabaseReference ViewsRef,ViewsLike,Check;
    DatabaseReference coinsRef;
    TextView Name1,Value1,Views1;
    ImageView imageView3;
    private CardView cardView2;
    TextView Name2,Value2,Views2;
    ImageView imageView4;
    private CardView cardView3;
    private TextView x,y,z;
    private View x1,y1,z1;
    private int viewleft,subleft,likeleft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_channels);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Uid = firebaseUser.getUid();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ViewsSubs = FirebaseDatabase.getInstance().getReference().child("SubsList");
        ViewsRef = FirebaseDatabase.getInstance().getReference().child("ViewsList");
        ViewsLike = FirebaseDatabase.getInstance().getReference().child("LikeList");
        Check = FirebaseDatabase.getInstance().getReference();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(Uid).child("Coins");


        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("My Channels");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageView = findViewById(R.id.imageView);
        War = findViewById(R.id.Nwar);

        Name = findViewById(R.id.Name);
        Value = findViewById(R.id.Value);
        imageView1 = findViewById(R.id.imageView2);
        Views = findViewById(R.id.views);
        cardView = findViewById(R.id.CardView);


        Name1 = findViewById(R.id.Name2);
        Value1 = findViewById(R.id.Value2);
        imageView3 = findViewById(R.id.imageView3);
        Views1 = findViewById(R.id.views2);
        cardView2 = findViewById(R.id.CardView2);

        Name2 = findViewById(R.id.Name3);
        Value2 = findViewById(R.id.Value3);
        imageView4 = findViewById(R.id.imageView4);
        Views2 = findViewById(R.id.views3);
        cardView3 = findViewById(R.id.CardView3);

        x= findViewById(R.id.textView2);
        x1 = findViewById(R.id.v1);
        y = findViewById(R.id.T2);
        z = findViewById(R.id.T3);
        y1 = findViewById(R.id.v2);
        z1 = findViewById(R.id.v3);


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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onStart() {
        super.onStart();

        ViewsSubs.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    x.setVisibility(View.GONE);
                    x1.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);


                }else {
                    String image = dataSnapshot.child("image").getValue(String.class);
                    final String name = dataSnapshot.child("name").getValue(String.class);
                    Long viewsDone = dataSnapshot.child("viewsDone").getValue(long.class);
                    final Long cost = dataSnapshot.child("cost").getValue(Long.class);
                    final Long views = dataSnapshot.child("views").getValue(Long.class);

                    assert views != null;
                    int v = views.intValue();
                    assert viewsDone != null;
                    int vd = viewsDone.intValue();
                    assert cost != null;
                    int c = cost.intValue();

                    subleft = (v - vd)*c;

                    Name.setText(name);
                    Value.setText(String.valueOf(cost));
                    Picasso.get().load(image).into(imageView1);
                    Views.setText(String.valueOf(viewsDone));
                }


                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(MyChannelsActivity.this, R.style.MyDialogTheme)
                                .setTitle("Warning")
                                .setMessage("Are you sure you want to delete this Channel?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        incrementCoins(subleft);
                                        Toast.makeText(MyChannelsActivity.this,"You have got "+ subleft +" Coins",Toast.LENGTH_LONG).show();
                                        dataSnapshot.getRef().setValue(null);
                                    }
                                })

                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ViewsRef.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    y.setVisibility(View.GONE);
                    y1.setVisibility(View.GONE);
                    cardView2.setVisibility(View.GONE);

                }else {
                    String image = dataSnapshot.child("image").getValue(String.class);
                    final String name = dataSnapshot.child("name").getValue(String.class);
                    Long viewsDone = dataSnapshot.child("viewsDone").getValue(long.class);
                    final Long cost = dataSnapshot.child("cost").getValue(Long.class);
                    final Long views = dataSnapshot.child("views").getValue(Long.class);

                    assert views != null;
                    int v = views.intValue();
                    assert viewsDone != null;
                    int vd = viewsDone.intValue();
                    assert cost != null;
                    int c = cost.intValue();
                    viewleft = (v - vd)*c;

                    assert name != null;
                    String upToNCharacters = name.substring(0, Math.min(name.length(), 20));

                    Name1.setText(upToNCharacters);
                    Value1.setText(String.valueOf(cost));
                    Picasso.get().load(image).into(imageView3);
                    Views1.setText(String.valueOf(viewsDone));
                }


                cardView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(MyChannelsActivity.this, R.style.MyDialogTheme)
                                .setTitle("Warning")
                                .setMessage("Are you sure you want to delete this campaign?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        incrementCoins(viewleft);
                                        Toast.makeText(MyChannelsActivity.this,"You have got "+ viewleft +" Coins",Toast.LENGTH_LONG).show();
                                        dataSnapshot.getRef().setValue(null);
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ViewsLike.child(Uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                     z.setVisibility(View.GONE);
                     z1.setVisibility(View.GONE);
                     cardView3.setVisibility(View.GONE);


                }else {
                    String image = dataSnapshot.child("image").getValue(String.class);
                    final String name = dataSnapshot.child("name").getValue(String.class);
                    Long viewsDone = dataSnapshot.child("viewsDone").getValue(long.class);
                    final Long cost = dataSnapshot.child("cost").getValue(Long.class);
                    final Long views = dataSnapshot.child("views").getValue(Long.class);

                    assert views != null;
                    int v = views.intValue();
                    assert viewsDone != null;
                    int vd = viewsDone.intValue();
                    assert cost != null;
                    int c = cost.intValue();

                    likeleft = (v-vd)*c;



                    assert name != null;
                    String upToNCharacters = name.substring(0, Math.min(name.length(), 20));

                    Name2.setText(upToNCharacters);
                    Value2.setText(String.valueOf(cost));
                    Picasso.get().load(image).into(imageView4);
                    Views2.setText(String.valueOf(viewsDone));
                }


                cardView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(MyChannelsActivity.this, R.style.MyDialogTheme)
                                .setTitle("Warning")
                                .setMessage("Are you sure you want to delete this campaign?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        incrementCoins(likeleft);
                                        Toast.makeText(MyChannelsActivity.this,"You have got "+ likeleft +" Coins",Toast.LENGTH_LONG).show();
                                        dataSnapshot.getRef().setValue(null);
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Check.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("SubsList").child(Uid).exists()&&!dataSnapshot.child("ViewsList").child(Uid).exists()&&!dataSnapshot.child("LikeList").child(Uid).exists()) {
                    imageView.setVisibility(View.VISIBLE);
                    War.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.GONE);
                    cardView2.setVisibility(View.GONE);
                    cardView3.setVisibility(View.GONE);
                    x.setVisibility(View.GONE);
                    y.setVisibility(View.GONE);
                    z.setVisibility(View.GONE);
                    x1.setVisibility(View.GONE);
                    y1.setVisibility(View.GONE);
                    z1.setVisibility(View.GONE);

                }else if (!dataSnapshot.child("SubsList").child(Uid).exists()){
                    x.setVisibility(View.GONE);
                    x1.setVisibility(View.GONE);
                    cardView.setVisibility(View.GONE);
                }else if (!dataSnapshot.child("ViewsList").child(Uid).exists()){
                    cardView2.setVisibility(View.GONE);
                    y.setVisibility(View.GONE);
                    y1.setVisibility(View.GONE);

                }else if (!dataSnapshot.child("LikeList").child(Uid).exists()){
                    z1.setVisibility(View.GONE);
                    z.setVisibility(View.GONE);
                    cardView3.setVisibility(View.GONE);
                }


                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
