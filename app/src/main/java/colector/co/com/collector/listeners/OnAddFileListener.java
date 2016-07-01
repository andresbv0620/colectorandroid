package colector.co.com.collector.listeners;

import android.widget.LinearLayout;

import colector.co.com.collector.views.FileItemViewContainer;
import colector.co.com.collector.views.PhotoItemView;
import colector.co.com.collector.views.PhotoItemViewContainer;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public interface OnAddFileListener {

    void onAddFileClicked(FileItemViewContainer container);

    void onFileClicked(LinearLayout container, PhotoItemView view);
}
