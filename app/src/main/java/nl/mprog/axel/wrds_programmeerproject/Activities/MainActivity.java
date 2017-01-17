package nl.mprog.axel.wrds_programmeerproject.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

import nl.mprog.axel.wrds_programmeerproject.Adapters.WordListsCursorAdapter;
import nl.mprog.axel.wrds_programmeerproject.Database.DatabaseManager;
import nl.mprog.axel.wrds_programmeerproject.Dialogs.CMListDialog;
import nl.mprog.axel.wrds_programmeerproject.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private DatabaseManager dbm = DatabaseManager.getInstance();
    private WordListsCursorAdapter adapter;

    private ArrayList<Long> selectedItemsList;

    private Toolbar toolbar;

    private Menu currentMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If there is a savedInstanceState get data from it, otherwise create empty list
        if (savedInstanceState == null) {
            selectedItemsList = new ArrayList<>();
        } else {
            selectedItemsList = (ArrayList<Long>) savedInstanceState
                    .getSerializable("selectedItemsList");
        }

        try {
            dbm.open(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Creates toolbar
        toolbar = (Toolbar) findViewById(R.id.main_menu);
        setSupportActionBar(toolbar);

        Button add_button = (Button) findViewById(R.id.add_button);
        add_button.setOnClickListener(this);

        Cursor cursor = dbm.getUserLists();

        adapter = new WordListsCursorAdapter(this, R.layout.list_item, cursor, 0);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        // TODO set empty listView.setEmptyView()
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    private void showEditToolbar() {
        currentMenu.clear();
        toolbar.setTitle("");
        getMenuInflater().inflate(R.menu.edit_menu, currentMenu);

        if (selectedItemsList.size() > 1) {
            currentMenu.findItem(R.id.share_button).setVisible(false);
            currentMenu.findItem(R.id.modify_button).setVisible(false);
        } else {
            currentMenu.findItem(R.id.share_button).setVisible(true);
            currentMenu.findItem(R.id.modify_button).setVisible(true);

        }

    }

    private void hideEditToolbar() {
        currentMenu.clear();
        toolbar.setTitle(R.string.app_name);
        getMenuInflater().inflate(R.menu.main_menu, currentMenu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                CMListDialog cmListDialog = new CMListDialog();
                cmListDialog.show(getFragmentManager(), "CMListDialog");

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = view.getContext();
        Intent intent = new Intent(context, ListActivity.class);
        intent.putExtra("id", id);

        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.return_button:
                selectedItemsList.clear();
                dataChange();
                hideEditToolbar();

                return true;

            case R.id.modify_button:
                // Edit button is only available if size of selectedItemsList is 1 thus first item
                // is to be edited
                Bundle bundle = new Bundle();
                bundle.putLong("id", selectedItemsList.get(0));

                CMListDialog cmListDialog = new CMListDialog();
                cmListDialog.setArguments(bundle);
                cmListDialog.show(getFragmentManager(), "CMListDialog");

                return true;

            case R.id.delete_button:
                // TODO add are you sure dialog

                for (long id: selectedItemsList) {
                    dbm.deleteList(id);
                }

                dataChange();
                return true;

            case R.id.share_button:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        }

        return true;
    }

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
            hideEditToolbar();
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        dbm.close();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedItemsList", selectedItemsList);
    }

    public void dataChange() {
        adapter.swapCursor(dbm.getUserLists());
        adapter.notifyDataSetChanged();
    }

    public ArrayList<Long> getSelectedItemsList() {
        return selectedItemsList;
    }
}

