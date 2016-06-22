package colector.co.com.collector.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.Spinner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.model.IdOptionValue;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.Question;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class SpinnerItemView extends LinearLayout {

    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.spinner)
    Spinner spinner;
    private Long id;
    private String validation;
    private ArrayList<String> options = new ArrayList<>();
    private boolean required = false;

    public SpinnerItemView(Context context) {
        super(context);
        init(context);
    }

    public SpinnerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SpinnerItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item_view, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(ArrayList<IdOptionValue> responses, Question question, @Nullable String previewDefault) {
        this.id = question.getId();
        this.validation = question.getValidacion();
        if (responses.isEmpty()) return;
        required = question.getRequerido();
        for (IdOptionValue option : responses) options.add(option.getValue());
        spinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.row_spn, options));
        spinner.setSelection(options.indexOf(previewDefault));
        if (required) {
            label.setText(getContext().getString(R.string.required_field, question.getName()));
            setSpinnerListener();
        } else label.setText(question.getName());
        if (question.getoculto()) this.setVisibility(GONE);
    }

    public boolean validateField() {
        if (!required) return true;
        if (spinner.getSelectedItemPosition() != 0) {
            label.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
            return true;
        } else {
            label.setTextColor(ContextCompat.getColor(getContext(), R.color.red_label_error_color));
            return false;
        }
    }

    private void setSpinnerListener() {
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                validateField();
            }
        });
    }

    public IdValue getResponse() {
        return new IdValue(id, spinner.getSelectedItem().toString(), validation);
    }
}
