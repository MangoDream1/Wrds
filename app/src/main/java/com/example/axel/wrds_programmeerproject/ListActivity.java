package com.example.axel.wrds_programmeerproject;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

public class ListActivity extends AppCompatActivity {

    private long listId;
    private DatabaseManager dbm;
    private WordsCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        listId = intent.getLongExtra("id", 0L);

        dbm = DatabaseManager.getInstance();

        Cursor listCursor = dbm.getUserListTitle(listId);
        String title = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_title));
        String lanA = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_languageA));
        String lanB = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_languageB));

        title = String.format("%s (%s - %s)", title, lanA, lanB);

        // Creates toolbar and sets title
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_menu);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        dbm.insertWord(listId, "test", "TEST");
        Cursor cursor = dbm.getListWords(listId);

        adapter = new WordsCursorAdapter(this, R.layout.word_item, cursor, 0);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);


    }
}
