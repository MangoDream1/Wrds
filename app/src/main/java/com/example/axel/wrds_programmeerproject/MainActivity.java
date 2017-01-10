package com.example.axel.wrds_programmeerproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_menu);
        setSupportActionBar(myToolbar);

    }
}
