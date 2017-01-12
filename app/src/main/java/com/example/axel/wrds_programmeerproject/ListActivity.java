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

        Cursor listCursor = dbm.getUserListTitle(listId);
        String title = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_title));
        String lanA = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_languageA));
        String lanB = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_languageB));

        title = String.format("%s (%s - %s)", title, lanA, lanB);

        setTitle(title);

    }
}
