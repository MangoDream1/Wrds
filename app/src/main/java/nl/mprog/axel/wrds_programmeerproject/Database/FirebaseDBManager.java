package nl.mprog.axel.wrds_programmeerproject.Database;

import android.database.Cursor;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by axel on 24-1-17.
 */

public class FirebaseDBManager {

    private static FirebaseDBManager instance = null;
    private static DatabaseManager dbm;
    private static FirebaseDatabase firebaseDB;

    private FirebaseDBManager() {
        // Only exists to defeat instantiation
    }

    public static FirebaseDBManager getInstance() {
        if (instance == null) {
            dbm = DatabaseManager.getInstance();
            firebaseDB = FirebaseDatabase.getInstance();

            instance = new FirebaseDBManager();
        }

        return instance;
    }

    public void createUser(String username, String id) {
        firebaseDB.getReference().child("users").child(id).setValue(username);
        firebaseDB.getReference().child("usernames").child(username).setValue(id);
    }

    public String uploadList(long listId, String userId) {
        String key = dbm.getFirebaseId(listId);

        if (key == null) {
            key = firebaseDB.getReference().child("lists").push().getKey();
        }

        uploadList(key, listId, userId);

        return key;
    }


    private void uploadList(final String key, final long listId, String userId) {
        firebaseDB.getReference().child("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            uploadList(listId, key, dataSnapshot.getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void uploadList(long listId, String key, String username) {
        HashMap<String, Object> data = createListHashTable(listId, username);
        firebaseDB.getReference().child("lists").child(key).setValue(data);
        dbm.updateFirebaseId(listId, key);
    }

    public void deleteList(String firebaseId) {
        firebaseDB.getReference().child("lists").child(firebaseId).removeValue();
    }

    private HashMap<String, Object> createListHashTable(long listId, String username) {
        Cursor lCursor = dbm.getSingleList(listId);
        Cursor wCursor = dbm.getListWords(listId);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("title", lCursor.getString(lCursor.getColumnIndex(DatabaseHelper.STR_TITLE)));
        hashMap.put("desc", lCursor.getString(lCursor.getColumnIndex(DatabaseHelper.STR_DESC)));
        hashMap.put("createdAt", lCursor.getString(
                lCursor.getColumnIndex(DatabaseHelper.DT_CREATED_AT)));
        hashMap.put("languageA", lCursor.getString(
                lCursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_A)));
        hashMap.put("languageB", lCursor.getString(
                lCursor.getColumnIndex(DatabaseHelper.STR_LANGUAGE_B)));
        hashMap.put("username", username);

        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();

        for (wCursor.moveToFirst(); !wCursor.isAfterLast(); wCursor.moveToNext()) {
            HashMap<String, String> word = new HashMap<>();

            word.put("wordA", wCursor.getString(wCursor.getColumnIndex(DatabaseHelper.STR_WORD_A)));
            word.put("wordB", wCursor.getString(wCursor.getColumnIndex(DatabaseHelper.STR_WORD_B)));

            wordList.add(word);
        }

        hashMap.put("words", wordList);

        return hashMap;
    }


}
