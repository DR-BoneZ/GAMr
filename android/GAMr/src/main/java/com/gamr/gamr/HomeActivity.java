package com.gamr.gamr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by beni on 1/9/15.
 */
public class HomeActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView responseTextView = (TextView) findViewById(R.id.response_text);
        responseTextView.setText(getIntent().getStringExtra("ResponseJson"));
    }
}