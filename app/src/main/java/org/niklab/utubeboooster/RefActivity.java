package org.niklab.utubeboooster;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RefActivity extends AppCompatActivity {
    private EditText inRef;
    private TextView myId;
    private FirebaseAuth firebaseAuth;
    String UID,userN;
    private LinearLayout cardView;
    DatabaseReference coinsRef,refRef,ExchangeRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ref);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Button submit = findViewById(R.id.button4);
        Button copy = findViewById(R.id.button2);
        inRef = findViewById(R.id.editText6);
        myId = findViewById(R.id.textView14);
        cardView = findViewById(R.id.CardView);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Refer");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        assert user != null;
        UID =(user.getUid());
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(UID).child("Coins");
        ExchangeRef = rootRef.child(UID).child("Exchange");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", myId.getText());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "referid Copied to Clipboard",
                        Toast.LENGTH_LONG).show();
            }
        });



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              SubmitRef();
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


    void incrementRefcoins(final long value) {
        refRef.runTransaction(new Transaction.Handler() {
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








    private void SubmitRef(){

        final String ref = inRef.getText().toString().trim();
        if (TextUtils.isEmpty(ref)) {
            Toast.makeText(this, "Please enter Ref Username", Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        if (!ref.equals(userN)){

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference UBoosterRef = rootRef.child("UserName");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(ref).exists()){

                        String uid = dataSnapshot.child(ref).getValue(String.class);
                        assert uid != null;
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        refRef = rootRef.child(uid).child("Coins");
                        rootRef.child(UID).child("DREF").setValue(1);
                        GiveReward();
                        progressDialog.dismiss();
                        Toast.makeText(RefActivity.this, "Successfully refer done ", Toast.LENGTH_LONG).show();



                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(RefActivity.this, "Invalid Ref Id", Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            UBoosterRef.addValueEventListener(eventListener);
        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "You can't ref yourself", Toast.LENGTH_LONG).show();
        }

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UBoosterRef = rootRef.child(UID);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("UserName").exists()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    String email = user.getEmail();

                     if (email!=null){
                         DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                         final String name   = email.substring(0, email.lastIndexOf("@"));
                         if (name.contains(".")){
                             String m = name.replace(".","");
                             rootRef.child("UserName").child(m).setValue(firebaseAuth.getCurrentUser().getUid());
                             rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(m);
                         }else {
                             rootRef.child("UserName").child(name).setValue(firebaseAuth.getCurrentUser().getUid());
                             rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(name);
                         }

                     }






                }else{
                    String username = dataSnapshot.child("UserName").getValue(String.class);
                    assert username != null;
                    myId.setText(username);
                    userN = username;
                }

                if (!dataSnapshot.child("DREF").exists()){
                    cardView.setVisibility(View.VISIBLE);


                }else {
                    cardView.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        UBoosterRef.addValueEventListener(eventListener);

    }


    boolean thatThingHappened = false;
    private void GiveReward(){
        if (!thatThingHappened){
            thatThingHappened = true;
            incrementRefcoins(20);
            incrementCoins(20);
            incrementExchange(1);
            Toast.makeText(RefActivity.this,"you have got 20 coins",Toast.LENGTH_LONG).show();
        }


    }


}
