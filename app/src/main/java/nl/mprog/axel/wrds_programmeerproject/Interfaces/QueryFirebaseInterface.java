package nl.mprog.axel.wrds_programmeerproject.Interfaces;

/**
 * Created by axel on 31-1-17.
 */

public interface QueryFirebaseInterface {

    void onDataChange(Object data, String key);

    void onCancelled();

}
