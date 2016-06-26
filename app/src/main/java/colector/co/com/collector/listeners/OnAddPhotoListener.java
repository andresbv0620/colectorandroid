package colector.co.com.collector.listeners;

import android.widget.LinearLayout;

import colector.co.com.collector.views.PhotoItemView;
import colector.co.com.collector.views.PhotoItemViewContainer;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public interface OnAddPhotoListener {

    void onAddPhotoClicked(PhotoItemViewContainer container);

    void onPhotoClicked(LinearLayout container, PhotoItemView view);
}
