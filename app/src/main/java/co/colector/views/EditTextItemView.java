package co.colector.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.colector.R;
import co.colector.database.DatabaseHelper;
import co.colector.listeners.CallDialogListener;
import co.colector.model.IdOptionValue;
import co.colector.model.IdValue;
import co.colector.model.Question;
import co.colector.model.QuestionVisibilityRules;
import co.colector.model.AnswerValue;
import co.colector.model.ResponseComplex;
import co.colector.session.AppSession;
import io.realm.RealmList;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class EditTextItemView extends FrameLayout {

    @BindView(R.id.label_edit_text)
    TextInputEditText label;
    @BindView(R.id.input_edit_text)
    TextInputLayout input;
    //    Patch for multiple lines hint
    @BindView(R.id.text_view_hint)
    TextView alternativeTextHint;
    private Activity activity;
    private String validation;
    private Long id;
    private boolean required;
    private int mType;
    private List<IdOptionValue> response;
    private List<IdOptionValue> filtered_responses;
    private boolean alreadyShow;
    private boolean isGoneByRules;
    private Question question;
    private SectionItemView sectionItemView;
    private String defaultValue;
    private int sectionCount;
    private int sectionIndex;

    public Question getQuestion(){
        return question;
    }

    public RealmList<QuestionVisibilityRules> getVisibilityRules() {
        return visibilityRules;
    }

    private RealmList<QuestionVisibilityRules> visibilityRules;

    public EditTextItemView(Context context, SectionItemView sectionItemView) {
        super(context);
        this.sectionItemView = sectionItemView;
        init(context);
    }

    public Long getIdentifier() {
        return id;
    }

    public TextInputEditText getLabel() {
        return label;
    }

    public int getType(){ return mType; }

    public void setLabel(TextInputEditText label) {
        this.label = label;
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.edit_text_item_view, this, true);
        ButterKnife.bind(this, view);
        this.activity = (Activity) context;
    }

    private void initValues(Question question) {
        this.question = question;
        this.validation = question.getValidacion();
        this.id = question.getId();
        this.required = question.getRequerido();
        this.mType = question.getType();
        String hint = question.getName();
        if(hint.length() < 60)
        {
            input.setHint(hint);
        }
        else
        {
            alternativeTextHint.setText(hint);
        }
        if (question.getoculto()) this.setVisibility(GONE);
        if (required) {
            label.addTextChangedListener(new EditTextWatcher(activity, question.getId(), sectionItemView));
//            input.setHint(activity.getString(R.string.required_field, question.getName()));
            if(hint.length() < 60)
            {
                input.setHint(activity.getString(R.string.required_field, question.getName()));
            }
            else
            {
                alternativeTextHint.setText(activity.getString(R.string.required_field, question.getName()));
            }
        }
    }

    /**
     * Bind the question info to the view, Called on Text Questions
     *
     * @param question       to inflate
     * @param previewDefault information
     */
    public void bind(Question question, @Nullable String previewDefault) {
        initValues(question);
        if (Boolean.parseBoolean(question.getSoloLectura()))
            label.setEnabled(false);
        if (previewDefault != null) label.setText(previewDefault);
        else if (question.getDefecto() != null && !question.getDefecto().equals(""))
            label.setText(question.getDefecto());
        label.setVisibility(!question.getValorVisibility().isEmpty() ? View.GONE : View.VISIBLE);
        isGoneByRules = question.getValorVisibility().isEmpty();
        visibilityRules = question.getValorVisibility();
        switch (question.getType()) {
            case 1:
                setLenghtOfEditText(question, false);
                break;
            case 2:
                allowsMultilineEditText();
                break;
            case 8:
                label.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                setLenghtOfEditText(question,true);
                break;
            case 15:
                label.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                setLenghtOfEditText(question, true);
                break;
            default:
//                input.setHint(activity.getString(R.string.type_default, String.valueOf(question.getType())));
                String hint = String.valueOf(question.getType());
                if(hint.length() < 60)
                {
                    input.setHint(activity.getString(R.string.required_field, question.getName()));
                }
                else
                {
                    alternativeTextHint.setText(activity.getString(R.string.required_field, question.getName()));
                }
                break;
        }
        label.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Click Sec Index", sectionIndex+"");
            }
        });
    }

    /**
     * Called on Spinner Questions
     * @param question Question to bind
     * @param response List of Responses
     * @param previewDefault Text to preview
     */
    public void bind(
            final Question question,
            final List<IdOptionValue> response,
            @Nullable String previewDefault
    )
    {
        initValues(question);
        if (Boolean.parseBoolean(question.getSoloLectura()))
            label.setEnabled(false);
        this.response = response;
        this.filtered_responses = response;
        if (previewDefault != null)
            if (mType != 4) {
                label.setText(previewDefault);
            } else {
                label.setText(getResponseValue(previewDefault));
        }
        else {
            if (question.getDefecto() != null && !question.getDefecto().isEmpty()) {
                label.setFocusable(false);
                defaultValue = !question.getDefecto().isEmpty() ? question.getDefecto() : "";
                label.setText(defaultValue);
            }
        }
        label.setVisibility(!question.getValorVisibility().isEmpty() ? View.GONE : View.VISIBLE);
        isGoneByRules = question.getValorVisibility().isEmpty();
        visibilityRules = question.getValorVisibility();
        final CallDialogListener listener = (CallDialogListener) activity;
        label.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.callDialog(question.getName(), EditTextItemView.this.filtered_responses, EditTextItemView.this, 0, sectionItemView, defaultValue);
            }
        });
        label.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !alreadyShow) {
                    alreadyShow = true;
                    listener.callDialog(question.getName(), EditTextItemView.this.filtered_responses, EditTextItemView.this, 0, sectionItemView, defaultValue);
                }
            }
        });
    }

    /**
     * Called on dynamic Questions
     * @param question
     */
    public void bind(final Question question){
        this.question = question;
        this.validation = question.getValidacion();
        this.id = question.getId();
        this.required = question.getRequerido();
        this.mType = question.getType();
        final CallDialogListener listener = (CallDialogListener) activity;
        label.setFocusable(false);
        String hint = question.getName();
        if(hint.length() < 60)
        {
            label.setHint(hint);
        }
        else
        {
            alternativeTextHint.setText(hint);
        }
        label.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.callDynamicDialog(question.getName(), question, EditTextItemView.this);
            }
        });
    }

    public void removeFocusability() {
        label.setFocusable(false);
    }

    private void allowsMultilineEditText() {
        label.setSingleLine(false);
        label.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        label.setMaxLines(2);
        label.setLines(2);
        label.setHeight(200);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setLenghtOfEditText(Question question, boolean isNumeric){
        if (question.getMax() != null && !question.getMax().isEmpty()){
            InputFilter[] filterArray = new InputFilter[1];
            if (!isNumeric)
                filterArray[0] = new InputFilter.LengthFilter(Integer.valueOf(question.getMax()));
            else
                filterArray[0] = new InputFilter.LengthFilter(Integer.valueOf(question.getMax().length()));
            label.setFilters(filterArray);
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
            if (mType != 4 && mType != 3)
                requestFocus(label);
            return false;
        } else {
            if (mType == 1 || mType == 2) {
                if (question.getMin() != null && question.getMax() != null) {
                    if (label.getText().toString().length() >= Integer.parseInt(question.getMin()) &&
                            label.getText().toString().length() <= Integer.parseInt(question.getMax())) {
                        input.setError(null);
                        return true;
                    } else {
                        input.setError(String.format(activity.getString(R.string.required_error_lenght), question.getMin(), question.getMax()));
                        return false;
                    }
                } else {
                    input.setError(null);
                    return true;
                }
            } else if (mType == 8 || mType == 15) {
                if (question.getMin() != null && question.getMax() != null) {
                    if (Long.parseLong(label.getText().toString()) > Long.parseLong(question.getMax())) {
                        input.setError(String.format(activity.getString(R.string.required_error_range), question.getMin(), question.getMax()));
                        return false;
                    } else {
                        if (Long.parseLong(label.getText().toString()) >= Long.parseLong(question.getMin()) &&
                                Long.parseLong(label.getText().toString()) <= Long.parseLong(question.getMax())) {
                            input.setError(null);
                            return true;
                        } else {
                            input.setError(String.format(activity.getString(R.string.required_error_range), question.getMin(), question.getMax()));
                            return false;
                        }
                    }
                } else {
                    input.setError(null);
                    return true;
                }
            } else {
                input.setError(null);
                return true;
            }
        }
    }

    public IdValue getResponse() {
        // Closed Question one answer
        Log.i("Response", "Getting the response of the question id: "+getQuestion().getId() + "and name" + getQuestion().getName());
        if (mType == 4)
        {
            IdValue idValue = new IdValue(id, new RealmList<>(getResponseId()), validation, mType);
            idValue.setSectionId(this.sectionCount);
            return idValue;
        } else
        {
            IdValue idValue = new IdValue(
                    id,
                    new RealmList<>(new AnswerValue(label.getText().toString())),
                    validation,
                    mType);
            idValue.setSectionId(this.sectionCount);
            return idValue;
        }
    }

    public IdValue getResponse(RealmList<ResponseComplex> options, int indexValue) {
        try {
            return new IdValue(id, new RealmList<>(new AnswerValue(options.get(indexValue).getRecord_id())),
                    validation, mType);
        }catch (IndexOutOfBoundsException ioe)
        {
            // Patch for Dynamic question in case options lenght is 0
            return null;
        }
    }

    private AnswerValue getResponseId() {
        for (IdOptionValue item : response) {
            if (item.getValue().equals(label.getText().toString())) {
                return new AnswerValue(String.valueOf(item.getId()));
            }
        }
        return new AnswerValue(String.valueOf(response.get(0).getId()));
    }


    private String getResponseValue(String value) {
        for (IdOptionValue item : response) {
            if (String.valueOf(item.getId()).equals(value)) {
                return String.valueOf(item.getValue());
            }
        }
        return value;
    }

    public void setVisibilityLabel(boolean isVisible) {
        label.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        input.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setValue(String value){
        label.setText(value);
    }

    public void setIsShow() {
        alreadyShow = false;
    }

    public boolean getIsGoneByVisibilityRules() {
        return isGoneByRules;
    }

    private class EditTextWatcher implements TextWatcher {
        private Activity activity;
        private Long idParentRule;
        private SectionItemView sectionItemView;

        public EditTextWatcher(Activity activity, Long idParentRule, SectionItemView sectionItemView){
            this.activity = activity;
            this.idParentRule = idParentRule;
            this.sectionItemView = sectionItemView;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            validateField();
            final CallDialogListener listener = (CallDialogListener) activity;
            listener.callDynamicVisibilityRules(editable.toString(), idParentRule, sectionItemView);
        }
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

    public String getInputText()
    {
        return label.getText()+"";
    }

    public void addTextListenerToInput(TextWatcher tw)
    {
        label.addTextChangedListener(tw);
    }

    public void adaptAnswersToChange(long idQuestion, String text)
    {
        Log.i("Adapting Change", "On Question: "+ this.question.getId()+ "Comparing: " + idQuestion);
        if(mType == 4)
        {
            // Adapt list to the change
            Log.i("Adapting Change", "Option 4, Len Response:" + this.response.size());
            ArrayList<IdOptionValue> newResponse = new ArrayList<>();
            ArrayList<IdOptionValue> notAsignedAnswers = new ArrayList<>();
            for (IdOptionValue optionValue: this.response)
            {
                // Not assigned values are default answers
                if(optionValue.getQuestion_id() == 0)
                {
                    notAsignedAnswers.add(optionValue);
                }
                if(optionValue.getQuestion_id() == idQuestion)
                {
                    Log.i("Comparing", optionValue.getAnswer()+ " with "+ text);
                    Log.i("Comparing", optionValue.getQuestion_id()+ " with "+ idQuestion);
                    if(optionValue.getAnswer().trim().compareTo(text.trim())==0)
                    {
                        Log.i("Is Equal", optionValue.getAnswer());
                        newResponse.add(optionValue);
                    }
                }
            }
            Log.i("Size of new Response:", newResponse.size()+"");
            if(!newResponse.isEmpty())
            {
                Log.i("Size of new Response:", "Changing the response");
                if(question.getFiltrar())
                {
                    this.filtered_responses = newResponse;
                }
                else
                {
                    this.filtered_responses = notAsignedAnswers;
                    if(!newResponse.isEmpty())
                    {
                        label.setText(newResponse.get(0).getValue());
                    }
                }
            }
        }
        else
        {
            int i = 0;
            for (IdOptionValue optionValue: this.question.getResponses())
            {
                if(optionValue.getQuestion_id() == idQuestion)
                {
                    Log.i("Comparing", optionValue.getAnswer()+ " with "+ text);
                    Log.i("Comparing", optionValue.getQuestion_id()+ " with "+ idQuestion);
                    Log.i("I", i+"");
                    if(optionValue.getAnswer().trim().compareTo(text.trim())==0)
                    {
                        Log.i("Is Equal", optionValue.getAnswer());
                        if(this.question.getResponses().size()>1)
                        {
                            if (i==sectionIndex)
                            {
                                label.setText(optionValue.getValue());
                            }
                        }
                        else
                        {
                            label.setText(optionValue.getValue());
                        }
//                        label.setText(optionValue.getValue());
                        i += 1;
                    }
                }
            }
        }
    }

    public void setSectionIndex(int sectionIndex) {
        this.sectionIndex = sectionIndex;
        Log.i("Setting secindex", sectionIndex+"");
    }
}