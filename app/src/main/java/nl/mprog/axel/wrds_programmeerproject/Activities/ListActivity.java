package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import nl.mprog.axel.wrds_programmeerproject.Adapters.WordsCursorAdapter;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseHelper;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.Dialogs.DefaultDialog;
import nl.mprog.axel.wrds_programmeerproject.Dialogs.ModifyWordDialog;
import nl.mprog.axel.wrds_programmeerproject.Interfaces.DefaultDialogInterface;
import nl.mprog.axel.wrds_programmeerproject.R;

public class ListActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, DefaultDialogInterface {

    private long listId;
    private DatabaseManager dbm;
    private WordsCursorAdapter adapter;

    private EditText wordAEditText;
    private EditText wordBEditText;

    private Menu currentMenu;
    private Toolbar toolbar;
    private String title;

    private ArrayList<Long> selectedItemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // If there is a savedInstanceState get data from it, otherwise create empty list
        if (savedInstanceState == null) {
            selectedItemsList = new ArrayList<>();
        } else {
            selectedItemsList = (ArrayList<Long>) savedInstanceState
                    .getSerializable("selectedItemsList");
        }

        Intent intent = getIntent();
        listId = intent.getLongExtra("id", 0L);

        dbm = DatabaseManager.getInstance();

        Cursor listCursor = dbm.getUserListTitle(listId);
        title = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.STR_TITLE));
        String lanA  = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_A));
        String lanB  = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_B));

        title = String.format("%s (%s - %s)", title, lanA, lanB);

        // Creates toolbar and sets title
        toolbar = (Toolbar) findViewById(R.id.main_menu);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        Cursor cursor = dbm.getListWords(listId);

        adapter = new WordsCursorAdapter(this, R.layout.word_item, cursor, 0);

        // Populate listView
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (dbm.isListOwner(listId)) {
            // Set editText footer
            View footer = getLayoutInflater().inflate(R.layout.word_footer, null);
            listView.addFooterView(footer);

            // Set onClick
            Button addWordButton = (Button) footer.findViewById(R.id.add_word_button);
            addWordButton.setOnClickListener(this);
        }

        // Find editTexts
        wordAEditText = (EditText) findViewById(R.id.wordA_editText);
        wordBEditText = (EditText) findViewById(R.id.wordB_editText);

    }

    private void showEditToolbar() {
        currentMenu.clear();
        toolbar.setTitle("");
        getMenuInflater().inflate(R.menu.edit_menu, currentMenu);

        currentMenu.findItem(R.id.share_button).setVisible(false);

        if (selectedItemsList.size() > 1) {
            currentMenu.findItem(R.id.modify_button).setVisible(false);
        } else {
            currentMenu.findItem(R.id.modify_button).setVisible(true);
        }
    }

    private void showMainToolbar() {
        currentMenu.clear();
        toolbar.setTitle(title);
        getMenuInflater().inflate(R.menu.main_menu, currentMenu);

        currentMenu.findItem(R.id.play_button).setVisible(true);
        currentMenu.findItem(R.id.results_button).setVisible(true);
    }

    @Override
    public void dialogPositive() {
        for (long id: selectedItemsList) {
            dbm.deleteWord(id);
        }

        selectedItemsList.clear();
        showMainToolbar();
        dataChange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        currentMenu = menu;

        // If instance is restored and there are selections show correct menu
        if (!selectedItemsList.isEmpty()) {
            showEditToolbar();
        } else {
            showMainToolbar();
        }

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (selectedItemsList.contains(id)) {
            selectedItemsList.remove(id);
            view.setBackgroundColor(Color.TRANSPARENT);
        } else {
            selectedItemsList.add(id);
            view.setBackgroundColor(Color.LTGRAY);
        }

        if (!selectedItemsList.isEmpty()) {
            showEditToolbar();
        } else {
            showMainToolbar();
        }
    }

    private void giveWarningNotFilledIn(String wordA, String wordB) {
        String toastMessage = null;

        if (wordA.isEmpty()) {
            toastMessage = "Please enter Word A";
            ((EditText) findViewById(R.id.wordA_editText)).setError("Required.");
        }

        if (wordB.isEmpty()) {
            toastMessage = "Please enter Word B";
            ((EditText) findViewById(R.id.wordB_editText)).setError("Required.");
        }

        if (wordA.isEmpty() && wordB.isEmpty()) {
            toastMessage = "Please enter Word A and Word B";
        }

        if (toastMessage != null) {
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_word_button:
                String wordA = wordAEditText.getText().toString();
                String wordB = wordBEditText.getText().toString();


                if (!wordA.isEmpty() && !wordB.isEmpty()) {
                    dbm.insertWord(listId, wordA, wordB);
                    dataChange();

                    // Reset
                    wordAEditText.setText("");
                    wordBEditText.setText("");

                    wordAEditText.requestFocus();
                }

                giveWarningNotFilledIn(wordA, wordB);

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play_button:
                if (dbm.countListWords(listId) == 0) {
                    Toast.makeText(this, R.string.toast_no_words, Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent intent = new Intent(this, ExamActivity.class);
                intent.putExtra("id", listId);

                startActivity(intent);
                return true;

            case R.id.results_button:

                if (dbm.getHighestTries(listId) == 0L) {
                    Toast.makeText(getApplicationContext(), R.string.toast_list_no_results
                            , Toast.LENGTH_SHORT).show();
                    return true;
                }

                intent = new Intent(this, ResultActivity.class);
                intent.putExtra("id", listId);

                startActivity(intent);
                return true;

            case R.id.return_button:
                selectedItemsList.clear();
                showMainToolbar();
                dataChange();

                return true;

            case R.id.modify_button:
                if (!dbm.isListOwner(listId)) {
                    Toast.makeText(this, R.string.toast_modify_not_owner,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                // Edit button is only available if size of selectedItemsList is 1 thus first item
                // is to be edited
                Bundle arguments = new Bundle();
                arguments.putLong("id", selectedItemsList.get(0));

                ModifyWordDialog mwd = new ModifyWordDialog();
                mwd.setArguments(arguments);
                mwd.show(getFragmentManager(), "ModifyWordDialog");

                selectedItemsList.clear();
                showMainToolbar();

                return true;

            case R.id.delete_button:
                if (!dbm.isListOwner(listId)) {
                    Toast.makeText(this, R.string.toast_delete_not_owner,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                DefaultDialog defaultDialog = new DefaultDialog();

                Bundle bundle = new Bundle();

                bundle.putInt("title", R.string.dialog_delete_title);
                bundle.putInt("positive", R.string.button_yes);
                bundle.putInt("negative", R.string.button_no);

                defaultDialog.setArguments(bundle);
                defaultDialog.show(getFragmentManager(), "DefaultDialog");

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedItemsList", selectedItemsList);
    }

    public void dataChange() {
        adapter.swapCursor(dbm.getListWords(listId));
        adapter.notifyDataSetChanged();
    }

    public ArrayList<Long> getSelectedItemsList() {
        return selectedItemsList;
    }
}
