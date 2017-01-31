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
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;

import nl.mprog.axel.wrds_programmeerproject.Activities.MainActivity;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseHelper;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * Created by axel on 11-1-17.
 *
 * Create or modify list dialog that either handles creation or modification of list, depending
 * on content of arguments.
 */

public class CMListDialog extends DialogFragment implements View.OnClickListener,
        DialogInterface.OnShowListener {

    private DatabaseManager dbm;
    private Boolean isModify = false;
    private long listId;

    private EditText titleEditText;
    private EditText descEditText;
    private EditText lanAEditText;
    private EditText lanBEditText;

    // Default values
    private int title = R.string.dialog_create_list_title;
    private int positiveButtonString = R.string.button_create;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dbm = DatabaseManager.getInstance();

        Activity activity = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.cm_list_dialog, null);
        builder.setView(view);

        findViews(view);
        changeIfModify();
        builder = createButtons(builder);

        Dialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }

    /**
     * Find the view
     * @param view view
     */
    private void findViews(View view) {
        titleEditText = (EditText) view.findViewById(R.id.list_name_editText);
        descEditText = (EditText) view.findViewById(R.id.list_description_editText);
        lanAEditText = (EditText) view.findViewById(R.id.list_languageA_editText);
        lanBEditText = (EditText) view.findViewById(R.id.list_languageB_editText);
    }

    /**
     * Checks if it is modify instead of create and changes necessary functions.
     */
    private void changeIfModify() {
        if (getArguments() != null) {
            title = R.string.dialog_modify_list_title;
            positiveButtonString = R.string.button_modify;
            isModify = true;

            setArgumentsText();
        }
    }

    /**
     * From arguments id get database information and set text in EditTexts
     */
    private void setArgumentsText() {
        listId = getArguments().getLong("id");
        Cursor cursor = dbm.getSingleList(listId);

        titleEditText.setText(cursor.getString(
                cursor.getColumnIndex(DatabaseHelper.STR_TITLE)));
        descEditText.setText(cursor.getString(
                cursor.getColumnIndex(DatabaseHelper.STR_DESC)));
        lanAEditText.setText(cursor.getString(
                cursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_A)));
        lanBEditText.setText(cursor.getString(
                cursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_B)));
    }

    /**
     * Add buttons to the builder
     * @param builder builder without buttons
     * @return builder with buttons
     */
    private AlertDialog.Builder createButtons(AlertDialog.Builder builder) {
        builder.setTitle(title)
                .setPositiveButton(positiveButtonString, null)
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog
                        // Empty since this does nothing except close
                    }
                });

        return builder;
    }

    /**
     * onClick on positive button either update or insert list and call dataChange() in MainActivity
     * @param v view
     */
    @Override
    public void onClick(View v) {
        String name = titleEditText.getText().toString().trim();
        String desc = descEditText.getText().toString().trim();
        String lanA = lanAEditText.getText().toString().trim();
        String lanB = lanBEditText.getText().toString().trim();

        if (validateForm()) {
            if (isModify) {
                dbm.updateList(listId, name, desc, "you", lanA, lanB);
            } else {
                dbm.insertList(name, desc, "you", lanA, lanB);
            }

            ((MainActivity) getActivity()).dataChange();

            dismiss();
        }
    }

    /**
     * onShow of dialog set positiveButton to onClick listener.
     * @param dialog dialog
     */
    @Override
    public void onShow(DialogInterface dialog) {
        Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    /**
     * Validate form
     * @return true if valid, false if not
     */
    private boolean validateForm() {
        // If statement is lazy thus not possible thus check all first then find false
        Boolean[] isValid = new Boolean[]{validateTitle(), validateLanA(), validateLanB()};

        return !Arrays.asList(isValid).contains(false);
    }

    /**
     * Validate the title, if not set error
     * @return true if valid, false if not
     */
    private boolean validateTitle() {
        String title = titleEditText.getText().toString();

        if (title.isEmpty()) {
            titleEditText.setError(getString(R.string.error_required));
            return false;
        } else if (title.length() < 2) {
            titleEditText.setError(getString(R.string.error_title_short));
            return false;
        }

        return true;
    }

    /**
     * Validate language, if not set error
     * @param lanEditText language EditText
     * @return true if valid, false if not
     */
    private boolean validateLanguage(EditText lanEditText) {
        if (lanEditText.getText().toString().isEmpty()) {
            lanEditText.setError(getString(R.string.error_required));
            return false;
        }

        return true;
    }

    /**
     * Validate language A using validateLanguage(EditText lanEditText)
     * @return validateLanguage(EditText lanEditText) result
     */
    private boolean validateLanA() {
        return validateLanguage(lanAEditText);
    }

    /**
     * Validate language B using validateLanguage(EditText lanEditText)
     * @return validateLanguage(EditText lanEditText) result
     */
    private boolean validateLanB() {
        return validateLanguage(lanBEditText);
    }

}
