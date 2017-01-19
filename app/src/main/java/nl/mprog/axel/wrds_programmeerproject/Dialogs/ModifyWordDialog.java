package nl.mprog.axel.wrds_programmeerproject.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import nl.mprog.axel.wrds_programmeerproject.Activities.ListActivity;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseHelper;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 17-1-17.
 */

public class ModifyWordDialog extends DialogFragment {

    private DatabaseManager dbm;
    private long wordId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();


        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.modify_word_dialog, null);

        dbm = DatabaseManager.getInstance();

        builder.setView(view);

        final EditText wordAEditText = (EditText) view.findViewById(R.id.wordA_editText);
        final EditText wordBEditText = (EditText) view.findViewById(R.id.wordB_editText);

        Bundle arguments = getArguments();
        wordId = arguments.getLong("id");
        Cursor cursor = dbm.getSingleWord(wordId);

        wordAEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_A)));
        wordBEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_B)));

        builder.setMessage("Modify word")
                .setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String wordA = wordAEditText.getText().toString();
                        String wordB = wordBEditText.getText().toString();

                        dbm.updateWord(wordId, wordA, wordB);

                        ((ListActivity) activity).dataChange();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog
                        // Empty since this does nothing except close
                    }
                });

        return builder.create();
    }
}
