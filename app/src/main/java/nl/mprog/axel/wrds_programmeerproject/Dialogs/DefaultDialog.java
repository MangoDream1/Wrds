package nl.mprog.axel.wrds_programmeerproject.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import nl.mprog.axel.wrds_programmeerproject.Interfaces.DefaultDialogInterface;

/**
 * Created by axel on 26-1-17.
 *
 * DefaultDialog creates a dialog with the given title, message, positive button string
 * and negative button string. Then uses the callback DefaultDialogInterface.
 *
 */

public class DefaultDialog extends DialogFragment {
    String origin;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle arguments = getArguments();
        int title = arguments.getInt("title", -1);
        int message = arguments.getInt("message", -1);
        int positiveString = arguments.getInt("positive", -1);
        int negativeString = arguments.getInt("negative", -1);
        origin = arguments.getString("origin", null);

        if (message != -1) { builder.setMessage(message); }
        if (title != -1) { builder.setTitle(title); }

        builder = addNegativeButton(addPositiveButton(builder, positiveString), negativeString);

        return builder.create();
    }

    /**
     * Add positive button to builder
     * @param builder           builder without positive button
     * @param positiveString    button string
     * @return                  builder with positive button
     */
    private AlertDialog.Builder addPositiveButton(AlertDialog.Builder builder, int positiveString) {
        if (positiveString != 0) {
            builder.setPositiveButton(positiveString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((DefaultDialogInterface) getActivity()).dialogPositive(origin);
                }
            });
        }

        return builder;
    }

    /**
     * Add negative button to builder
     * @param builder           builder without negative button
     * @param negativeString    button string
     * @return                  builder with negative button
     */
    private AlertDialog.Builder addNegativeButton(AlertDialog.Builder builder, int negativeString) {
        if (negativeString != 0) {
            builder.setNegativeButton(negativeString, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        return builder;
    }
}
