package nl.mprog.axel.wrds_programmeerproject.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import nl.mprog.axel.wrds_programmeerproject.Interfaces.DefaultDialogInterface;

/**
 * Created by axel on 26-1-17.
 */

public class DefaultDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Activity activity = getActivity();

        Bundle arguments = getArguments();
        String title = arguments.getString("title", null);
        String message = arguments.getString("message", null);
        String positiveString = arguments.getString("positive", null);
        String negativeString = arguments.getString("negative", null);

        builder.setMessage(message);
        builder.setTitle(title);

        if (positiveString != null) {
            builder.setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((DefaultDialogInterface) activity).dialogPositive();
                }
            });
        }

        if (negativeString != null) {
            builder.setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        return builder.create();
    }
}
