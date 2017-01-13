package nl.mprog.axel.wrds_programmeerproject;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class ExamActivity extends AppCompatActivity {

    private String wordA;
    private String wordB;

    private long listId;
    private DatabaseManager dbm;
    private ArrayList<String[]> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        dbm = DatabaseManager.getInstance();

        Intent intent = getIntent();
        listId = intent.getLongExtra("id", 0L);

//        Cursor listCursor = dbm.getUserListTitle(listId);
//        String title = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_title));
//        String lanA  = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_languageA));
//        String lanB  = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.str_languageB));
//
//        title = String.format("%s (%s - %s)", title, lanA, lanB);
//
//        // Creates toolbar and sets title
//        Toolbar toolbar = (Toolbar) findViewById(R.id.main_menu);
//        toolbar.setTitle(title);
//        setSupportActionBar(toolbar);

        Cursor cursor = dbm.getListWords(listId);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            dataList.add(new String[]{
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordA)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordB))
            });
        }

        findRandomWord();
    }

    private void findRandomWord() {
        String[] random = dataList.get(new Random().nextInt(dataList.size()));
        wordA = random[0];
        wordB = random[1];

        ((TextView) this.findViewById(R.id.wordA)).setText(wordA);

    }
}
