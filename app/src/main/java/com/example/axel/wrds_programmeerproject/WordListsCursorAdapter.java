package com.example.axel.wrds_programmeerproject;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

/**
 * Created by axel on 11-01-17.
 */

public class WordListsCursorAdapter extends ResourceCursorAdapter {

    public WordListsCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        // Finds views
        TextView title = (TextView) v.findViewById(R.id.title_textView);
        TextView nWords = (TextView) v.findViewById(R.id.nWords_textView);
        TextView desc = (TextView) v.findViewById(R.id.desc_textView);

        TextView date = (TextView) v.findViewById(R.id.date_textView);
        TextView creator = (TextView) v.findViewById(R.id.creator_textView);
        TextView language = (TextView) v.findViewById(R.id.language_textView);

        // Set views
        title.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_title)));
//        title.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_title)));
        desc.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_desc)));

//        title.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_title)));
        creator.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_creator)));

        String languageText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_languageA))
                + " - " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_languageB));

        language.setText(languageText);

    }
}
