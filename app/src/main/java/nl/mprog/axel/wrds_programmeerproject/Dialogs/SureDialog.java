package nl.mprog.axel.wrds_programmeerproject.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by axel on 26-1-17.
 */

public class SureDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Bundle arguments = getArguments();


        return super.onCreateDialog(savedInstanceState);
    }
}
