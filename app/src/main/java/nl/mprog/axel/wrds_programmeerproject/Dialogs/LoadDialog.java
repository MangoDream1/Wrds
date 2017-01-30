package nl.mprog.axel.wrds_programmeerproject.Dialogs;

import android.app.Activity;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import nl.mprog.axel.wrds_programmeerproject.Activities.MainActivity;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 25-1-17.
 */

public class LoadDialog extends DialogFragment implements View.OnClickListener {

    private DatabaseManager dbm;
    private FirebaseDatabase firebaseDB;

    private EditText keyEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();

        dbm = DatabaseManager.getInstance();
        firebaseDB = FirebaseDatabase.getInstance();

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.load_dialog, null);

        keyEditText = (EditText) view.findViewById(R.id.key_editText);

        view.findViewById(R.id.paste_button).setOnClickListener(this);

        final AlertDialog dialog = new AlertDialog.Builder(activity).setView(view)
                .setTitle(R.string.dialog_load_title)
                .setPositiveButton(R.string.button_load_list, null)
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        //http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = keyEditText.getText().toString();

                        getListFirebase(key);

                    }
                });
            }
        });

        return dialog;
    }

    private void getListFirebase(final String key) {
        firebaseDB.getReference().child("lists").child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();

                        if (data == null) {
                            keyEditText.setError(getString(R.string.error_key_missing));
                        } else {
                            dbm.insertFromFirebase(data, key);

                            ((MainActivity) getActivity()).dataChange();
                            dismiss(getDialog());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        keyEditText.setError(getString(R.string.error_database));
                    }
                });
    }

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
}
