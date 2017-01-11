package com.example.axel.wrds_programmeerproject;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            DatabaseManager.getInstance().open(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Creates toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_menu);
        setSupportActionBar(myToolbar);

        Button add_button = (Button) findViewById(R.id.add_button);
        add_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                CMDialog cmDialog = new CMDialog();
                cmDialog.show(getFragmentManager(), "CMDialog");
        }
    }
}

