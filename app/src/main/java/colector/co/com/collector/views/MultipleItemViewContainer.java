package colector.co.com.collector.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.model.IdOptionValue;
import colector.co.com.collector.model.Question;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class MultipleItemViewContainer extends LinearLayout {

    @BindView(R.id.container)
    public LinearLayout container;
    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.show)
    TextView show;
    @BindView(R.id.collapse)
    TextView collapse;

    ArrayList<IdOptionValue> options = new ArrayList<>();
    boolean required = false;

    public MultipleItemViewContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.multiple_item_view_container, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(ArrayList<IdOptionValue> response, Question question) {
        if (response.size() == 0) return;
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
}
