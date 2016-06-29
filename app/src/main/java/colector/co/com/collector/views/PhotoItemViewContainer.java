package colector.co.com.collector.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.listeners.OnAddPhotoListener;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.Question;
import io.realm.RealmList;

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
    private String validation;
    private OnAddPhotoListener callback;
    private boolean required;
    private int mType;

    public PhotoItemViewContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item_view_container, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(Question question, OnAddPhotoListener callback, @Nullable List<String> previewDefault) {
        this.callback = callback;
        id = question.getId();
        required = question.getRequerido();
        mType = question.getType();
        validation = question.getValidacion();
        label.setText(question.getName());
        button.setText(question.getName());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoItemViewContainer.this.callback.onAddPhotoClicked(PhotoItemViewContainer.this);
            }
        });
        if (previewDefault != null) for (String url : previewDefault) {
            addImages(url);
        }
    }

    public void addImages(String url) {
        final PhotoItemView photoItemView = new PhotoItemView(getContext());
        photoItemView.bind(url);
        photoItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPhotoClicked(photoContainer, (PhotoItemView) v);
            }
        });
        photoContainer.addView(photoItemView);
    }

    public RealmList<IdValue> getResponses() {
        RealmList<IdValue> responses = new RealmList<>();
        for (int itemViewIndex = 0; itemViewIndex < photoContainer.getChildCount(); itemViewIndex++) {
            PhotoItemView itemView = (PhotoItemView) photoContainer.getChildAt(itemViewIndex);
            responses.add(new IdValue(id, itemView.url, validation, mType));
        }
        return responses;
    }

    public boolean validateFields() {
        if (!required) return true;
        if (photoContainer.getChildCount() > 0) {
            label.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
            return true;
        }
        label.setTextColor(ContextCompat.getColor(getContext(), R.color.red_label_error_color));
        return false;
    }

}
