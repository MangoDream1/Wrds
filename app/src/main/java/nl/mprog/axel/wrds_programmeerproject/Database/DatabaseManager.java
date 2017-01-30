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

        // Creator is owner
        contentValues.put(DatabaseHelper.BOOL_IS_OWNER, 1);

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

    public void deleteList(long listId) {
        /* Delete list from database */

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

    private Cursor query(String table, String[] columns, String where, String sortedOn) {
        Cursor cursor = database.query(table, columns, where, null, null, null, sortedOn, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    private Cursor queryListTable(String[] columns, String where, String sortedOn) {
        return query(DatabaseHelper.LIST_TABLE, columns, where, sortedOn);
    }

    private Cursor queryListTable(String[] columns, String where) {
        return queryListTable(columns, where, null);
    }

    private Cursor queryListTable(String where) {
        return queryListTable(null, where);
    }

    public Cursor getUserListTitle(long listId) {
        String[] columns = new String[]{DatabaseHelper.STR_TITLE,
                DatabaseHelper.STR_LANGUAGE_A, DatabaseHelper.STR_LANGUAGE_B};

        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        return queryListTable(columns, where);
    }

    public Cursor getSingleList(long listId) {
        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        return queryListTable(where);
    }

    public long countListWords(long listId) {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.WORD_TABLE,
                DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId));
    }

    public Cursor getUserLists() {
        /* Get all lists with all the attributes of the user ordered by time */
        return queryListTable(null, null, DatabaseHelper.DT_CREATED_AT);

    }

    public boolean isListOwner(long listId) {
        String[] columns = new String[]{DatabaseHelper.BOOL_IS_OWNER};
        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        Cursor cursor = queryListTable(columns, where);

        return cursor.getInt(cursor.getColumnIndex(DatabaseHelper.BOOL_IS_OWNER)) == 1;
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


    public Cursor queryWordTable(String[] columns, String where) {
        return query(DatabaseHelper.WORD_TABLE, columns, where, null);
    }

    public Cursor getListWords(String where) {
        /* Get all the words of a list */
        String[] columns = new String[]{DatabaseHelper.PK_WORD_ID, DatabaseHelper.STR_WORD_A,
                DatabaseHelper.STR_WORD_B, DatabaseHelper.INT_TRIES};

        return queryWordTable(columns, where);
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

    public Cursor getSingleWord(long wordId, String[] columns ) {
        String where = DatabaseHelper.PK_WORD_ID + " = " + String.valueOf(wordId);

        return queryWordTable(columns, where);
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

    private int resetWordTries(String where) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.INT_TRIES, 0);

        return database.update(DatabaseHelper.WORD_TABLE, contentValues,
                where, null);
    }

    public int resetWordTries(long listId, boolean isReset) {
        String where = DatabaseHelper.FK_LIST_ID + " = " + String.valueOf(listId);

        if (isReset) {
            where = where + " AND " + DatabaseHelper.INT_TRIES + " > 1 ";
        }

        return resetWordTries(where);
    }

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

    public String getFirebaseId(long listId) {

        String[] columns = new String[]{DatabaseHelper.STR_FB_ID};
        String where = DatabaseHelper.PK_LIST_ID + " = " + String.valueOf(listId);

        Cursor cursor = queryListTable(columns, where);

        return cursor.getString(cursor.getColumnIndex(DatabaseHelper.STR_FB_ID));
    }

    int updateFirebaseId(long listId, String firebaseId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STR_FB_ID, firebaseId);

        return database.update(DatabaseHelper.LIST_TABLE, contentValues,
                DatabaseHelper.PK_LIST_ID + " = " + listId, null);
    }
}
