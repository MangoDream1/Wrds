package nl.mprog.axel.wrds_programmeerproject.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.mprog.axel.wrds_programmeerproject.Activities.MainActivity;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseHelper;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 11-01-17.
 *
 * Adapter that fills ListView in MainActivity with information of a list.
 *
 */

public class WordListsCursorAdapter extends ResourceCursorAdapter {

    private TextView title;
    private TextView nWords ;
    private TextView desc;
    private TextView date;
    private TextView creator;
    private TextView language;

    public WordListsCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.PK_LIST_ID));
        ArrayList<Long> selectedItemsList = ((MainActivity) context).getSelectedItemsList();

        findViews(v);
        setView(cursor, id);

        // If id in selectedItemsList than is selected thus color gray
        if (selectedItemsList.contains(id)) {
            v.setBackgroundColor(Color.LTGRAY);
        } else {
            v.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * Find the views
     * @param v view
     */
    private void findViews(View v) {
        // Finds views
        title = (TextView) v.findViewById(R.id.title_textView);
        nWords = (TextView) v.findViewById(R.id.nWords_textView);
        desc = (TextView) v.findViewById(R.id.desc_textView);

        date = (TextView) v.findViewById(R.id.date_textView);
        creator = (TextView) v.findViewById(R.id.creator_textView);
        language = (TextView) v.findViewById(R.id.language_textView);
    }

    /**
     * Set the data in the view
     * @param cursor database cursor
     * @param id list id
     */
    private void setView(Cursor cursor, long id) {
        // Set views
        title.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_TITLE)));
        desc.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_DESC)));

        date.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DT_CREATED_AT)));
        creator.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_CREATOR)));

        // Add languages into one string
        String languageText = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_A))
                + " - " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_B));

        language.setText(languageText);

        // Count nWords of the list
        nWords.setText(" (" + String.valueOf(DatabaseManager.getInstance()
                .countListWords(id)) + ")");

    }
}
