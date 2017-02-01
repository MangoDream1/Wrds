package nl.mprog.axel.wrds_programmeerproject.Interfaces;

/**
 * Created by axel on 26-01-17.
 *
 * DefaultDialogInterface that handles callback of DefaultDialog
 *
 */

public interface DefaultDialogInterface {

    /**
     * If postive button is pressed this function is called
     * @param origin origin that handles which function should be run
     */
    void dialogPositive(String origin);
}
