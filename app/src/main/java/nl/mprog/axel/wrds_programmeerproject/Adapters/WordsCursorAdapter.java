package nl.mprog.axel.wrds_programmeerproject.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

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
        TextView index = (TextView) v.findViewById(R.id.index);
        TextView wordA = (TextView) v.findViewById(R.id.wordA);
        TextView wordB = (TextView) v.findViewById(R.id.wordB);

        index.setText(String.valueOf(cursor.getPosition() + 1));
        wordA.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordA)));
        wordB.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.str_wordB)));
    }
}
