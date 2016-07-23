package co.colector.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.colector.R;
import co.colector.listeners.OnEditTextClickedOrFocused;
import co.colector.model.IdValue;
import co.colector.model.Question;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class EditTextDatePickerItemView extends FrameLayout {

    @BindView(R.id.label_edit_text)
    TextInputEditText label;
    @BindView(R.id.input_edit_text)
    TextInputLayout input;
    private Activity activity;
    private String validation;
    private Long id;
    private boolean required;
    private int mType;

    public TextInputEditText getLabel() {
        return label;
    }

    public EditTextDatePickerItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.edit_text_item_view, this, true);
        ButterKnife.bind(this, view);
        this.activity = (Activity) context;
    }

    private void initValues(Question question) {
        this.validation = question.getValidacion();
        this.id = question.getId();
        this.required = question.getRequerido();
        this.mType = question.getType();
        input.setHint(question.getName());
        if (question.getoculto()) this.setVisibility(GONE);
        if (required) {
            label.addTextChangedListener(new EditTextDatePickerItemView.EditTextWatcher());
            input.setHint(activity.getString(R.string.required_field, question.getName()));
        }
    }

    /**
     * Bind the question info to the view
     *
     * @param question       to inflate
     * @param previewDefault information
     * @param callback       to notify the activity to show the date picker
     */
    public void bind(Question question, @Nullable String previewDefault, final OnEditTextClickedOrFocused callback) {
        initValues(question);
        if (previewDefault != null) label.setText(previewDefault);
        label.setFocusable(false);
        label.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onEditTextAction(EditTextDatePickerItemView.this);
            }
        });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Validate if the edit text on the View is filled
     *
     * @return true if the Filed is filled
     */
    public boolean validateField() {
        if (!required) return true;
        if (label.getText().toString().trim().isEmpty()) {
            input.setError(activity.getString(R.string.required_error));
            if (mType != 4 || mType != 3)
                requestFocus(label);
            return false;
        } else {
            input.setErrorEnabled(false);
            return true;
        }
    }

    public IdValue getResponse() {
        return new IdValue(id, label.getText().toString(), validation, mType);
    }


    private class EditTextWatcher implements TextWatcher {

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            validateField();
        }
    }
}

