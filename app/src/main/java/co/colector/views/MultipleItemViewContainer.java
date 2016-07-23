package co.colector.views;

import android.app.Activity;
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
import co.colector.R;
import co.colector.helpers.PreferencesManager;
import co.colector.listeners.CallDialogListener;
import co.colector.model.IdOptionValue;
import co.colector.model.IdValue;
import co.colector.model.Question;
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
    private int mType;
    private Activity activity;

    private ArrayList<IdOptionValue> options = new ArrayList<>();
    private boolean required = false;
    private String finalResult = "";

    private ArrayList<String> selectedResults = new ArrayList<>();
    private SectionItemView sectionItemView;

    public MultipleItemViewContainer(Context context, SectionItemView sectionItemView) {
        super(context);
        this.sectionItemView = sectionItemView;
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.multiple_item_view_container, this, true);
        ButterKnife.bind(this, view);
        this.activity = (Activity) context;
    }

    public void bind(ArrayList<IdOptionValue> response, Question question,
                     @Nullable List<String> previewDefault) {
        if (response.isEmpty()) return;
        this.mType = question.getType();
        this.id = question.getId();
        this.validation = question.getValidacion();
        required = question.getRequerido();
        this.options = response;
        setOnClickListeners(question.getName(), response);
        //Bind the title
        if (required) {
            label.setText(getContext().getString(R.string.required_field, question.getName()));
        } else label.setText(question.getName());

        //Adding extra text info, to notify the user the action to make.
        label.setText(label.getText() + " " + getContext().getString(R.string.click_agregar));
        //Bind the show and hide buttons
        bindShowButton();
        bindCollapseButton(collapse);
        if (previewDefault != null) {
            bindDefaultSelected(previewDefault);
        }
        if (question.getoculto()) this.setVisibility(GONE);
    }

    public void fillData(List<String> results) {
        // Bind the items
        container.removeAllViews();
        if (!results.isEmpty()) {
            selectedResults = new ArrayList<>(results);
            String resultToDisplay = "";
            for (String result : selectedResults) {
                finalResult = finalResult.isEmpty() ? result : finalResult + ", " + result;
                resultToDisplay = resultToDisplay.isEmpty() ? result : resultToDisplay + ", " + result;
            }
            //FinalResult = "mazda,hola,como"
            //result = "hola,como"

            String[] wordsToDelete = finalResult.split(",");
            ArrayList<String> wordsToRealDelete = new ArrayList<String>();

            for (int i = 0; i < wordsToDelete.length; i++){
                if (!resultToDisplay.contains(wordsToDelete[i]))
                    wordsToRealDelete.add(wordsToDelete[i]);
            }
            String totalResults = PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.OPTIONS_SELECTEDS,"");
            if (totalResults.isEmpty()) {
                PreferencesManager.getInstance().storeOptionsSelecteds(resultToDisplay);
            }
            else {
                for (String s: wordsToRealDelete)
                    totalResults = totalResults.replace(s,"");
                PreferencesManager.getInstance().storeOptionsSelecteds(resultToDisplay+totalResults);
            }
            finalResult = resultToDisplay;
            TextView textView = new TextView(getContext());
            textView.setText(resultToDisplay);
            container.addView(textView);
        }
    }

    private void setOnClickListeners(final String title, final List<IdOptionValue> response) {
        final CallDialogListener listener = (CallDialogListener) activity;
        label.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            listener.callDialog(title, response, MultipleItemViewContainer.this, 1, sectionItemView);
            }
        });
        label.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                listener.callDialog(title, response, MultipleItemViewContainer.this, 1, sectionItemView);
            }
        });
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
        if (!selectedResults.isEmpty()) {
            label.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
            return true;
        }
        label.setTextColor(ContextCompat.getColor(getContext(), R.color.red_label_error_color));
        return false;
    }

    public RealmList<IdValue> getResponses() {
        RealmList<IdValue> responses = new RealmList<>();
        if (!selectedResults.isEmpty())
            for (String item : selectedResults)
                responses.add(new IdValue(id, getSelectedId(item), validation, mType));
        return responses;
    }

    private String getSelectedId(String selectedValue) {
        for (IdOptionValue option : options) {
            if (option.getValue().equals(selectedValue)) {
                return String.valueOf(option.getId());
            }
        }
        return selectedValue;
    }

    private void bindDefaultSelected(List<String> previewDefault) {
        getSelectedValues(previewDefault);
    }

    private void getSelectedValues(List<String> previewDefault) {
        ArrayList<String> values = new ArrayList<>();
        for (String value : previewDefault) {
            for (IdOptionValue option : options) {
                if (String.valueOf(option.getId()).equals(value)) values.add(option.getValue());
            }
        }
        fillData(values);
    }
}