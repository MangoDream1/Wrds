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

import nl.mprog.axel.wrds_programmeerproject.Activities.MainActivity;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseHelper;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 11-1-17.
 */

public class CMListDialog extends DialogFragment {

    private DatabaseManager dbm;
    private Boolean isModify = false;
    private long listId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbm = DatabaseManager.getInstance();

        final Activity activity = getActivity();

        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.cm_list_dialog, null);
        builder.setView(view);


        final EditText nameEditText = (EditText) view.findViewById(R.id.list_name_editText);
        final EditText descEditText = (EditText) view.findViewById(R.id.list_description_editText);
        final EditText lanAEditText = (EditText) view.findViewById(R.id.list_languageA_editText);
        final EditText lanBEditText = (EditText) view.findViewById(R.id.list_languageB_editText);

        final Bundle arguments = getArguments();

        String message = "Create list";
        String positiveButtonString = "Create";

        if (arguments != null) {
            message = "Modify list";
            positiveButtonString = "Modify";
            isModify = true;

            listId = arguments.getLong("id");
            Cursor cursor = dbm.getSingleList(listId);

            nameEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_TITLE)));
            descEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_DESC)));
            lanAEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_A)));
            lanBEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_B)));
        }

        builder.setMessage(message)
                .setPositiveButton(positiveButtonString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString();
                        String desc = descEditText.getText().toString();
                        String lanA = lanAEditText.getText().toString();
                        String lanB = lanBEditText.getText().toString();

                        if (isModify) {
                            dbm.updateList(listId, name, desc, "you", lanA, lanB);
                        } else {
                            dbm.insertList(name, desc, "you", lanA, lanB);
                        }

                        ((MainActivity) activity).dataChange();

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
