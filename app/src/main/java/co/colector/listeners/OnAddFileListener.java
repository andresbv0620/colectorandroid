package co.colector.listeners;

import android.widget.LinearLayout;

import co.colector.views.FileItemViewContainer;
import co.colector.views.PhotoItemView;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public interface OnAddFileListener {

    void onAddFileClicked(FileItemViewContainer container);

    void onFileClicked(LinearLayout container, PhotoItemView view);
}
