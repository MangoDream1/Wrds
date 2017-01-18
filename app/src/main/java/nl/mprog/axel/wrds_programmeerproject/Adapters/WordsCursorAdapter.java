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
 */

public class WordsCursorAdapter extends ResourceCursorAdapter {

    public WordsCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    @Override
    public void bindView(View v, Context context, Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.pk_listId));
        ArrayList<Long> selectedItemsList = ((ListActivity) context).getSelectedItemsList();

        TextView index = (TextView) v.findViewById(R.id.index);
        TextView wordA = (TextView) v.findViewById(R.id.wordA);
        TextView wordB = (TextView) v.findViewById(R.id.wordB);

        index.setText(String.valueOf(cursor.getPosition() + 1));
        wordA.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordA)));
        wordB.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordB)));

        if (selectedItemsList.contains(id)) {
            v.setBackgroundColor(Color.LTGRAY);
        } else {
            v.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
