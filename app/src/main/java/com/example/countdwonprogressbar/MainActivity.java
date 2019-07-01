package com.example.countdwonprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private CountDownProgressBar cpb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cpb = findViewById(R.id.cpb);
    }

    @Override
    protected void onResume() {
        cpb.countDownResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        cpb.countDownCancel();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cpb.countDownCancel();
        super.onDestroy();
        System.gc();
    }
}
