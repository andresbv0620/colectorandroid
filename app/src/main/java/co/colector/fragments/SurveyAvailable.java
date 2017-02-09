package co.colector.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.colector.R;
import co.colector.SurveyActivity;
import co.colector.adapters.SurveyAdapter;
import co.colector.database.DatabaseHelper;
import co.colector.listeners.OnUploadSurvey;
import co.colector.model.Survey;
import co.colector.session.AppSession;
import co.colector.settings.AppSettings;


public class SurveyAvailable extends Fragment {

    @BindView(R.id.list_items)
    ListView list;
    @BindView(R.id.loading)
    View loading;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;
    OnUploadSurvey callback;

    private String idTabs;
    private ArrayList<Survey> toPrint;
    private SurveyAdapter adapter;
    private ProgressDialog progressDialog;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (OnUploadSurvey) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_survey_available, container, false);
        ButterKnife.bind(this, v);
        idTabs = this.getTag();
        //loading.setVisibility(View.VISIBLE);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.sync_data));
        setupTabs();
        fillList();
        loading.setVisibility(View.GONE);
        return v;
    }

    private void setupTabs() {
        if (idTabs.equals(AppSettings.TAB_ID_AVAILABLE_SURVEY))
        {
            AppSession.getInstance().cleanSurveyAvailable();
            toPrint = new ArrayList<>(AppSession.getInstance().getSurveyAvailable());
        }
        else if (idTabs.equals(AppSettings.TAB_ID_DONE_SURVEY))
        {
            ArrayList<Survey> toUnion = DatabaseHelper.getInstance().getSurveysDone(
                    new ArrayList<>(AppSession.getInstance().getSurveyAvailable())
            );

            toPrint = DatabaseHelper.getInstance().getSurveysUploaded(
                    new ArrayList<>(AppSession.getInstance().getSurveyAvailable())
            );

            for (Survey survey : toUnion)
            {
                toPrint.add(survey);
            }
            Collections.sort(toPrint, new DateComparator());
        }
    }


    private void fillList() {
        adapter = new SurveyAdapter(getActivity(), toPrint, idTabs, callback);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppSession.getInstance().setCurrentSurvey(toPrint.get(position), AppSettings.SURVEY_SELECTED_NEW);
                Intent intent = new Intent(getContext(), SurveyActivity.class);
                startActivity(intent);
            }
        });
    }

    private class DateComparator implements Comparator<Survey>
    {
        @Override
        public int compare(Survey survey, Survey t1) {
            return t1.getInstanceHoraFin().compareTo(survey.getInstanceHoraFin());
        }
    }
}


