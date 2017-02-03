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

/**
 * ListActivity where the user can add new words, delete words or modify words. From here the user
 * can start the exam of list.
 */

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
        dbm = DatabaseManager.getInstance();

        restoreSavedInstanceState(savedInstanceState);

        Intent intent = getIntent();
        listId = intent.getLongExtra("id", 0L);

        setToolbar();
        populateListView(listId);
        findEditTexts();
    }

    /**
     * Signal data change and update ListView
     */
    public void dataChange() {
        adapter.swapCursor(dbm.getListWords(listId));
        adapter.notifyDataSetChanged();
    }

    /**
     * Create the toolbar + add title to it
     */
    private void setToolbar() {
        Cursor listCursor = dbm.getUserListTitle(listId);
        title = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.STR_TITLE));
        String lanA  = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_A));
        String lanB  = listCursor.getString(listCursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_B));

        title = String.format("%s (%s - %s)", title, lanA, lanB);

        // Creates toolbar and sets title
        toolbar = (Toolbar) findViewById(R.id.main_menu);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

    }

    /**
     * Set ListView and set adapter
     * @param listId id of list
     */
    private void populateListView(long listId) {
        Cursor cursor = dbm.getListWords(listId);

        adapter = new WordsCursorAdapter(this, R.layout.word_item, cursor, 0);

        // Populate listView
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (dbm.isListOwner(listId)) {
            addFooter(listView);
        }
    }

    /**
     * Add footer add button to listview
     * @param listView listView
     */
    private void addFooter(ListView listView) {
        // Set editText footer
        View footer = getLayoutInflater().inflate(R.layout.word_footer, null);
        listView.addFooterView(footer);

        // Set onClick
        Button addWordButton = (Button) footer.findViewById(R.id.add_word_button);
        addWordButton.setOnClickListener(this);
    }

    /**
     * Find EditTexts
     */
    private void findEditTexts() {
        wordAEditText = (EditText) findViewById(R.id.wordA_editText);
        wordBEditText = (EditText) findViewById(R.id.wordB_editText);
    }

    /**
     * Create the toolbar menu. Save menu in currentMenu
     * @param menu  menu
     * @return      true
     */
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

    /**
     * Show the edit toolbar with correct buttons shown depending on length of selectedItemsList
     */
    private void showEditToolbar() {
        currentMenu.clear();
        toolbar.setTitle("");
        getMenuInflater().inflate(R.menu.edit_menu, currentMenu);

        currentMenu.findItem(R.id.share_button).setVisible(false);
        currentMenu.findItem(R.id.copy_button).setVisible(false);

        if (selectedItemsList.size() > 1) {
            currentMenu.findItem(R.id.modify_button).setVisible(false);
        } else {
            currentMenu.findItem(R.id.modify_button).setVisible(true);
        }
    }

    /**
     * Show main toolbar with correct buttons
     */
    private void showMainToolbar() {
        currentMenu.clear();
        toolbar.setTitle(title);
        getMenuInflater().inflate(R.menu.main_menu, currentMenu);

        currentMenu.findItem(R.id.play_button).setVisible(true);
        currentMenu.findItem(R.id.results_button).setVisible(true);
    }

    /**
     * onItemClick selects word and sets background
     */
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

    /**
     * onClick buttons
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_word_button:
                addButton();
                break;
        }
    }

    /**
     * Add button adds a word
     */
    private void addButton() {
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
    }

    /**
     * Give the correct warning and toast
     * @param wordA wordA
     * @param wordB wordB
     */
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

    /**
     * onOptionsItemSelected
     * @param item  item
     * @return      true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play_button:
                return playMenuButton();

            case R.id.results_button:
                return resultMenuButton();

            case R.id.return_button:
                return returnMenuButton();

            case R.id.modify_button:
                return modifyMenuButton();

            case R.id.delete_button:
                return deleteMenuButton();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Play button starts ExamActivity of this list if there are words present
     * @return true
     */
    private boolean playMenuButton() {
        if (dbm.countListWords(listId) == 0) {
            Toast.makeText(this, R.string.toast_no_words, Toast.LENGTH_SHORT).show();
            return true;
        }

        Intent intent = new Intent(this, ExamActivity.class);
        intent.putExtra("id", listId);

        startActivity(intent);

        return true;
    }

    /**
     * Shows ResultActivity if there are results else show toast
     * @return true
     */
    private boolean resultMenuButton() {
        if (dbm.getHighestTries(listId) == 0L) {
            Toast.makeText(getApplicationContext(), R.string.toast_list_no_results
                    , Toast.LENGTH_SHORT).show();
            return true;
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("id", listId);

        startActivity(intent);

        return true;
    }

    /**
     * Return menu button deselects selection
     * @return true
     */
    private boolean returnMenuButton() {
        selectedItemsList.clear();
        showMainToolbar();
        dataChange();

        return true;
    }

    /**
     * Modify menu button starts ModifyWordDialog
     * If user is not owner do not allow and show toast
     * @return true
     */
    private boolean modifyMenuButton() {
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
    }

    /**
     * Start DefaultDialog with as origin "delete" after confirmation delete selected words
     * If user is not owner do not allow and show toast
     * @return true
     */
    private boolean deleteMenuButton() {
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
        bundle.putString("origin", "delete");

        defaultDialog.setArguments(bundle);
        defaultDialog.show(getFragmentManager(), "DefaultDialog");

        return true;
    }

    /**
     * Save selectedItemsList
     * @param outState Bundle of outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedItemsList", selectedItemsList);
    }

    /**
     * Restore savedInstanceState if possible otherwise create empty list or selectedItemsList
     * @param savedInstanceState bundle with saved instance
     */
    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        // If there is a savedInstanceState get data from it, otherwise create empty list
        if (savedInstanceState == null) {
            selectedItemsList = new ArrayList<>();
        } else {
            selectedItemsList = (ArrayList<Long>) savedInstanceState
                    .getSerializable("selectedItemsList");
        }
    }

    /**
     * Interface callback if DefaultDialog is positive, used by delete and copy lists
     * @param origin origin of Dialog to be called back
     */
    @Override
    public void dialogPositive(String origin) {
        switch (origin) {
            case "delete":
                for (long id: selectedItemsList) {
                    dbm.deleteWord(id);
                }
                selectedItemsList.clear();
                showMainToolbar();
                dataChange();
                break;
        }
    }

    /**
     * Getter selectedList
     * @return selectedList
     */
    public ArrayList<Long> getSelectedItemsList() {
        return selectedItemsList;
    }
}
