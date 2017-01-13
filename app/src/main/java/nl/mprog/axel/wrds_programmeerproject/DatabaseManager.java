package nl.mprog.axel.wrds_programmeerproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by axel on 10-1-17.
 */

public class DatabaseManager {
    private static DatabaseManager instance = null;

    private static DatabaseHelper dbHelper;
    private static SQLiteDatabase database;

    private DatabaseManager() {
        // Only exists to defeat instantiation
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    protected void open(Context context) throws SQLException {
        /* Open database from context */
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    protected void close() {
        dbHelper.close();
    }

    protected void insertList(String title, String desc, String creator,
                              String languageA, String languageB) {
        /* Inserts new list into database */

        ContentValues contentValues = createListContentValues(title, desc, creator,
                languageA, languageB);

        database.insert(DatabaseHelper.listTable, null, contentValues);

    }

    protected int updateList(long listId, String title, String desc, String creator,
                             String languageA, String languageB) {
        /* Updates values in a list using listId to find the list */

        ContentValues contentValues = createListContentValues(title, desc, creator,
                languageA, languageB);

        return database.update(DatabaseHelper.listTable, contentValues,
                DatabaseHelper.pk_listId + " = " + listId, null);

    }

    protected void deleteList(long listId) {
        /* Delete list from database */

        database.delete(DatabaseHelper.listTable, DatabaseHelper.pk_listId + " = " + listId, null);

        // Also delete all words in list
        database.delete(DatabaseHelper.wordTable, DatabaseHelper.fk_listId + " = " + listId, null);
    }

    protected Cursor getUserLists(int limit) {
        /* Get all lists with all the attributes of the user ordered by time */

        Cursor cursor = database.query(DatabaseHelper.listTable, null, null,
                null, null, null, DatabaseHelper.dt_createdAt, Integer.toString(limit));

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;

    }

    protected Cursor getUserLists() {
        return getUserLists(10);
    }

    protected Cursor getUserListTitle(long id) {
        String[] columns = new String[]{DatabaseHelper.str_title,
                DatabaseHelper.str_languageA, DatabaseHelper.str_languageB};


        Cursor cursor = database.query(DatabaseHelper.listTable, columns, DatabaseHelper.pk_listId
                + " = " + String.valueOf(id), null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }


    private ContentValues createListContentValues(String title, String desc, String creator,
                                                  String languageA, String languageB) {
        /* Fills in the contentValues for lists in the database */

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.str_title, title);
        contentValues.put(DatabaseHelper.str_desc, desc);
        contentValues.put(DatabaseHelper.str_creator, creator);
        contentValues.put(DatabaseHelper.str_languageA, languageA);
        contentValues.put(DatabaseHelper.str_languageB, languageB);

        return contentValues;

    }

    protected void insertWord(long listId, String wordA, String wordB) {
        /* Inserts new word in database connecting with correct list */

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.fk_listId, listId);
        contentValues.put(DatabaseHelper.str_wordA, wordA);
        contentValues.put(DatabaseHelper.str_wordB, wordB);

        database.insert(DatabaseHelper.wordTable, null, contentValues);

    }

    protected int updateWord(long wordId, String wordA, String wordB) {
        /* Updates a word in the database */

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.str_wordA, wordA);
        contentValues.put(DatabaseHelper.str_wordB, wordB);

        return database.update(DatabaseHelper.wordTable, contentValues,
                DatabaseHelper.pk_wordId + " = " + wordId, null);

    }

    protected void deleteWord(long wordId) {
        database.delete(DatabaseHelper.wordTable, DatabaseHelper.pk_wordId + " = " + wordId, null);
    }

    protected Cursor getListWords(long listId) {
        /* Get all the words of a list */

        String[] columns = new String[]{DatabaseHelper.pk_wordId, DatabaseHelper.str_wordA,
                DatabaseHelper.str_wordB};

        Cursor cursor = database.query(DatabaseHelper.wordTable, columns, DatabaseHelper.fk_listId
                        + " = " + String.valueOf(listId), null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;

    }
}