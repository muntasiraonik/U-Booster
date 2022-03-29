package org.niklab.utubeboooster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        SharedPreferences prefs = getSharedPreferences("U", MODE_PRIVATE);
        int id = prefs.getInt("idName", 0);
        if(id==2)
        {
            LoadHome();
        }
        else
        {
            setContentView(R.layout.activity_welcome);

            ImageView imageView = findViewById(R.id.imageView5);
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadeout);
            imageView.startAnimation(aniFade);
            TextView textView = findViewById(R.id.textView9);

            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                textView.setText(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                    startActivity(new Intent(WelcomeActivity.this, LRActivity.class));


                }
            }, 5000);




        }


    }


    private void LoadHome(){
        startActivity(new Intent(this,LRActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("U", MODE_PRIVATE).edit();
        editor.putInt("idName", 2);
        editor.apply();
    }
}
