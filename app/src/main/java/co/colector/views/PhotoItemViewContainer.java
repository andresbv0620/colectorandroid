package co.colector.views;

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
import co.colector.R;
import co.colector.listeners.OnAddPhotoListener;
import co.colector.model.IdValue;
import co.colector.model.Question;
import co.colector.model.AnswerValue;
import co.colector.model.QuestionVisibilityRules;
import io.realm.RealmList;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public class PhotoItemViewContainer extends LinearLayout {

    @BindView(R.id.container)
    LinearLayout container;
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
    private int sectionCount;

    private boolean isGoneByRules;
    public RealmList<QuestionVisibilityRules> getVisibilityRules() {
        return visibilityRules;
    }

    private RealmList<QuestionVisibilityRules> visibilityRules;

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
        container.setVisibility(!question.getValorVisibility().isEmpty() ? View.GONE : View.VISIBLE);
        isGoneByRules = question.getValorVisibility().isEmpty();
        visibilityRules = question.getValorVisibility();
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoItemViewContainer.this.callback.onAddPhotoClicked(PhotoItemViewContainer.this);
            }
        });
        if (previewDefault != null) for (String url : previewDefault) {
            if (!url.equals("")) addImages(url);
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

    public IdValue getResponses() {
        RealmList<AnswerValue> responses = new RealmList<>();
        if (photoContainer.getChildCount() > 0) {
            for (int itemViewIndex = 0; itemViewIndex < photoContainer.getChildCount(); itemViewIndex++)
                responses.add(new AnswerValue(((PhotoItemView) photoContainer.getChildAt(itemViewIndex)).url));
            return new IdValue(id, responses, validation, mType);
        } else return new IdValue(id, new RealmList<>(new AnswerValue("")), validation, mType);

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

    public void setVisibilityLabel(boolean isVisible) {
        container.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        container.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
        if(sectionCount%2==0)
        {
            this.setBackgroundColor(getContext().getResources().getColor(R.color.pair_option));
        }
        else
        {
            this.setBackgroundColor(getContext().getResources().getColor(R.color.odd_option));
        }
    }
}
