package colector.co.com.collector.listeners;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public interface OnDataBaseSave {

    /**
     * Data successfully saved
     */
    void onSuccess();

    /**
     * Error saving data
     */
    void onError();
}
