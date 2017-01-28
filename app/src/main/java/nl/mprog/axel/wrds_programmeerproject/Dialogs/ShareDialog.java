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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.Database.FirebaseDBManager;
import nl.mprog.axel.wrds_programmeerproject.Interfaces.FirebaseKeyInterface;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 25-1-17.
 */

public class ShareDialog extends DialogFragment
        implements FirebaseKeyInterface, View.OnClickListener {

    FirebaseDBManager fdbm;
    DatabaseManager dbm;

    Activity activity;
    View view;

    String userId;
    long listId;

    boolean isShared;
    boolean isOwner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)  {
        activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();

        view = inflater.inflate(R.layout.share_dialog, null);
        builder.setView(view);

        fdbm = FirebaseDBManager.getInstance();
        dbm = DatabaseManager.getInstance();

        Bundle arguments = getArguments();
        listId = arguments.getLong("id");
        userId = arguments.getString("userId");

        isShared = dbm.getFirebaseId(listId) != null;
        isOwner = dbm.isListOwner(listId);

        view.findViewById(R.id.copy_button).setOnClickListener(this);

        if (isShared && isOwner) {
            showKey(dbm.getFirebaseId(listId));
        } else if (isShared) {
            fdbm.listIdExists(dbm.getFirebaseId(listId), this);
        }

        builder = createCorrectDialog(builder);

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                AlertDialog dialog = (AlertDialog) dialogInterface;

                setPositiveButton(dialog);
                setNegativeButton(dialog);
                setNeutralButton(dialog);
            }
        });

        return dialog;
    }

    private AlertDialog.Builder createCorrectDialog(AlertDialog.Builder builder) {
        if (!isShared && isOwner) {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            builder.setTitle("Upload list")
                    .setPositiveButton("Upload", null)
                    .setNegativeButton("Cancel", null)
                    .setMessage("Upload your list and share the key with your friends");
        } else if (isShared && isOwner) {
            builder.setTitle("Shared list")
                    .setPositiveButton("Update", null)
                    .setNegativeButton("Continue", null)
                    .setNeutralButton("Stop share", null);
        } else {
            builder.setTitle("Shared list")
                    .setNegativeButton("Continue", null);
        }

        return builder;
    }

    @Override
    public void keyStillExists(String firebaseId, boolean keyExists) {
        if (keyExists) {
            showKey(firebaseId);
        } else {
            showKey("List is deleted by the owner thus cannot be shared");
            view.findViewById(R.id.copy_button).setVisibility(View.GONE);
        }
    }

    private void showKey(String key) {
        ((TextView) view.findViewById(R.id.key_editText)).setText(key);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.key_layout).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.copy_button:
                String firebaseId = dbm.getFirebaseId(listId);

                ClipboardManager clipboardManager = (ClipboardManager)
                        activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("wrds list key", firebaseId);
                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(activity, "Key copied", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void setPositiveButton(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isShared && isOwner) {
                            Toast.makeText(activity, "List uploaded", Toast.LENGTH_SHORT).show();
                            fdbm.uploadList(listId, userId);

                            Bundle bundle = new Bundle();
                            bundle.putLong("id", listId);

                            ShareDialog shareDialog = new ShareDialog();
                            shareDialog.setArguments(bundle);
                            shareDialog.show(getFragmentManager(), "ShareDialog");

                            dismiss();
                        } else {
                            Toast.makeText(activity, "List updated", Toast.LENGTH_SHORT).show();
                            fdbm.uploadList(listId, userId);
                        }
                    }
                });
    }

    private void setNegativeButton(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
    }

    private void setNeutralButton(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String firebaseId = dbm.getFirebaseId(listId);
                        fdbm.deleteList(listId, firebaseId);
                        Toast.makeText(activity, "Stopped share", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }
}
