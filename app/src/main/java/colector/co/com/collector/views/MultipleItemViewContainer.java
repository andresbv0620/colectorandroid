package colector.co.com.collector.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.model.IdOptionValue;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.Question;
import io.realm.RealmList;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class MultipleItemViewContainer extends LinearLayout {

    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.show)
    TextView show;
    @BindView(R.id.collapse)
    TextView collapse;
    private Long id;
    private String validation;

    private ArrayList<IdOptionValue> options = new ArrayList<>();
    private boolean required = false;

    public MultipleItemViewContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.multiple_item_view_container, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(ArrayList<IdOptionValue> response, Question question,
                     @Nullable List<String> previewDefault) {
        if (response.isEmpty()) return;
        this.id = question.getId();
        this.validation = question.getValidacion();
        required = question.getRequerido();
        this.options = response;
        // Bind the items
        for (IdOptionValue option : options) {
            MultipleItemView multipleItemView = new MultipleItemView(getContext());
            multipleItemView.bind(option);
            container.addView(multipleItemView);
        }
        //Bind the title
        if (required) {
            label.setText(getContext().getString(R.string.required_field, question.getName()));
        } else label.setText(question.getName());
        //Bind the show and hide buttons
        bindShowButton();
        bindCollapseButton(collapse);
        if (previewDefault != null) {
            bindDefaultSelected(previewDefault);
        }
        if (question.getoculto()) this.setVisibility(GONE);
    }

    private void bindShowButton() {
        show.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText(getContext().getString(R.string.hide));
                bindCollapseButton(show);
                collapse.setVisibility(VISIBLE);
                container.setVisibility(VISIBLE);
            }
        });
    }

    private void bindCollapseButton(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show.setText(getContext().getString(R.string.show));
                bindShowButton();
                collapse.setVisibility(GONE);
                container.setVisibility(GONE);
            }
        });
    }

    public boolean validateFields() {
        if (!required) return true;
        for (IdOptionValue option : options) {
            if (option.isStatus()) {
                label.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
                return true;
            }
        }
        label.setTextColor(ContextCompat.getColor(getContext(), R.color.red_label_error_color));
        return false;
    }

    public RealmList<IdValue> getResponses() {
        RealmList<IdValue> responses = new RealmList<>();
        for (int itemViewIndex = 0; itemViewIndex < container.getChildCount(); itemViewIndex++) {
            MultipleItemView itemView = (MultipleItemView) container.getChildAt(itemViewIndex);
            if (itemView.toggle.isChecked()) {
                responses.add(new IdValue(id, itemView.textView.getText().toString(), validation));
            }
        }
        return responses;
    }

    private void bindDefaultSelected(List<String> previewDefault) {
        for (int itemViewIndex = 0; itemViewIndex < container.getChildCount(); itemViewIndex++) {
            MultipleItemView itemView = (MultipleItemView) container.getChildAt(itemViewIndex);
            for (String defaultValue : previewDefault) {
                if (itemView.textView.getText().toString().equals(defaultValue)) {
                    options.get(itemViewIndex).setStatus(true);
                    itemView.toggle.setChecked(true);
                }
            }
        }
    }
}
