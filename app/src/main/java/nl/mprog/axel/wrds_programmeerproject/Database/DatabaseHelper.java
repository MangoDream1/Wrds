package nl.mprog.axel.wrds_programmeerproject.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by axel on 10-1-17.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "wrds.db";
    private static final int DB_VERSION = 5;

    // Table names
    public static final String LIST_TABLE = "ListTable";
    public static final String WORD_TABLE = "WordTable";

    // List table columns
    public static final String PK_LIST_ID = "_id";
    public static final String STR_TITLE = "title";
    public static final String STR_DESC = "desc";
    public static final String DT_CREATED_AT = "createdAt";
    public static final String STR_CREATOR = "creator";
    public static final String STR_LANGUAGE_A = "languageA";
    public static final String STR_LANGUAGE_B = "languageB";
    public static final String STR_FB_ID = "firebaseId";
    public static final String BOOL_IS_OWNER = "isOwner";

    // Word table columns
    public static final String PK_WORD_ID = "_id";
    public static final String FK_LIST_ID = "listId";
    public static final String STR_WORD_A = "wordA";
    public static final String STR_WORD_B = "wordB";
    public static final String INT_TRIES = "tries";

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create database queries
    private static final String CREATE_LIST_TABLE = "create table " + LIST_TABLE + " (" +
            PK_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            STR_TITLE + " TEXT NOT NULL, " +
            STR_DESC + " TEXT, " +
            DT_CREATED_AT + " DATETIME DEFAULT CURRENT_DATE, " +
            STR_CREATOR + " TEXT NOT NULL, " +
            STR_LANGUAGE_A + " TEXT NOT NULL, " +
            STR_LANGUAGE_B + " TEXT NOT NULL, " +
            STR_FB_ID + " TEXT DEFAULT NULL, " +
            BOOL_IS_OWNER +  " INTEGER DEFAULT 0);";

    private static final String CREATE_WORD_TABLE = "create table " + WORD_TABLE + " (" +
            PK_WORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FK_LIST_ID + " INTEGER NOT NULL, " +
            STR_WORD_A + " TEXT NOT NULL, " +
            STR_WORD_B + " TEXT NOT NULL, " +
            INT_TRIES + " INTEGER DEFAULT 0);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LIST_TABLE);
        db.execSQL(CREATE_WORD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + WORD_TABLE);
        onCreate(db);
    }
}