package nl.mprog.axel.wrds_programmeerproject.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by axel on 10-1-17.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    static final String DB_NAME = "wrds.db";
    static final int DB_VERSION = 1;

    // Table names
    public static final String listTable = "ListTable";
    public static final String wordTable = "WordTable";

    // List table columns
    public static final String pk_listId = "_id";
    public static final String str_title = "title";
    public static final String str_desc = "desc";
    public static final String dt_createdAt = "createdAt";
    public static final String str_creator = "creator";
    public static final String str_languageA = "languageA";
    public static final String str_languageB = "languageB";

    // Word table columns
    public static final String pk_wordId = "_id";
    public static final String fk_listId = "list_id";
    public static final String str_wordA = "wordA";
    public static final String str_wordB = "wordB";
    public static final String int_mistakes = "mistakes";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create database queries
    public static final String createListTable = "create table " + listTable + " (" +
            pk_listId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            str_title + " TEXT NOT NULL, " +
            str_desc + " TEXT, " +
            dt_createdAt + " DATETIME DEFAULT CURRENT_DATE, " +
            str_creator + " TEXT NOT NULL, " +
            str_languageA + " TEXT NOT NULL, " +
            str_languageB + " TEXT NOT NULL);";

    public static final String createWordTable = "create table " + wordTable + " (" +
            pk_wordId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            fk_listId + " INTEGER NOT NULL, " +
            str_wordA + " TEXT NOT NULL, " +
            str_wordB + " TEXT NOT NULL, " +
            int_mistakes + "INTEGER DEFAULT 0);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createListTable);
        db.execSQL(createWordTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + listTable);
        db.execSQL("DROP TABLE IF EXISTS " + wordTable);
        onCreate(db);
    }
}