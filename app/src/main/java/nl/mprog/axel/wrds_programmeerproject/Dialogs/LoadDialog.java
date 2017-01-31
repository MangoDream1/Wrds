package nl.mprog.axel.wrds_programmeerproject.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

import nl.mprog.axel.wrds_programmeerproject.Activities.MainActivity;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.Database.FirebaseDBManager;
import nl.mprog.axel.wrds_programmeerproject.Interfaces.QueryFirebaseInterface;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 25-1-17.
 *
 * LoadDialog takes the key given by the user and loads the corresponding list from Firebase.
 *
 */

public class LoadDialog extends DialogFragment implements View.OnClickListener,
        DialogInterface.OnShowListener, QueryFirebaseInterface {

    private DatabaseManager dbm;
    private FirebaseDBManager fdbm;

    private EditText keyEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dbm = DatabaseManager.getInstance();
        fdbm = FirebaseDBManager.getInstance();

        View view = createView();
        AlertDialog dialog = createDialog(view);
        dialog.setOnShowListener(this);

        return dialog;
    }


    /**
     * Creates view, finds EditText and sets button
     * @return created view
     */
    private View createView() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.load_dialog, null);
        keyEditText = (EditText) view.findViewById(R.id.key_textView);
        view.findViewById(R.id.paste_button).setOnClickListener(this);

        return view;
    }

    /**
     * Create dialog with buttons
     * @param view view
     * @return created dialog
     */
    private AlertDialog createDialog(View view) {
        return new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle(R.string.dialog_load_title)
                .setPositiveButton(R.string.button_load_list, null)
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
    }

    /**
     * Dismiss given dialog
     * @param dialog dialog to be dismissed
     */
    private void dismiss(Dialog dialog) {
        dialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.paste_button:
                ClipboardManager clipboard = (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

                if (!clipboard.hasPrimaryClip()) {
                    Toast.makeText(getActivity(), R.string.toast_empty_clipboard,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                keyEditText.setText(item.getText().toString());

                break;
        }
    }

    /**
     * onShow that sets button positive to getListFirebase(key, callback)
     * code idea adapted from http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog
     * -from-closing-when-a-button-is-clicked
     * @param dialog dialog
     */
    @Override
    public void onShow(DialogInterface dialog) {
        final LoadDialog callback = this;

        Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = keyEditText.getText().toString();

                fdbm.getListFirebase(key, callback);

            }
        });
    }

    /**
     * onDataChange called as callback from getListFirebase(key, callback) on succes
     * Inserts data into local database or sets error is data is missing.
     * @param data list data
     * @param key firebase key
     */

    @Override
    public void onDataChange(Object data, String key) {
        if (data == null) {
            keyEditText.setError(getString(R.string.error_key_missing));
        } else {
            dbm.insertFromFirebase((Map<String, Object>) data, key);

            ((MainActivity) getActivity()).dataChange();
            dismiss(getDialog());
        }
    }

    /**
     * If there was a firebase database errort, then set error
     */
    @Override
    public void onCancelled() {
        keyEditText.setError(getString(R.string.error_database));
    }
}
