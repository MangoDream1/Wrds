package nl.mprog.axel.wrds_programmeerproject.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.mprog.axel.wrds_programmeerproject.Activities.ListActivity;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseHelper;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 12-1-17.
 *
 * Adapter that fills ListView in ListActivity with words
 */

public class WordsCursorAdapter extends ResourceCursorAdapter {

    private TextView index;
    private TextView wordA;
    private TextView wordB;

    public WordsCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.PK_LIST_ID));
        ArrayList<Long> selectedItemsList = ((ListActivity) context).getSelectedItemsList();

        findView(v);
        setViews(cursor);

        // If id in selectedItemsList than is selected thus color gray
        if (selectedItemsList.contains(id)) {
            v.setBackgroundColor(Color.LTGRAY);
        } else {
            v.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * Find the view
     * @param v view
     */
    private void findView(View v) {
        index = (TextView) v.findViewById(R.id.index);
        wordA = (TextView) v.findViewById(R.id.wordA);
        wordB = (TextView) v.findViewById(R.id.wordB);

    }

    /**
     * Set the views
     * @param cursor cursor
     */
    private void setViews(Cursor cursor) {
        index.setText(String.valueOf(cursor.getPosition() + 1));
        wordA.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_A)));
        wordB.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_B)));

    }
}
