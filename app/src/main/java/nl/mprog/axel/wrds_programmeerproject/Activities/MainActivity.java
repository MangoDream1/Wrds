package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.SQLException;
import java.util.ArrayList;

import nl.mprog.axel.wrds_programmeerproject.Adapters.WordListsCursorAdapter;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.Dialogs.CMListDialog;
import nl.mprog.axel.wrds_programmeerproject.Dialogs.DefaultDialog;
import nl.mprog.axel.wrds_programmeerproject.Dialogs.LoadDialog;
import nl.mprog.axel.wrds_programmeerproject.Dialogs.ShareDialog;
import nl.mprog.axel.wrds_programmeerproject.Interfaces.DefaultDialogInterface;
import nl.mprog.axel.wrds_programmeerproject.R;

/**
 * In the MainActivity the user can create, share, delete, modify and load lists. These lists can
 * be selected going to ListActivity. If the user is not logged and share is pressed the user will
 * go to the LogInActivity.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        DefaultDialogInterface{

    private DatabaseManager dbm = DatabaseManager.getInstance();
    private WordListsCursorAdapter adapter;

    private ArrayList<Long> selectedItemsList;

    private Toolbar toolbar;

    private Menu currentMenu;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            dbm.open(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        restoreSavedInstanceState(savedInstanceState);
        fillListView();
        startFirebaseAuth();
        setButtonListeners();
        setToolbar();
    }

    /**
     * Set ListView listener and set adapter
     */
    private void fillListView() {
        Cursor cursor = dbm.getUserLists();
        adapter = new WordListsCursorAdapter(this, R.layout.list_item, cursor, 0);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.emptyView));
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    /**
     * Set button listeners
     */
    private void setButtonListeners() {
        Button add_button = (Button) findViewById(R.id.add_button);
        add_button.setOnClickListener(this);
    }

    /**
     * Start Firebase authentication listener
     */
    private void startFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TEST", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TEST", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    /**
     * Create the toolbar
     */
    private void setToolbar() {
        // Creates toolbar
        toolbar = (Toolbar) findViewById(R.id.main_menu);
        setSupportActionBar(toolbar);
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

        if (selectedItemsList.size() > 1) {
            currentMenu.findItem(R.id.share_button).setVisible(false);
            currentMenu.findItem(R.id.modify_button).setVisible(false);
            currentMenu.findItem(R.id.copy_button).setVisible(false);
        } else {
            currentMenu.findItem(R.id.share_button).setVisible(true);
            currentMenu.findItem(R.id.modify_button).setVisible(true);
            currentMenu.findItem(R.id.copy_button).setVisible(true);
        }
    }

    /**
     * Show the main toolbar with correct buttons depending on if the user is logged in
     */
    private void showMainToolbar() {
        currentMenu.clear();
        toolbar.setTitle(R.string.app_name);

        getMenuInflater().inflate(R.menu.main_menu, currentMenu);

        currentMenu.findItem(R.id.load_list_button).setVisible(true);

        if (FirebaseAuth.getInstance().getCurrentUser() != null ) {
            currentMenu.findItem(R.id.log_out_button).setVisible(true);
        } else {
            currentMenu.findItem(R.id.log_out_button).setVisible(false);
        }
    }

    /**
     * OnClick listener
     * @param v view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                addButton();
                break;
        }
    }

    /**
     * Start CMListDialog to create a list
     */
    private void addButton() {
        CMListDialog cmListDialog = new CMListDialog();
        cmListDialog.show(getFragmentManager(), "CMListDialog");
    }

    /**
     * Signal data change and update ListView
     */
    public void dataChange() {
        adapter.swapCursor(dbm.getUserLists());
        adapter.notifyDataSetChanged();
    }

    /**
     * Menu bar selection
     * @param item selected item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.return_button:
                return returnMenuButton();

            case R.id.modify_button:
                return modifyMenuButton();

            case R.id.copy_button:
                return copyMenuButton();

            case R.id.delete_button:
                return deleteMenuButton();

            case R.id.share_button:
                return shareMenuButton();

            case R.id.load_list_button:
                return loadListMenuButton();

            case R.id.log_out_button:
                return logOutMenuButton();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Return menu button to clear selectedItemsList
     * @return true
     */
    private Boolean returnMenuButton() {
        selectedItemsList.clear();
        dataChange();
        showMainToolbar();

        return true;
    }

    /**
     * Calls modify dialog on single selectedItemsList; only availible if only 1 item is selected
     * @return true
     */
    private Boolean modifyMenuButton() {
        Bundle bundle = new Bundle();
        bundle.putLong("id", selectedItemsList.get(0));

        CMListDialog cmListDialog = new CMListDialog();
        cmListDialog.setArguments(bundle);
        cmListDialog.show(getFragmentManager(), "CMListDialog");

        return true;
    }

    /**
     * Calls default dialog with origin "copy". If positive selection by user will copy list.
     * @return true
     */
    private Boolean copyMenuButton() {
        DefaultDialog defaultDialog = new DefaultDialog();

        Bundle bundle = new Bundle();

        bundle.putInt("title", R.string.dialog_copy_title);
        bundle.putInt("message", R.string.dialog_copy_message);
        bundle.putInt("positive", R.string.button_yes);
        bundle.putInt("negative", R.string.button_no);
        bundle.putString("origin", "copy");

        defaultDialog.setArguments(bundle);
        defaultDialog.show(getFragmentManager(), "DefaultDialog");

        return true;
    }

    /**
     * Calls default dialog with origin "delete" after confirmation will delete all selected list
     * @return true
     */
    private Boolean deleteMenuButton() {
        DefaultDialog defaultDialog = new DefaultDialog();

        Bundle bundle = new Bundle();
        bundle.putInt("title", R.string.dialog_delete_title);
        bundle.putInt("message", R.string.dialog_delete_list_message);
        bundle.putInt("positive", R.string.button_yes);
        bundle.putInt("negative", R.string.button_no);
        bundle.putString("origin", "delete");

        defaultDialog.setArguments(bundle);
        defaultDialog.show(getFragmentManager(), "DefaultDialog");

        return true;
    }

    /**
     * If user is logged in will goto LogInActivity otherwise will start ShareDialog
     * @return true
     */
    private Boolean shareMenuButton() {
        long listId = selectedItemsList.get(0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Uploads selected list
            Bundle bundle = new Bundle();
            bundle.putString("userId", user.getUid());
            bundle.putLong("id", listId);

            ShareDialog shareDialog = new ShareDialog();
            shareDialog.setArguments(bundle);
            shareDialog.show(getFragmentManager(), "ShareDialog");

        } else {
            // Not logged in thus goto login
            Intent intent = new Intent(this, LogInActivity.class);
            intent.putExtra("id", listId);
            startActivity(intent);
        }

        return true;
    }

    /**
     * Starts load dialog
     * @return true
     */
    private Boolean loadListMenuButton() {
        LoadDialog loadDialog = new LoadDialog();
        loadDialog.show(getFragmentManager(), "LoadDialog");

        return true;
    }

    /**
     * Starts DefaultDialog with origin "logOut" after confirmation will log out
     * @return true
     */
    private Boolean logOutMenuButton() {
        DefaultDialog defaultDialog = new DefaultDialog();

        Bundle bundle = new Bundle();
        bundle.putInt("title", R.string.dialog_logout_title);
        bundle.putInt("positive", R.string.button_yes);
        bundle.putInt("negative", R.string.button_no);
        bundle.putString("origin", "logOut");

        defaultDialog.setArguments(bundle);
        defaultDialog.show(getFragmentManager(), "DefaultDialog");

        return true;
    }

    /**
     * onItemClick to goto ListActivity of clicked list
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();
        Intent intent = new Intent(context, ListActivity.class);
        intent.putExtra("id", id);

        context.startActivity(intent);
    }

    /**
     * onItemLongClick to select lists. Change color and add to list. Also change toolbar if needed
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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

        return true;
    }

    /**
     * Interface callback if DefaultDialog is positive, used by delete and copy lists
     * @param origin origin of Dialog to be called back
     */
    @Override
    public void dialogPositive(String origin) {
        switch (origin) {
            case "delete":
                // Reached when user is sure it wants to delete
                for (long id: selectedItemsList) {
                    dbm.deleteList(id);
                }
                selectedItemsList.clear();
                break;
            case "copy":
                dbm.copyList(selectedItemsList.get(0));
                break;
            case "logOut":
                FirebaseAuth.getInstance().signOut();
                break;
        }

        showMainToolbar();
        dataChange();
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
     * Getter of selectedItemsList
     * @return selectedItemsList
     */
    public ArrayList<Long> getSelectedItemsList() {
        return selectedItemsList;
    }

    /**
     * onStart add authentication listener for Firebase
     */
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    /**
     * Call dataChange() after onResume to reselected items
     */
    @Override
    protected void onResume() {
        super.onResume();
        dataChange();
    }

    /**
     * onStop remove authentication listener of Firebase
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    /**
     * onDestroy close the database
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbm.close();
    }
}

