package colector.co.com.collector.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.listeners.CallDialogListener;
import colector.co.com.collector.model.IdOptionValue;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.Question;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class EditTextItemView extends FrameLayout {

    @BindView(R.id.label_edit_text)
    TextInputEditText label;
    @BindView(R.id.input_edit_text)
    TextInputLayout input;
    private Activity activity;
    private String validation;
    private Long id;
    private boolean required;
    private int mType;
    private List<IdOptionValue> response;

    public EditTextItemView(Context context) {
        super(context);
        init(context);
    }

    public EditTextItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditTextItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TextInputEditText getLabel() {
        return label;
    }

    public void setLabel(TextInputEditText label) {
        this.label = label;
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
            label.addTextChangedListener(new EditTextWatcher());
            input.setHint(activity.getString(R.string.required_field, question.getName()));
        }
    }

    /**
     * Bind the question info to the view
     *
     * @param question       to inflate
     * @param previewDefault information
     */
    public void bind(Question question, @Nullable String previewDefault) {
        initValues(question);
        if (previewDefault != null) label.setText(previewDefault);
        switch (question.getType()) {
            case 1:
                break;
            case 2:
                allowsMultilineEditText();
                break;
            case 8:
                label.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 15:
                label.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            default:
                input.setHint(activity.getString(R.string.type_default, String.valueOf(question.getType())));
                break;
        }
    }

    public void bind(final Question question, final List<IdOptionValue> response,
                     @Nullable String previewDefault) {
        initValues(question);
        this.response = response;
        if (previewDefault != null) label.setText(previewDefault);
        final CallDialogListener listener = (CallDialogListener) activity;
        label.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.callDialog(question.getName(), response, EditTextItemView.this, 0);
            }
        });
        label.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    listener.callDialog(question.getName(), response, EditTextItemView.this, 0);
            }
        });
    }

    private void allowsMultilineEditText() {
        label.setSingleLine(false);
        label.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        label.setMaxLines(2);
        label.setLines(2);
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
            requestFocus(label);
            return false;
        } else {
            input.setErrorEnabled(false);
            return true;
        }
    }

    public IdValue getResponse() {
        if (mType == 4) {
            return new IdValue(id, getResponseId(), validation, mType);
        } else
            return new IdValue(id, label.getText().toString(), validation, mType);
    }

    private
    @Nullable
    String getResponseId() {
        for (IdOptionValue item : response) {
            if (item.getValue().equals(label.getText().toString())) {
                return String.valueOf(item.getId());
            }
        }
        return null;
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

