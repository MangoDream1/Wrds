package nl.mprog.axel.wrds_programmeerproject.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by axel on 10-1-17.
 *
 * DatabaseManager handles all local SQL database functions. From insert, update, delete and query.
 *
 */

public class DatabaseManager {
    private static DatabaseManager instance = null;

    private static DatabaseHelper dbHelper;
    private static SQLiteDatabase database;

    private DatabaseManager() {
        // Only exists to defeat instantiation
    }

    /**
     * If instance exists return instance else create new and return
     * @return DatabaseManager instance
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    /**
     * Open database
     * @param context Context from where database is opened
     * @throws SQLException
     */
    public void open(Context context) throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Close database
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Inserts new list into database
     * @param title list title
     * @param desc list description
     * @param creator creator of list
     * @param languageA language A of list
     * @param languageB language A of list
     */
    public void insertList(String title, String desc, String creator,
                              String languageA, String languageB) {

        ContentValues contentValues = createListContentValues(title, desc, creator,
                languageA, languageB);

        // Creator is owner
        contentValues.put(DatabaseHelper.BOOL_IS_OWNER, 1);

        database.insert(DatabaseHelper.LIST_TABLE, null, contentValues);

    }

    /**
     * Updates values in a list using listId to find the list
     * @param listId the id of the list that is updated
     * @param title new title
     * @param desc new desc
     * @param creator new creator
     * @param languageA new language A
     * @param languageB new language B
     * @return Amount of rows affected
     */
    public int updateList(long listId, String title, String desc, String creator,
                             String languageA, String languageB) {

        ContentValues contentValues = createListContentValues(title, desc, creator,
                languageA, languageB);

        return database.update(DatabaseHelper.LIST_TABLE, contentValues,
                DatabaseHelper.PK_LIST_ID + " = " + listId, null);

    }

