package org.niklab.utubeboooster;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class InsertSubActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText Link,Views,Cost;
    private Button submit;
    private int views1,cost1;
    private String Uid;
    private FirebaseAuth firebaseAuth;
    private Long C;
    String L,T,ImageLink;
    private ProgressDialog progressDialog;
    DatabaseReference coinsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_sub);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Link = findViewById(R.id.editText3);
        Views = findViewById(R.id.editText4);
        Cost = findViewById(R.id.editText5);
        submit = findViewById(R.id.button);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Uid = firebaseUser.getUid();
        submit.setOnClickListener(this);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Create Subscribe Exchange");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(Uid).child("Coins");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View v) {
        if (v==submit){
            Submit();
        }
    }


    void incrementCoins(final long value) {
        coinsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull final MutableData currentData) {
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



    public void Submit(){
        final String link = Link.getText().toString().trim();
        final String views = Views.getText().toString().trim();
        final String cost = Cost.getText().toString().trim();






        if (TextUtils.isEmpty(link)) {
            Toast.makeText(this, "Please enter video link", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(views)) {
            Toast.makeText(this, "Please enter video views", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(cost)) {
            Toast.makeText(this, "Please enter views cost", Toast.LENGTH_LONG).show();
            return;
        }
        int x = Integer.parseInt(views);
        int y = Integer.parseInt(cost);

        if (x<9){
            Toast.makeText(this, "Minimum views must be 10", Toast.LENGTH_LONG).show();
        }

        if (y<4){
            Toast.makeText(this, "Minimum cost must be 5", Toast.LENGTH_LONG).show();
        }
        if (x>=10&&y>=5) {

            progressDialog.setMessage("Please Wait......");
            progressDialog.show();


            L = link;


            Pattern pattern = Pattern.compile(
                    "^(http(s)?:\\/\\/)?((w){3}.)?youtube.com\\/?channel?\\/?.+",
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(link);
            if (matcher.matches()) {
                String v = L.substring(L.indexOf("l/") + 2);


                StringRequest stringRequest = new StringRequest(
                        Request.Method.GET,
                        "https://www.googleapis.com/youtube/v3/channels?part=snippet%2CcontentDetails%2Cstatistics&id=" + v + "&key=" + getString(R.string.service_link),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {

                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                                    JSONObject object = jsonArray.getJSONObject(0);
                                    JSONObject snippet = object.getJSONObject("snippet");

                                    T = snippet.getString("title");


                                    JSONArray jsonArray1 = jsonObject.getJSONArray("items");

                                    JSONObject object1 = jsonArray1.getJSONObject(0);
                                    JSONObject snippet1 = object1.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default");
                                    ImageLink = snippet1.getString("url");


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Toast.makeText(InsertSubActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                };


                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);


                String mlink = "https://www.youtube.com/channel";

                final String dlinkC = link.substring(0, link.lastIndexOf("/"));

                views1 = Integer.parseInt(views);
                cost1 = Integer.parseInt(cost);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dlinkC.equals(mlink) && T != null && ImageLink != null) {
                            Do();


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(InsertSubActivity.this, "Invalid Link", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 3000);


            } else {
                progressDialog.dismiss();
                Toast.makeText(InsertSubActivity.this, "Invalid Link", Toast.LENGTH_SHORT).show();
            }

        }




    }



    public void Do(){
        if (C>cost1&&views1*cost1<C){




            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("SubsList").child(Uid).child("link").setValue(L);
            rootRef.child("SubsList").child(Uid).child("name").setValue(T);
            rootRef.child("SubsList").child(Uid).child("cost").setValue(cost1);
            rootRef.child("SubsList").child(Uid).child("views").setValue(views1);
            rootRef.child("SubsList").child(Uid).child("image").setValue(ImageLink);
            rootRef.child("SubsList").child(Uid).child("viewsDone").setValue(0);
            progressDialog.dismiss();
            incrementCoins(-cost1*views1);
            finish();
            Toast.makeText(this, "Successfully Added", Toast.LENGTH_LONG).show();


        }else {
            progressDialog.dismiss();
            Toast.makeText(this, "You don't have enough points", Toast.LENGTH_LONG).show();
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

                if (dataSnapshot.child("Coins").exists()){
                    CheckalreadyaddedViews();
                    C = dataSnapshot.child("Coins").getValue(Long.class);
                }else {
                    rootRef.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("Coins").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        };
        UBoosterRef.addValueEventListener(eventListener);


    }


    public void CheckalreadyaddedViews(){
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UBoosterRef = rootRef.child("SubsList");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(Uid).exists()){
                    submit.setVisibility(View.INVISIBLE);
                    Toast.makeText(InsertSubActivity.this, "You Already Have a running campaign", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        };
        UBoosterRef.addListenerForSingleValueEvent(eventListener);
    }
}
