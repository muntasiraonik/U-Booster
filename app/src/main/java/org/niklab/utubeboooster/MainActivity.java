package org.niklab.utubeboooster;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.instacart.library.truetime.TrueTime;
import com.instacart.library.truetime.TrueTimeRx;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView email, points,LV;
    private Button Icon;
    private String Uid;
    DatabaseReference coinsRef,Rewarded;
    FirebaseAuth firebaseAuth;

    int dia;
    int Month;
    int TotalDate;
    private int appVersion;
    private String verSionQ;
    private int RE;
    private com.facebook.ads.AdView adfbView;






    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AudienceNetworkAds.initialize(this);
        adfbView = new com.facebook.ads.AdView(MainActivity.this, "2580157178767459_2580165415433302", AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);
        adContainer.addView(adfbView);
        adfbView.loadAd();





        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isNetworkConnectionAvailable();
        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .setShowLaterButton(true)
                .setDebug(false)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);




        drawerLayout = findViewById(R.id.drawer_layout1);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        NavigationView navigationView = findViewById(R.id.navigation_view1);

        View hView = navigationView.getHeaderView(0);
        email = hView.findViewById(R.id.email);
        points = hView.findViewById(R.id.points);
        Icon = hView.findViewById(R.id.EIcon);
        Button edit = hView.findViewById(R.id.edit);
        Button logout = hView.findViewById(R.id.logout);
        navigationView.setNavigationItemSelectedListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        LV = findViewById(R.id.LiveMsg);


        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser==null){
            finish();
            startActivity(new Intent(MainActivity.this,LRActivity.class));
        }



            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            Uid = firebaseUser.getUid();




        if(isNetworkAvailable()){new GoogleTime().execute();}else
        {
            Toast.makeText(this, "Sorry no Internet", Toast.LENGTH_SHORT).show();
        }






        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        coinsRef = rootRef.child(Uid).child("Coins");
        Rewarded = rootRef.child(Uid).child("Rewarded");









        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Views"));
        tabLayout.addTab(tabLayout.newTab().setText("Subs"));
        tabLayout.addTab(tabLayout.newTab().setText("Likes"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        actionBarDrawerToggle.syncState();
        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,UserInformation.class));

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this,LRActivity.class));
            }
        });










    }






    @Override
    protected void onDestroy() {
        if (adfbView != null) {
            adfbView.destroy();
        }


        super.onDestroy();
    }


    @Keep
    @SuppressLint("StaticFieldLeak")
    class GoogleTime extends AsyncTask<Void,Void,Void> {
        @SuppressLint("CheckResult")
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                TrueTime.build().initialize();

                TrueTimeRx.build()
                        .initializeRx("time.google.com")
                        .subscribeOn(Schedulers.io())
                        .subscribe(date -> {
                           java.util.Date da = TrueTimeRx.now();
                            Log.w("PPPPP", "TrueTime was initialized and we have a time: " + da);

                                    String mm= String.valueOf(da);


                                    String []strArray=mm.split(" ");

                                    Month = getMonthInIntFormat(strArray[1]);
                                    String dd = strArray[2];
                                    String yy = strArray[5];


                                    String  Datw = dd +""+Month+""+yy;


                                    TotalDate =Integer.parseInt(Datw);

                            Log.w("PPPPP", "TrueTime was initialized and we have a time: " + TotalDate);


                        }

                        , Throwable::printStackTrace);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


    }

    private int getMonthInIntFormat(String s) {
        int num = 0;
        switch (s){

            case "Jan":
                num = 1 ;
                break;
            case "Feb":
                num = 2;
                break;
            case "Mar":
                num = 3;
                break;
            case "Apr":
                num = 4;
                break;
            case "May":
                num = 5;
                break;
            case "Jun":
                num = 6;
                break;
            case "Jul":
                num = 7;
                break;
            case "Aug":
                num = 8;
                break;
            case "Sep":
                num = 9;
                break;
            case "Oct":
                num = 10;
                break;
            case "Nov":
                num = 11;
                break;
            case "Dec":
                num = 12;
                break;

        }
        return num;
    }









    private void Update(){
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionCode;
            verSionQ = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UBoosterRef = rootRef.child("AppU");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Long id = dataSnapshot.child("AppV").getValue(Long.class);
                assert id != null;
                int x = id.intValue( );
                if (x != appVersion){
                    dia =1;
                    UpdateAlert();

                }

                String idv = dataSnapshot.child("AppQ").getValue(String.class);

                assert idv != null;
                if (!idv.equals(verSionQ)){
                    dia =1;
                    UpdateAlert();
                }

                Long M = dataSnapshot.child("M").getValue(Long.class);
                assert M != null;
                int x1 = M.intValue();
                if (x1 ==2) {
                    dia =1;
                    MAlert();
                }

                String Lim = dataSnapshot.child("LM").getValue(String.class);
                LV.setText(Lim);



            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        UBoosterRef.addValueEventListener(eventListener);
    }

    public void UpdateAlert() {
        new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme)

                .setCancelable(false)
                .setTitle("Update Available")
                .setMessage("Please Update App to New Version")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName()));
                        localIntent.addFlags(1208483840);
                        try
                        {
                            startActivity(localIntent);
                        }
                        catch (Exception localException)
                        {
                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    public void MAlert() {
        new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme)
                .setCancelable(false)
                .setTitle("Maintenance break")
                .setMessage("Please ComeBack After 30 Minutes .")

                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    void rewarded(){
        Rewarded.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                if (mutableData.getValue() == null) {
                    mutableData.setValue((long) 1);
                } else {
                    Long points = mutableData.getValue(Long.class);
                    assert points != null;
                    int x = points.intValue();
                    Long incrementedPoints = x + (long) 1;
                    mutableData.setValue(incrementedPoints);

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
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LRActivity.class));
        }
        return super.onOptionsItemSelected(item);


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



    @SuppressLint("WrongConstant")
    public void rateTheApp()
    {
        Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + getPackageName()));
        localIntent.addFlags(1208483840);
        try
        {
            startActivity(localIntent);
        }
        catch (Exception localException)
        {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }


    public void shareTheApp(){
        try
        {
            Intent localIntent = new Intent("android.intent.action.SEND");
            localIntent.setType("text/plain");
            localIntent.putExtra("android.intent.extra.SUBJECT", "Your Laywer");
            localIntent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + getPackageName() + " \n\n");
            startActivity(Intent.createChooser(localIntent, "Share with.."));
        }
        catch (Exception ignored) {}
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.fb_id) {
            finish();
            launchFacebook();



        }else if (id==R.id.mchannel)  {

            startActivity(new Intent(getApplicationContext(),MyChannelsActivity.class));



        } else if (id==R.id.how){
            startActivity(new Intent(getApplicationContext(),HowTOWorkActivity.class));

        }


        else if (id==R.id.Refer){
            startActivity(new Intent(getApplicationContext(),RefActivity.class));

        } else if (id == R.id.share_id) {
           shareTheApp();


        } else if (id == R.id.rate_id) {
            rateTheApp();



        } else if (id == R.id.logout_id) {
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        drawer.closeDrawer(GravityCompat.START);
        return false;

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout1);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    private void saveButtonClickTime() {
        if (TotalDate!=0){
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child(Uid).child("Date").child("time1").setValue(TotalDate);
        }else {
            Toast.makeText(MainActivity.this,"Please Restart the app",Toast.LENGTH_LONG).show();

        }

    }





    public void isTimeToReset() {
        if (TotalDate!=0){
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child(Uid).child("Date").child("time2").setValue(TotalDate);
            Do();

        }


    }




    public void Do(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UBoosterRef = rootRef.child(Uid).child("Date");
        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("time1").exists()&&dataSnapshot.child("time2").exists()) {
                    Long time1 = dataSnapshot.child("time1").getValue(Long.class);
                    assert time1 != null;
                    int x1 = time1.intValue();

                    Long time2 = dataSnapshot.child("time2").getValue(Long.class);
                    assert time2 != null;
                    int x2 = time2.intValue();

                    if (x1 != x2&&x2!=0){
                        Reset();
                        GiveReward();
                    }




                }


                if (!dataSnapshot.child("time1").exists()){
                    rootRef.child(Uid).child("Date").child("time1").setValue(1);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        UBoosterRef.addValueEventListener(eventListener);
    }


    private void Reset(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child(Uid).child("Rewarded").setValue(0);
        saveButtonClickTime();
    }

    boolean thatThingHappened = false;
    private void GiveReward(){
        if (!thatThingHappened){
            thatThingHappened = true;
            incrementCoins(10);
            Toast.makeText(MainActivity.this,"you have got 10 coins for daily login",Toast.LENGTH_LONG).show();

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
                    Update();
                    isTimeToReset();
                    Long C = dataSnapshot.child("Coins").getValue(Long.class);
                    points.setText(String.valueOf(C));

                }

                if (dataSnapshot.child("email").exists()){
                    String e = dataSnapshot.child("email").getValue(String.class);
                    email.setText(e);
                    assert e != null;
                    String s =e.substring(0,1);
                    Icon.setText(s);
                }else {

                    rootRef.child(Uid).child("email").setValue(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

                }





                    if (dataSnapshot.child("Rewarded").exists()){
                        Long rewarded = dataSnapshot.child("Rewarded").getValue(Long.class);
                        assert rewarded != null;
                        RE= rewarded.intValue();

                    }else{
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        rootRef.child(Uid).child("Rewarded").setValue(0);
                    }


                    if (dataSnapshot.child("Disabled").exists()){
                        Long d = dataSnapshot.child("Disabled").getValue(Long.class);
                        assert d != null;
                        int x = d.intValue();
                        if (x==2){
                            ShowDisableNotice();
                        }
                    }









            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        };
        UBoosterRef.addValueEventListener(eventListener);

    }

    private void ShowDisableNotice(){
        new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setTitle("Warning")
                .setMessage("Your account has been disabled because our system detected some unusual activities in your account. Please Contact our facebook page.")
                .setNeutralButton("Copy Userid", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("simple text", Uid);
                        assert clipboard != null;
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "ID Copied to Clipboard",
                                Toast.LENGTH_LONG).show();


                        finish();
                        launchFacebook();


                    }
                })
                .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        finish();

                    }
                })
                .show();
    }






    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
            return false;
        }
    }





}
