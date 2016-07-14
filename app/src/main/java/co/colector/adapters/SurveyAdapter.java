package co.colector.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.colector.R;
import co.colector.SurveyActivity;
import co.colector.database.DatabaseHelper;
import co.colector.listeners.OnUploadSurvey;
import co.colector.model.Survey;
import co.colector.session.AppSession;
import co.colector.settings.AppSettings;

/**
 * Created by dherrera on 11/10/15.
 */
public class SurveyAdapter extends ArrayAdapter<Survey> {

    private Context context;
    private List<Survey> items;
    private String idTab;
    OnUploadSurvey callback;
    @BindView(R.id.adapter_survey_title)
    TextView row_name;
    @BindView(R.id.adapter_survey_description)
    TextView row_description;
    @BindView(R.id.buttonDeleteSurvey)
    ImageButton deleteButton;
    @BindView(R.id.buttonUploadSurvey)
    ImageButton uploadUpload;
    @BindView(R.id.buttonEditSurvey)
    ImageButton editUpload;
    @BindView(R.id.imageButtonNotSync)
    ImageButton imageButtonNotSync;
    @BindView(R.id.imageButtonSyncDone)
    ImageButton imageButtonSyncDone;
    @BindView(R.id.containerDescription)
    LinearLayout containerDescription;


    public SurveyAdapter(Context context, ArrayList<Survey> items, String idTab, OnUploadSurvey callback) {
        super(context, R.layout.adapter_survey, items);
        this.context = context;
        this.items = items;
        this.idTab = idTab;
        this.callback = callback;
    }

    public List<Survey> getItems() {
        return items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Survey item = this.items.get(position);
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_survey, parent, false);
        ButterKnife.bind(this, row);
        row_name.setText(item.getForm_name());

        if (idTab.equals(AppSettings.TAB_ID_AVAILABLE_SURVEY))
            row_description.setText(item.getForm_description());
        else if (idTab.equals(AppSettings.TAB_ID_DONE_SURVEY)) {
            configureDoneRow(item, position);
        }

        return row;
    }


    /**
     * Configure Action Buttons on Done Surveys
     *
     * @param item     survey
     * @param position on list
     */
    private void configureDoneRow(final Survey item, final int position) {
        row_description.setText(item.getSurveyDoneDescription());
        deleteButton.setTag(item.getInstanceId());
        deleteButton.setVisibility(View.VISIBLE);

        if (!item.isUploaded()) {
            imageButtonSyncDone.setVisibility(View.GONE);
            imageButtonNotSync.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
            uploadUpload.setVisibility(View.GONE);
            editUpload.setVisibility(View.GONE);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(R.string.ans_message_delete_survey_undone)
                            .setPositiveButton(getContext().getString(R.string.common_erase), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseHelper.getInstance().deleteSurveysDone(item.getInstanceId());
                                    items.remove(item);
                                    SurveyAdapter.this.notifyDataSetChanged();
                                }

                            })
                            .setNegativeButton(getContext().getString(R.string.common_cancel), null)
                            .show();
                }
            });
            imageButtonNotSync.setTag(item.getInstanceId());
            imageButtonNotSync.setVisibility(View.VISIBLE);
            imageButtonNotSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    callback.onUploadClicked(item, SurveyAdapter.this);
                }
            });
            configureContainerClickListener(position);

        } else {
            configureUploadedRow(item, position);
        }
    }

    /**
     * Configure Action Buttons on Uploaded Surveys
     *
     * @param item survey
     */
    private void configureUploadedRow(final Survey item, int position) {
        row_description.setText(item.getSurveyDoneDescription());
        imageButtonSyncDone.setVisibility(View.VISIBLE);
        imageButtonNotSync.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        uploadUpload.setVisibility(View.GONE);
        editUpload.setVisibility(View.GONE);
        configureContainerClickListener(position);
    }


    /**
     * Configure row click listener
     *
     * @param position of row
     */
    private void configureContainerClickListener(final int position) {
        containerDescription.setClickable(true);
        containerDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppSession.getInstance().setCurrentSurvey(items.get(position), AppSettings.SURVEY_SELECTED_EDIT);
                Intent intent = new Intent(getContext(), SurveyActivity.class);
                context.startActivity(intent);
            }
        });
    }
}