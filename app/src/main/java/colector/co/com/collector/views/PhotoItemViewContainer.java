package colector.co.com.collector.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.listeners.OnAddPhotoListener;
import colector.co.com.collector.model.Question;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public class PhotoItemViewContainer extends LinearLayout {

    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.photo_container)
    LinearLayout photoContainer;
    public Long id;

    public PhotoItemViewContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item_view_container, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(Question question, final OnAddPhotoListener callback) {
        id = question.getId();
        label.setText(question.getName());
        button.setText(question.getName());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAddPhotoClicked(PhotoItemViewContainer.this);
            }
        });
    }

    public void addImages(String url) {
        PhotoItemView photoItemView = new PhotoItemView(getContext());
        photoItemView.bind(url);
        photoContainer.addView(photoItemView);
    }
}
