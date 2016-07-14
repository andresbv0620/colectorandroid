package co.colector.listeners;

import co.colector.views.EditTextDatePickerItemView;

/**
 * Created by gabriel on 29/06/16.
 */

public interface OnEditTextClickedOrFocused {

    /**
     * Ntify the activity to show the date Picker
     * @param view clicked
     */
    void onEditTextAction(EditTextDatePickerItemView view);
}
