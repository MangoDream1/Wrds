package nl.mprog.axel.wrds_programmeerproject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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

    public void open(Context context) throws SQLException {
        /* Open database from context */
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertList(String title, String desc, String creator,
                              String languageA, String languageB) {
        /* Inserts new list into database */

        ContentValues contentValues = createListContentValues(title, desc, creator,
                languageA, languageB);

        database.insert(DatabaseHelper.LIST_TABLE, null, contentValues);

    }

    public int updateList(long listId, String title, String desc, String creator,
                             String languageA, String languageB) {
        /* Updates values in a list using listId to find the list */

        ContentValues contentValues = createListContentValues(title, desc, creator,
                languageA, languageB);

        return database.update(DatabaseHelper.LIST_TABLE, contentValues,
                DatabaseHelper.PK_LIST_ID + " = " + listId, null);

    }

    public void deleteList(long listId) {
        /* Delete list from database */

        database.delete(DatabaseHelper.LIST_TABLE, DatabaseHelper.PK_LIST_ID + " = " + listId, null);

        // Also delete all words in list
        database.delete(DatabaseHelper.WORD_TABLE, DatabaseHelper.FK_LIST_ID + " = " + listId, null);
    }

    public Cursor getUserLists(int limit) {
        /* Get all lists with all the attributes of the user ordered by time */

        Cursor cursor = database.query(DatabaseHelper.LIST_TABLE, null, null,
                null, null, null, DatabaseHelper.DT_CREATED_AT, Integer.toString(limit));

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;

    }

    public Cursor getUserLists() {
        return getUserLists(10);
    }

    public Cursor getUserListTitle(long listId) {
        String[] columns = new String[]{DatabaseHelper.STR_TITLE,
                DatabaseHelper.STR_LANGUAGE_A, DatabaseHelper.STR_LANGUAGE_B};


        Cursor cursor = database.query(DatabaseHelper.LIST_TABLE, columns, DatabaseHelper.PK_LIST_ID
                + " = " + String.valueOf(listId), null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor getSingleList(long listId) {
        Cursor cursor = database.query(DatabaseHelper.LIST_TABLE, null,
                DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId), null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public long countListWords(long listId) {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.WORD_TABLE,
                DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId));
    }

    public Cursor getListWords(String where) {
        /* Get all the words of a list */
        String[] columns = new String[]{DatabaseHelper.PK_WORD_ID, DatabaseHelper.STR_WORD_A,
                DatabaseHelper.STR_WORD_B, DatabaseHelper.INT_TRIES};

        Cursor cursor = database.query(DatabaseHelper.WORD_TABLE, columns, where, null, null,
                null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor getListWords(long listId) {
        String where = DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId);

        return getListWords(where);
    }

    public Cursor getRetryWords(long listId) {
        String where = DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId) + " AND " +
                DatabaseHelper.INT_TRIES + " != 1";

        return getListWords(where);
    }

    private ContentValues createListContentValues(String title, String desc, String creator,
                                                  String languageA, String languageB) {
        /* Fills in the contentValues for lists in the database */

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.STR_TITLE, title);
        contentValues.put(DatabaseHelper.STR_DESC, desc);
        contentValues.put(DatabaseHelper.STR_CREATOR, creator);
        contentValues.put(DatabaseHelper.STR_LANGUAGE_A, languageA);
        contentValues.put(DatabaseHelper.STR_LANGUAGE_B, languageB);

        return contentValues;

    }

    public void insertWord(long listId, String wordA, String wordB) {
        /* Inserts new word in database connecting with correct list */

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.FK_LIST_ID, listId);
        contentValues.put(DatabaseHelper.STR_WORD_A, wordA);
        contentValues.put(DatabaseHelper.STR_WORD_B, wordB);

        database.insert(DatabaseHelper.WORD_TABLE, null, contentValues);

    }

    public int updateWord(long wordId, String wordA, String wordB) {
        /* Updates a word in the database */

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STR_WORD_A, wordA);
        contentValues.put(DatabaseHelper.STR_WORD_B, wordB);

        return database.update(DatabaseHelper.WORD_TABLE, contentValues,
                DatabaseHelper.PK_WORD_ID + " = " + wordId, null);

    }

    public void deleteWord(long wordId) {
        database.delete(DatabaseHelper.WORD_TABLE, DatabaseHelper.PK_WORD_ID + " = " + wordId, null);
    }

    public Cursor getSingleWord(long wordId, String[] columns ) {
        Cursor cursor = database.query(DatabaseHelper.WORD_TABLE, columns,
                DatabaseHelper.PK_WORD_ID + " = " + String.valueOf(wordId), null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor getSingleWord(long wordId) {
        /* Get all of single word */
        return getSingleWord(wordId, null);
    }

    public int incrementWordTry(long wordId) {
        Cursor cursor = getSingleWord(wordId, new String[]{DatabaseHelper.INT_TRIES});

        int tries = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.INT_TRIES));

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.INT_TRIES, tries + 1);

        return database.update(DatabaseHelper.WORD_TABLE, contentValues,
                DatabaseHelper.PK_WORD_ID + " = " + wordId, null);

    }

    public int resetWordTriesList(long listId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.INT_TRIES, 0);

        return database.update(DatabaseHelper.WORD_TABLE, contentValues,
                DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId), null);
    }

    public int getNumberOfMistakesList(long listId) {
        int sum = 0;
        int count = 0;

        Cursor sCursor = database.rawQuery("SELECT SUM(" + DatabaseHelper.INT_TRIES + ") FROM " +
                DatabaseHelper.WORD_TABLE + " WHERE " + DatabaseHelper.FK_LIST_ID + " = " +
                String.valueOf(listId) + " AND " + DatabaseHelper.INT_TRIES + " > 1", null);

        Cursor cCursor = database.query(DatabaseHelper.WORD_TABLE,
                new String[]{DatabaseHelper.INT_TRIES}, DatabaseHelper.FK_LIST_ID + " = " +
                        String.valueOf(listId) + " AND " + DatabaseHelper.INT_TRIES + " > 1",
                null, null, null, null);


        if (sCursor != null && cCursor != null) {
            sCursor.moveToFirst();

            sum = sCursor.getInt(0);
            count = cCursor.getCount();

            sCursor.close();
            cCursor.close();
        }

        return sum - count;
    }

    public int countNumberTriesList(long listId, int nTries) {
        int count = 0;

        Cursor cursor = database.rawQuery("SELECT COUNT(" + DatabaseHelper.INT_TRIES + ") FROM " +
                DatabaseHelper.WORD_TABLE + " WHERE " + DatabaseHelper.FK_LIST_ID + " = " +
                String.valueOf(listId) + " AND " + DatabaseHelper.INT_TRIES + " = " +
                String.valueOf(nTries), null);


        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }
}
