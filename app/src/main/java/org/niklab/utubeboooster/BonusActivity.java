package org.niklab.utubeboooster;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BonusActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView Money,Ex;
    private EditText mobile;
    private EditText amount;
    private Button submit;
    private long CMoney;

    private String Uid;
    private String method = "Bkash";
    DatabaseReference withdrawRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus);
        Money = (TextView) findViewById(R.id.M);
        Ex = (TextView) findViewById(R.id.eT);
        mobile = (EditText) findViewById(R.id.mobile);
        amount = (EditText) findViewById(R.id.amount);
        submit = (Button) findViewById(R.id.submit);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String uid = firebaseUser.getUid();
        Uid = uid;

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        withdrawRef = rootRef.child(uid).child("TWithdraw");

        submit.setOnClickListener(this);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

                switch (i) {
                    case R.id.flexiload:
                        method = "Flexiload";
                        break;
                    case R.id.bkash:
                        method = "Bkash";
                        break;
                    case R.id.rocket:
                        method = "Rocket";

                }

            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bonus");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ezzeearnRef = rootRef.child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Exchange").exists()){
                    Long money = dataSnapshot.child("Exchange").getValue(Long.class);
                    Ex.setText(String.valueOf(money));
                    assert money != null;
                    int x = money.intValue();
                    double y = x*0.1;

                    Money.setText(String.valueOf((Double.valueOf(y)).longValue()));
                }





            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ezzeearnRef.addValueEventListener(eventListener);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);

    }
    private void showAlert(String paramString) {
        new AlertDialog.Builder(this).setTitle("Message").setMessage(paramString).setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                paramAnonymousDialogInterface.dismiss();


            }
        }).setIcon(R.drawable.alert_circle_outline).show();
    }

    private void CheckMoney() {
        String Mobile = mobile.getText().toString().trim();
        String Amount = amount.getText().toString().trim();


        if (TextUtils.isEmpty(Mobile)) {
            Toast.makeText(this, "Please enter Mobile", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(Amount)) {
            Toast.makeText(this, "Please enter Amount", Toast.LENGTH_LONG).show();
            return;
        }


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ezzeearnRef = rootRef.child(Uid);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long money = dataSnapshot.child("Exchange").getValue(Long.class);
                assert money != null;
                int x = money.intValue();
                double y = x*0.1;
                CMoney = (Double.valueOf(y)).longValue();
                if (CMoney < 101) {
                    showAlert("You Have To Earn " + (101 - CMoney) + " Tk To Withdraw");
                } else if (CMoney >= 101) {
                    Submit();

                } else {
                    showAlert("Amount is greater than your balance");
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ezzeearnRef.addListenerForSingleValueEvent(eventListener);

    }




    private void Submit() {


        String Mobile = mobile.getText().toString().trim();
        String Amount = amount.getText().toString().trim();
        int ammount;
        ammount = Integer.parseInt(Amount);


        if (Integer.valueOf(amount.getText().toString()) < 101) {
            showAlert("Amount is Less Than 100");


        } else if (Integer.valueOf(amount.getText().toString()) >= 101 && ammount <= CMoney) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            rootRef.child("Withdraws").child(Uid).child("Method").setValue(method);
            rootRef.child("Withdraws").child(Uid).child("Mobile").setValue(Mobile);
            rootRef.child("Withdraws").child(Uid).child("Amount").setValue(ammount - 1);
            showAlerts();
            ammount = Integer.parseInt(Amount);
            double x = (CMoney-ammount)/0.1;

            long m = (Double.valueOf(x)).longValue();


            rootRef.child(Uid).child("Exchange").setValue(m);






        } else {
            showAlert("Entered Amount Is Greater Than Main Balance");
        }


    }

    public void showAlerts() {
        new AlertDialog.Builder(BonusActivity.this)
                .setCancelable(false)
                .setTitle("Message")
                .setMessage("Congratulations Your Money will send Soon")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })

                .show();
    }

    @Override
    public void onClick(View v) {
        if (v == submit) {
            CheckMoney();
        }
    }
}
