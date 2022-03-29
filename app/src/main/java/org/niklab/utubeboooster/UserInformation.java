package org.niklab.utubeboooster;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.Purchase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.revenuecat.purchases.Offerings;
import com.revenuecat.purchases.Package;
import com.revenuecat.purchases.PurchaserInfo;
import com.revenuecat.purchases.Purchases;
import com.revenuecat.purchases.PurchasesError;
import com.revenuecat.purchases.interfaces.Callback;
import com.revenuecat.purchases.interfaces.MakePurchaseListener;
import com.revenuecat.purchases.interfaces.ReceiveOfferingsListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UserInformation extends AppCompatActivity {

    private String Uid;
    private FirebaseAuth firebaseAuth;
    private TextView Coins;
    private EditText Email;
    Button Show,Bs,Bg,Bd;
    DatabaseReference coinsRef;
    RelativeLayout relativeLayout;
    private Button Fb;
    private Package Silver, Gold,Diamond;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Uid = firebaseUser.getUid();
        Coins = findViewById(R.id.coins);
        Email = findViewById(R.id.editEmail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Show = findViewById(R.id.Plus);
        Bg = findViewById(R.id.BGold);
        Bs = findViewById(R.id.BSilver);
        Bd = findViewById(R.id.BDiamond);
        Fb = findViewById(R.id.takeFb);
        relativeLayout = findViewById(R.id.R);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("User Information");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(Uid).child("Coins");

        Purchases.setDebugLogsEnabled(true);
        Purchases.configure(this, "zNnkmkghUhMajAcDgyVqxKdzLbuTkwZM");

        dialog = new ProgressDialog(this);
        Purchases.isBillingSupported(this, new Callback<Boolean>() {
            @Override
            public void onReceived(Boolean isSupported) {
                if (!isSupported) {
                    Toast.makeText(UserInformation.this,"Please Contact Our Facebook Page For quick Support",Toast.LENGTH_LONG).show();
                }
            }
        });


        dialog.setMessage("Loading Please wait....");
        dialog.setCancelable(false);
        dialog.show();

        Purchases.getSharedInstance().getOfferings(new ReceiveOfferingsListener() {
            @Override
            public void onReceived(@NonNull Offerings offerings) {
                if (offerings.getCurrent() != null) {
                    List<Package> availablePackages = offerings.getCurrent().getAvailablePackages();
                    Silver = availablePackages.get(0);
                    Gold = availablePackages.get(1);
                    Diamond = availablePackages.get(2);

                    dialog.cancel();


                }
            }

            @Override
            public void onError(@NonNull PurchasesError error) {

            }
        });











        Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             relativeLayout.setVisibility(View.VISIBLE);


            }

        });


        Bs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Purchases.getSharedInstance().purchasePackage(
                        UserInformation.this,
                        Silver,
                        new MakePurchaseListener() {
                            @Override
                            public void onCompleted(@NonNull Purchase purchase, @NonNull PurchaserInfo purchaserInfo) {


                                if (purchase.getPurchaseToken()!=null&&purchase.getPurchaseState()== Purchase.PurchaseState.PURCHASED){
                                    incrementCoins(2000);
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    rootRef.child("Purchase").push().child(Uid).setValue(2000);
                                    rootRef.child("Purchase").push().child(Uid).setValue(purchase.getOrderId());

                                }

                            }

                            @Override
                            public void onError(@NonNull PurchasesError error, boolean userCancelled) {

                            }


                        }
                );




            }
        });



        Bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Purchases.getSharedInstance().purchasePackage(
                        UserInformation.this,
                        Gold,
                        new MakePurchaseListener() {
                            @Override
                            public void onCompleted(@NonNull Purchase purchase, @NonNull PurchaserInfo purchaserInfo) {





                                if (purchase.getPurchaseToken()!=null&&purchase.getPurchaseState()== Purchase.PurchaseState.PURCHASED){
                                    incrementCoins(6000);
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    rootRef.child("Purchase").push().child(Uid).setValue(6000);
                                }




                            }

                            @Override
                            public void onError(@NonNull PurchasesError error, boolean userCancelled) {

                            }


                        }
                );
            }
        });



        Bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Purchases.getSharedInstance().purchasePackage(
                        UserInformation.this,
                        Diamond,
                        new MakePurchaseListener() {
                            @Override
                            public void onCompleted(@NonNull Purchase purchase, @NonNull PurchaserInfo purchaserInfo) {


                                if (purchase.getPurchaseToken()!=null&&purchase.getPurchaseState()== Purchase.PurchaseState.PURCHASED){
                                    incrementCoins(15000);
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    rootRef.child("Purchase").push().child(Uid).setValue(15000);
                                }



                            }

                            @Override
                            public void onError(@NonNull PurchasesError error, boolean userCancelled) {

                            }


                        }
                );

            }
        });

        Fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserInformation.this,"Send message to our facebook page",Toast.LENGTH_LONG).show();
              launchFacebook();
            }
        });









    }









    public final void launchFacebook() {
        final String urlFb = "fb://page/"+"100940624608174";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlFb));

        // If a Facebook app is installed, use it. Otherwise, launch
        // a browser
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            final String urlBrowser = "https://www.facebook.com/pages/"+"100940624608174";
            intent.setData(Uri.parse(urlBrowser));
        }

        startActivity(intent);
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
    protected void onStart() {
        super.onStart();
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UBoosterRef = rootRef.child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Coins").exists()){
                    Long C = dataSnapshot.child("Coins").getValue(Long.class);
                    Coins.setText(String.valueOf(C));

                    String email = dataSnapshot.child("email").getValue(String.class);
                    Email.setText(email);


                }else {
                    rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Coins").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        };
        UBoosterRef.addValueEventListener(eventListener);


    }












}











