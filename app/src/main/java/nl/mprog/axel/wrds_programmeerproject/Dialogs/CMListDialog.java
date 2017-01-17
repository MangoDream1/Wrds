package nl.mprog.axel.wrds_programmeerproject.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.Activities.MainActivity;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 11-1-17.
 */

public class CMListDialog extends DialogFragment {

    // TODO split modify and create from each other based on dialog creation location

    private DatabaseManager dbm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity activity = getActivity();

        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.cm_dialog, null);

        dbm = DatabaseManager.getInstance();

        builder.setView(view);

        builder.setMessage("Modify/Create list")
                .setPositiveButton("Modify/Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO also do modify here not only create

                        String name = ((EditText) view.findViewById(R.id.list_name_editText))
                                .getText().toString();
                        String desc = ((EditText) view.findViewById(R.id.list_description_editText))
                                .getText().toString();
                        String lanA = ((EditText) view.findViewById(R.id.list_languageA_editText))
                                .getText().toString();
                        String lanB = ((EditText) view.findViewById(R.id.list_languageB_editText))
                                .getText().toString();

                        dbm.insertList(name, desc, "you", lanA, lanB);

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
