package com.example.axel.wrds_programmeerproject;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ListActivity extends AppCompatActivity {

    private long listId;
    private DatabaseManager dbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        listId = intent.getLongExtra("id", 0L);

        DatabaseManager.getInstance();

        Cursor titleCursor = dbm.getUserListTitle(listId);
        String title = titleCursor.getString(titleCursor.getColumnIndex(DatabaseHelper.str_title));


    }
}
