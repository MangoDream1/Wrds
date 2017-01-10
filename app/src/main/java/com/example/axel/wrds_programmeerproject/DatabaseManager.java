package com.example.axel.wrds_programmeerproject;

import android.content.Context;
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
        // Open database from context
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    protected void close() {
        dbHelper.close();
    }

    protected void insertLists(String title, String desc, String creator,
                               String languageA, String languageB) {

    }

    protected void insertWord(String wordA, String wordB) {

    }

}
