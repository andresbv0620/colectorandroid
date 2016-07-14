package co.colector.listeners;

import java.util.List;

import co.colector.model.IdOptionValue;

/**
 * Created by danielsierraf on 6/27/16.
 */
public interface CallDialogListener {
    /**
     *
     * @param title Title of the dialog
     * @param response data for the list of items
     * @param parent object that called the dialog
     * @param type Single-option dialog or multiple-option
     */
    void callDialog(String title, List<IdOptionValue> response, Object parent, int type);
}