    /**
     * Fills in the contentValues for lists in the database
     * @param title list title
     * @param desc list desc
     * @param creator creator of list
     * @param languageA language A of list
     * @param languageB language B of list
     * @return ContentValues filled with params
     */
    private ContentValues createListContentValues(String title, String desc, String creator,
                                                  String languageA, String languageB) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.STR_TITLE, title);
        contentValues.put(DatabaseHelper.STR_DESC, desc);
        contentValues.put(DatabaseHelper.STR_CREATOR, creator);
        contentValues.put(DatabaseHelper.STR_LANGUAGE_A, languageA);
        contentValues.put(DatabaseHelper.STR_LANGUAGE_B, languageB);

        return contentValues;

    }

    /**
     * Delete list from database
     * @param listId the id of list
     */
    public void deleteList(long listId) {
        // Find firebaseId and then delete from firebase
        String[] columns = new String[]{DatabaseHelper.STR_FB_ID, DatabaseHelper.BOOL_IS_OWNER};
        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        Cursor cursor = queryListTable(columns, where);
        String firebaseId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_FB_ID));

        // Delete from firebase if the user is the owner
        if (isListOwner(listId) && firebaseId != null) {
            FirebaseDBManager.getInstance().deleteList(listId, firebaseId);
        }

        // Delete list
        database.delete(DatabaseHelper.LIST_TABLE, DatabaseHelper.PK_LIST_ID + " = " + listId, null);

        // Also delete all words in list
        database.delete(DatabaseHelper.WORD_TABLE, DatabaseHelper.FK_LIST_ID + " = " + listId, null);
    }

    /**
     * Queries a table
     * @param table table name
     * @param columns columns to be queried
     * @param where where part of query
     * @param sortedOn on what result is sorted
     * @return cursor with results moved to first if not null
     */
    private Cursor query(String table, String[] columns, String where, String sortedOn) {
        Cursor cursor = database.query(table, columns, where, null, null, null, sortedOn, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    /**
     * Query list table with variables defined by columns, cells defined by where and sorted on
     * defined by sortedOn
     * @param columns columns
     * @param where where
     * @param sortedOn sortedOn
     * @return cursor with values defined by columns, cells by where and sorted on by sortedOn
     */
    private Cursor queryListTable(String[] columns, String where, String sortedOn) {
        return query(DatabaseHelper.LIST_TABLE, columns, where, sortedOn);
    }

    /**
     * Query list table with variables defined by columns, cells defined by where
     * @param columns columns
     * @param where where
     * @return cursor with values defined by columns, cells by where and sorted on by sortedOn
     */
    private Cursor queryListTable(String[] columns, String where) {
        return queryListTable(columns, where, null);
    }

    /**
     * Query list table with cells defined by where
     * @param where where
     * @return cursor with values defined by where
     */
    private Cursor queryListTable(String where) {
        return queryListTable(null, where);
    }

    /**
     * Query list table to find title, lan A and lan B for creation title
     * @param listId id of list
     * @return cursor with query results
     */
    public Cursor getUserListTitle(long listId) {
        String[] columns = new String[]{DatabaseHelper.STR_TITLE,
                DatabaseHelper.STR_LANGUAGE_A, DatabaseHelper.STR_LANGUAGE_B};

        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        return queryListTable(columns, where);
    }

    /**
     * Query all data of one list
     * @param listId id of list
     * @return cursor with query result with all variables of one list
     */
    public Cursor getSingleList(long listId) {
        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        return queryListTable(where);
    }

    /**
     * Count the amount of words in list
     * @param listId id of list
     * @return amount of words in list
     */
    public long countListWords(long listId) {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.WORD_TABLE,
                DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId));
    }


    /**
     * Get all lists with all the attributes in the table ordered by time
     * @return cursor with all lists and all attributes
     */
    public Cursor getUserLists() {
        return queryListTable(null, null, DatabaseHelper.DT_CREATED_AT);

    }

    /**
     * Checks if user is list owner
     * @param listId id of list
     * @return boolean if the user is the list owner
     */
    public boolean isListOwner(long listId) {
        String[] columns = new String[]{DatabaseHelper.BOOL_IS_OWNER};
        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        Cursor cursor = queryListTable(columns, where);

        return cursor.getInt(cursor.getColumnIndex(DatabaseHelper.BOOL_IS_OWNER)) == 1;
    }

    /**
     * Insert a word into word table
     * @param listId id of list where the word goes into
     * @param wordA wordA
     * @param wordB wordB
     */
    public void insertWord(long listId, String wordA, String wordB) {
        /* Inserts new word in database connecting with correct list */

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.FK_LIST_ID, listId);
        contentValues.put(DatabaseHelper.STR_WORD_A, wordA);
        contentValues.put(DatabaseHelper.STR_WORD_B, wordB);

        database.insert(DatabaseHelper.WORD_TABLE, null, contentValues);

    }

    /**
     * Updates a word
     * @param wordId id of word
     * @param wordA new wordA
     * @param wordB new wordB
     * @return number of columns affected
     */
    public int updateWord(long wordId, String wordA, String wordB) {
        /* Updates a word in the database */

        ContentValues contentValues = new ContentValues();

        contentValues.put(DatabaseHelper.STR_WORD_A, wordA);
        contentValues.put(DatabaseHelper.STR_WORD_B, wordB);

        return database.update(DatabaseHelper.WORD_TABLE, contentValues,
                DatabaseHelper.PK_WORD_ID + " = " + wordId, null);

    }

    /**
     * Delete word
     * @param wordId id of word
     */
    public void deleteWord(long wordId) {
        database.delete(DatabaseHelper.WORD_TABLE, DatabaseHelper.PK_WORD_ID + " = " + wordId, null);
    }

    /**
     * Query the word table returning columns and from where
     * @param columns columns
     * @param where where
     * @return cursor with variables defined by columns and cells defined by where
     */
    private Cursor queryWordTable(String[] columns, String where) {
        return query(DatabaseHelper.WORD_TABLE, columns, where, null);
    }

    /**
     * Get words defined by where
     * @param where where
     * @return cursor with words defined by where
     */
    private Cursor getListWords(String where) {
        /* Get all the words of a list */
        String[] columns = new String[]{DatabaseHelper.PK_WORD_ID, DatabaseHelper.STR_WORD_A,
                DatabaseHelper.STR_WORD_B, DatabaseHelper.INT_TRIES};

        return queryWordTable(columns, where);
    }

    /**
     * Get all words of list
     * @param listId id of list
     * @return cursor with all words in list
     */
    public Cursor getListWords(long listId) {
        String where = DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId);

        return getListWords(where);
    }

    /**
     * Get the words for retry per list
     * @param listId id of list
     * @return cursor with all word that need to be retried
     */
    public Cursor getRetryWords(long listId) {
        String where = DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId) + " AND " +
                DatabaseHelper.INT_TRIES + " != 1";

        return getListWords(where);
    }

    /**
     * Return single word
     * @param wordId id of word
     * @param columns columns queried
     * @return cursor with single word with variables defined in columns
     */
    private Cursor getSingleWord(long wordId, String[] columns ) {
        String where = DatabaseHelper.PK_WORD_ID + " = " + String.valueOf(wordId);

        return queryWordTable(columns, where);
    }

    /**
     * Return single word with all variables
     * @param wordId id of word
     * @return cursor with single word with all variables
     */
    public Cursor getSingleWord(long wordId) {
        /* Get all of single word */
        return getSingleWord(wordId, null);
    }

    /**
     * Increment word try
     * @param wordId id of word
     * @return amount of rows affected
     */
    public int incrementWordTry(long wordId) {
        Cursor cursor = getSingleWord(wordId, new String[]{DatabaseHelper.INT_TRIES});

        int tries = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.INT_TRIES));

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.INT_TRIES, tries + 1);

        return database.update(DatabaseHelper.WORD_TABLE, contentValues,
                DatabaseHelper.PK_WORD_ID + " = " + wordId, null);

    }

    /**
     * Reset the word tries of words defined in where
     * @param where which words to be reset
     * @return amount of rows affected
     */
    private int resetWordTries(String where) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.INT_TRIES, 0);

        return database.update(DatabaseHelper.WORD_TABLE, contentValues,
                where, null);
    }

    /**
     * Reset the word tries of words in list. If isRetry then only reset words
     * that need to be retried
     * @param listId id of list
     * @param isRetry isRetry boolean
     * @return amount of row affected
     */
    public int resetWordTries(long listId, boolean isRetry) {
        String where = DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId);

        if (isRetry) {
            where = where + " AND " + DatabaseHelper.INT_TRIES + " > 1 ";
        }

        return resetWordTries(where);
    }

    /**
     * Count the number of mistakes made after exma
     * @param listId id of list
     * @return number of mistakes
     */
    public int getNumberOfMistakes(long listId) {
        int count = 0;

        String where = DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId) +
                " AND " + DatabaseHelper.INT_TRIES + " != 1";

        String[] columns = new String[]{DatabaseHelper.INT_TRIES};

        Cursor cursor = queryWordTable(columns, where);

        if (cursor != null) {
            count = cursor.getCount();
        }

        return count;
    }

    /**
     * Count number of tries given by nTries
     * @param listId id of list
     * @param nTries the number of tries to be counted
     * @return count of the number of tries of nTries
     */
    public int countNumberTries(long listId, int nTries) {
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

    /**
     * Find the max tries
     * @param listId id of list
     * @return the max tries of list
     */
    public int getHighestTries(long listId) {
        int max = 0;

        Cursor cursor = database.rawQuery("SELECT MAX(" + DatabaseHelper.INT_TRIES + ") FROM " +
                DatabaseHelper.WORD_TABLE + " WHERE " + DatabaseHelper.FK_LIST_ID + " = " +
                String.valueOf(listId), null);

        if (cursor != null) {
            cursor.moveToFirst();
            max = cursor.getInt(0);
            cursor.close();
        }

        return max;
    }

    /**
     * Insert from database into firebase
     * @param map data from firebase
     * @param firebaseId firebaseId
     */
    public void insertFromFirebase(Map<String, Object> map, String firebaseId) {
        ContentValues contentValues = createListContentValues(
                (String) map.get("title"),
                (String) map.get("desc"),
                (String) map.get("username"),
                (String) map.get("languageA"),
                (String) map.get("languageB"));

        contentValues.put(DatabaseHelper.DT_CREATED_AT, (String) map.get("createdAt"));
        contentValues.put(DatabaseHelper.STR_FB_ID, firebaseId);

        long listId = database.insert(DatabaseHelper.LIST_TABLE, null, contentValues);

        if (map.containsKey("words")) {
            ArrayList<Map<String, String>> wordList =
                    (ArrayList<Map<String, String>>) map.get("words");

            for (Map<String, String> words: wordList) {
                insertWord(listId, words.get("wordA"), words.get("wordB"));
            }
        };
    }

    /**
     * Get Firebase id from database
     * @param listId id of list
     * @return Firebase id of list
     */
    public String getFirebaseId(long listId) {

        String[] columns = new String[]{DatabaseHelper.STR_FB_ID};
        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        Cursor cursor = queryListTable(columns, where);

        return cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_FB_ID));
    }

    /**
     * Update Firebase id
     * @param listId id of list
     * @param firebaseId Firebase id
     * @return amount of rows affected
     */
    int updateFirebaseId(long listId, String firebaseId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STR_FB_ID, firebaseId);

        return database.update(DatabaseHelper.LIST_TABLE, contentValues,
                DatabaseHelper.PK_LIST_ID + " = " + listId, null);
    }
}
