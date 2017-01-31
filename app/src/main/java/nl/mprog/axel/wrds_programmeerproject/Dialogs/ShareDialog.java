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
import android.widget.TextView;
import android.widget.Toast;

import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.Database.FirebaseDBManager;
import nl.mprog.axel.wrds_programmeerproject.Interfaces.FirebaseKeyInterface;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 25-1-17.
 *
 * ShareDialog either shows upload, share owner or share. Upload asks for confirmation to upload
 * the list. Share owner shows the key, gives the possibility to update list or stop share. Users
 * that loaded a list can only see the key and nothing else.
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
        fdbm = FirebaseDBManager.getInstance();
        dbm = DatabaseManager.getInstance();

        Bundle arguments = getArguments();
        listId = arguments.getLong("id");
        userId = arguments.getString("userId");

        isShared = dbm.getFirebaseId(listId) != null;
        isOwner = dbm.isListOwner(listId);

        createView();
        AlertDialog.Builder builder = createBuilder();
        showKey();

        return createDialog(builder);
    }

    /**
     * Create view
     */
    private void createView() {
        LayoutInflater inflater = activity.getLayoutInflater();
        view = inflater.inflate(R.layout.share_dialog, null);
        view.findViewById(R.id.copy_button).setOnClickListener(this);
    }

    /**
     * Create builder
     * @return builder
     */
    private AlertDialog.Builder createBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        return createCorrectDialog(builder);
    }

    /**
     * Create dialog from builder, add buttons
     * @param builder   builder
     * @return          dialog
     */
    private Dialog createDialog(AlertDialog.Builder builder) {
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

    /**
     * Creates the correct dialog depending on the situation. With different buttons.
     * @param builder   builder
     * @return          builder with correct buttons
     */
    private AlertDialog.Builder createCorrectDialog(AlertDialog.Builder builder) {
        if (!isShared && isOwner) {
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            builder.setTitle(R.string.dialog_share_upload_title)
                    .setPositiveButton(R.string.button_upload, null)
                    .setNegativeButton(R.string.button_cancel, null)
                    .setMessage(R.string.dialog_share_message);
        } else if (isShared && isOwner) {
            builder.setTitle(R.string.dialog_share_title)
                    .setPositiveButton(R.string.button_update, null)
                    .setNegativeButton(R.string.button_continue, null)
                    .setNeutralButton(R.string.button_stop_share, null);
        } else {
            builder.setTitle(R.string.dialog_share_title)
                    .setNegativeButton(R.string.button_continue, null);
        }

        return builder;
    }

    /**
     * Callback from listIdExists. Calls showKey depending on keyExists
     * @param firebaseId firebaseId
     * @param keyExists keyExists boolean
     */
    @Override
    public void keyStillExists(String firebaseId, boolean keyExists) {
        if (keyExists) {
            showKey(firebaseId);
        } else {
            showKey(getString(R.string.error_list_id_deleted));
            view.findViewById(R.id.copy_button).setVisibility(View.GONE);
        }
    }

    /**
     * Sets the given key in TextView and makes it visible. Also hides progressBar
     * @param key Key to be displayed
     */
    private void showKey(String key) {
        ((TextView) view.findViewById(R.id.key_textView)).setText(key);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.key_layout).setVisibility(View.VISIBLE);
    }

    /**
     * Call correct showKey(String key)
     */
    private void showKey() {
        if (isShared && isOwner) {
            showKey(dbm.getFirebaseId(listId));
        } else if (isShared) {
            fdbm.listIdExists(dbm.getFirebaseId(listId), this);
        }
    }

    /**
     * onClick switch
     * @param view view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.copy_button:
                buttonCopy();
                break;
        }
    }

    /**
     * Copy the key to the clipboard
     */
    private void buttonCopy() {
        String firebaseId = dbm.getFirebaseId(listId);

        ClipboardManager clipboardManager = (ClipboardManager)
                activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("wrds list key", firebaseId);
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(activity, R.string.toast_key_copied, Toast.LENGTH_SHORT).show();
    }

    /**
     * Set positive button that uploads if not yet uploaded else updates
     * @param dialog dialog
     */
    private void setPositiveButton(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isShared && isOwner) {
                            uploadList();
                        } else {
                            updateList();
                        }
                    }
                });
    }

    /**
     * Uploads the list
     */
    private void uploadList() {
        Toast.makeText(activity, R.string.toast_list_uploaded,
                Toast.LENGTH_SHORT).show();
        fdbm.uploadList(listId, userId);

        Bundle bundle = new Bundle();
        bundle.putLong("id", listId);

        ShareDialog shareDialog = new ShareDialog();
        shareDialog.setArguments(bundle);
        shareDialog.show(getFragmentManager(), "ShareDialog");

        dismiss();
    }

    /**
     * Updates the list
     */
    private void updateList() {
        Toast.makeText(activity, R.string.toast_list_updated,
                Toast.LENGTH_SHORT).show();
        fdbm.uploadList(listId, userId);
    }

    /**
     * Set negative button that dismisses dialog.
     * @param dialog dialog
     */
    private void setNegativeButton(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
    }

    /**
     * Set neutral button that updates list
     * @param dialog dialog
     */
    private void setNeutralButton(AlertDialog dialog) {
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String firebaseId = dbm.getFirebaseId(listId);
                        fdbm.deleteList(listId, firebaseId);
                        Toast.makeText(activity, R.string.toast_stopped_share,
                                Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }
}
