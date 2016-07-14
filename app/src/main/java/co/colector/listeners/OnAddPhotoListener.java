package co.colector.listeners;

import android.widget.LinearLayout;

import co.colector.views.PhotoItemView;
import co.colector.views.PhotoItemViewContainer;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public interface OnAddPhotoListener {

    void onAddPhotoClicked(PhotoItemViewContainer container);

    void onPhotoClicked(LinearLayout container, PhotoItemView view);
}
