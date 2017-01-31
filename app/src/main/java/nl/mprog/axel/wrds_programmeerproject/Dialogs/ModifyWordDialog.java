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
 *
 * Dialog for modification of a word
 */

public class ModifyWordDialog extends DialogFragment {

    private DatabaseManager dbm;
    private long wordId;

    private Activity activity;

    private EditText wordAEditText;
    private EditText wordBEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = getActivity();

        dbm = DatabaseManager.getInstance();

        View view = createView();
        setText(view);

        return createBuilder(view).create();
    }

    /**
     * Create view
     * @return  view
     */
    private View createView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        return inflater.inflate(R.layout.modify_word_dialog, null);
    }

    /**
     * Set text in edit text from database
     * @param view  view
     */
    private void setText(View view) {
        wordAEditText = (EditText) view.findViewById(R.id.wordA_editText);
        wordBEditText = (EditText) view.findViewById(R.id.wordB_editText);

        Bundle arguments = getArguments();
        wordId = arguments.getLong("id");
        Cursor cursor = dbm.getSingleWord(wordId);

        wordAEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_A)));
        wordBEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_WORD_B)));

    }

    /**
     * Create builder
     * @param view  view
     * @return      created builder
     */
    private AlertDialog.Builder createBuilder(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setView(view);
        builder.setTitle(R.string.dialog_modify_word_title);
        builder = setPositiveButton(builder);
        builder = setNegativeButton(builder);

        return builder;
    }

    /**
     * setPositiveButton to builder
     * @param builder   builder without positive button
     * @return          builder with positive button
     */
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.button_modify, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String wordA = wordAEditText.getText().toString();
                String wordB = wordBEditText.getText().toString();

                dbm.updateWord(wordId, wordA, wordB);

                ((ListActivity) activity).dataChange();
            }
        });

        return builder;
    }

    /**
     * setNegativeButton to builder
     * @param builder   builder without negative button
     * @return          builder with negative button
     */
    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
        builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((ListActivity) activity).dataChange();
            }
        });

        return builder;
    }
}
