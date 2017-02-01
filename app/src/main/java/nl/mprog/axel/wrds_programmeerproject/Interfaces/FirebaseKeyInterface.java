package nl.mprog.axel.wrds_programmeerproject.Interfaces;

/**
 * Created by Axel on 27-01-17.
 *
 * FirebaseKeyInterface is a callback that is called when the Firebase list key is checked
 */

public interface FirebaseKeyInterface {

    /**
     * This method is called when the method is finished checking for the existence of the
     * firebase list key
     * @param firebaseId    firebase list id
     * @param keyExists     keyExists boolean
     */
    void keyStillExists(String firebaseId, boolean keyExists);
}
