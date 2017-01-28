package nl.mprog.axel.wrds_programmeerproject.Database;

import android.database.Cursor;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import nl.mprog.axel.wrds_programmeerproject.Interfaces.FirebaseKeyInterface;

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

    public void listIdExists(final String firebaseId, final Object callback) {
        firebaseDB.getReference().child("lists").child(firebaseId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((FirebaseKeyInterface) callback)
                                .keyStillExists(firebaseId, dataSnapshot.hasChild("deletedOn"));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public String uploadList(long listId, String userId) {
        String key = dbm.getFirebaseId(listId);

        //TODO create small key

        if (key == null) {
            key = firebaseDB.getReference().child("lists").push().getKey();
            dbm.updateFirebaseId(listId, key);
        }

        if (dbm.isListOwner(listId)) {
            uploadList(key, listId, userId);
        }

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
    }

    public void deleteList(long listId, String firebaseId) {
        dbm.updateFirebaseId(listId, null);

        firebaseDB.getReference().child("lists").child(firebaseId).removeValue();

        // Keep id in list in the case it gets reused and others will share wrong lists
        // Save date deletion so that old lists can be deleted at a later stage
        firebaseDB.getReference().child("lists").child(firebaseId)
                .child("deletedOn").setValue(ServerValue.TIMESTAMP);

    }

    private HashMap<String, Object> createListHashTable(long listId, String username) {
        Cursor lCursor = dbm.getSingleList(listId);

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

        return addWordsHashMap(listId, hashMap);
    }

    private HashMap<String, Object> addWordsHashMap(long listId, HashMap<String, Object> hashMap) {
        Cursor wCursor = dbm.getListWords(listId);

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
