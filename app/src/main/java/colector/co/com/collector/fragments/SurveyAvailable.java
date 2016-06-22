package colector.co.com.collector.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import colector.co.com.collector.R;
import colector.co.com.collector.SurveyActivity;
import colector.co.com.collector.adapters.SurveyAdapter;
import colector.co.com.collector.database.DatabaseHelper;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.persistence.dao.SurveyDAO;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.settings.AppSettings;


public class SurveyAvailable extends Fragment {
    ProgressDialog progress;
    private String idTabs;
    private ListView list;
    private ArrayList<Survey> toPrint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_survey_available, container, false);
        list = (ListView) v.findViewById(R.id.list_items);

        idTabs = this.getTag();
        progress = ProgressDialog.show(getContext(), getResources().getString(R.string.main_list_title),
                getResources().getString(R.string.main_list_msg), true);
        if (idTabs.equals(AppSettings.TAB_ID_AVAILABLE_SURVEY)) {
            AppSession.getInstance().cleanSurveyAvailable();
            toPrint = new ArrayList<>(AppSession.getInstance().getSurveyAvailable());


        } else if (idTabs.equals(AppSettings.TAB_ID_UPLOADED_SURVEY)) {
            AppSession.getInstance().setSurveyDone(new SurveyDAO(getContext()).getSurveyDone("ENVIADO"));
            toPrint = new ArrayList<>(AppSession.getInstance().getSurveyDone());


        } else if (idTabs.equals(AppSettings.TAB_ID_DONE_SURVEY)) {
            toPrint = DatabaseHelper.getInstance().getSurveysDone(
                    new ArrayList<>(AppSession.getInstance().getSurveyAvailable()));
        }
        progress.dismiss();
        fillList();
        return v;
    }

    private void fillList() {
        SurveyAdapter adapter = new SurveyAdapter(getActivity(), toPrint, idTabs);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Survey> WorkHoursRecSurvey = null;
                if (toPrint.get(position).getForm_precargados()) {
                    AppSession.getInstance().setSurveyDone(new SurveyDAO(getContext()).
                            getSurveyDonePrecar(toPrint.get(position).getForm_name().toString()));
                    WorkHoursRecSurvey = AppSession.getInstance().getSurveyDone();
                }

                boolean flagPrecargado = false;
                if (WorkHoursRecSurvey != null)
                    if (WorkHoursRecSurvey.get(0).getForm_id() != null)
                        flagPrecargado = true;

                if (flagPrecargado == true) {
                    AppSession.getInstance().setCurrentSurvey(WorkHoursRecSurvey.get(0), AppSettings.SURVEY_SELECTED_NEW);
                    Intent intentWH = new Intent(getContext(), SurveyActivity.class);
                    startActivity(intentWH);
                } else {
                    AppSession.getInstance().setCurrentSurvey(toPrint.get(position), AppSettings.SURVEY_SELECTED_NEW);
                    Intent intent = new Intent(getContext(), SurveyActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
