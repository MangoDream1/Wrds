package nl.mprog.axel.wrds_programmeerproject.Interfaces;

/**
 * Created by axel on 31-1-17.
 *
 * Firebase query interface
 *
 */

public interface QueryFirebaseInterface {

    /**
     * If data has changed in Firebase this function is called
     * @param data  new data
     * @param key   firebase key
     */
    void onDataChange(Object data, String key);

    /**
     * If there is an firebase error this function is called
     */
    void onCancelled();

}
