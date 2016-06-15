package colector.co.com.collector.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.model.Question;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class EditTextItemView extends FrameLayout {

    @BindView(R.id.label_edit_text)
    EditText label;
    @BindView(R.id.input_edit_text)
    TextInputLayout input;
    Activity activity;

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

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.edit_text_item_view, this, true);
        ButterKnife.bind(this, view);
    }

    /**
     * Bind the question info to the view
     *
     * @param question       to inflate
     * @param previewDefault information
     * @param activity       where the view is Inflated
     */
    public void bind(Question question, @Nullable String previewDefault, Activity activity) {
        this.activity = activity;
        input.setHint(question.getName());
        if (previewDefault != null) label.setText(previewDefault);
        if (question.getoculto()) this.setVisibility(GONE);
        if (question.getRequerido()) label.addTextChangedListener(new EditTextWatcher());
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
                input.setHint(activity.getString(R.string.type_default, question.getType()));
                break;
        }
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

    public void validateField() {
        if (label.getText().toString().trim().isEmpty()) {
            input.setError(activity.getString(R.string.required_error));
            requestFocus(label);
        } else {
            input.setErrorEnabled(false);
        }
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

