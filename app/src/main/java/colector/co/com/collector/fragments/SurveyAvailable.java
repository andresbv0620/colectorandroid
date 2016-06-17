package colector.co.com.collector.fragments;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import colector.co.com.collector.R;
import colector.co.com.collector.SurveyActivity;
import colector.co.com.collector.adapters.SurveyAdapter;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.persistence.dao.SurveyDAO;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.settings.AppSettings;

import static com.google.android.gms.internal.zzir.runOnUiThread;


public class SurveyAvailable extends Fragment {
    ProgressDialog progress;
    private String idTabs;
    private ListView list;
    private List<Survey> toPrint;


    private boolean isServiceRunning() {
        try {
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("colector.co.com.collector.utils.syncService".equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Service Err " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Boolean runingServiceFlag;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_survey_available, container, false);
        list = (ListView) v.findViewById(R.id.list_items);

        idTabs = this.getTag();


        progress = ProgressDialog.show(getContext(), getResources().getString(R.string.main_list_title),
                getResources().getString(R.string.main_list_msg), true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    runingServiceFlag = false;
                    if (idTabs.equals(AppSettings.TAB_ID_AVAILABLE_SURVEY)) {
                        toPrint = AppSession.getInstance().getSurveyAvailable();


                    } else if (idTabs.equals(AppSettings.TAB_ID_UPLOADED_SURVEY)) {
                        AppSession.getInstance().setSurveyDone(new SurveyDAO(getContext()).getSurveyDone("ENVIADO"));
                        toPrint = AppSession.getInstance().getSurveyDone();


                    } else if (idTabs.equals(AppSettings.TAB_ID_DONE_SURVEY)) {
                        runingServiceFlag = isServiceRunning();
                        AppSession.getInstance().setSurveyDone(new SurveyDAO(getContext()).getSurveyDone("FALSE"));
                        toPrint = AppSession.getInstance().getSurveyDone();
                    }

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            progress.dismiss();
                            if (runingServiceFlag) {
                                Toast.makeText(getContext(), "Service Upload Running!", Toast.LENGTH_LONG).show();
                            } else
                                fillList();

                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Progress " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();

        return v;
    }

    private void fillList() {
        SurveyAdapter adapter = new SurveyAdapter(getActivity(), new ArrayList<>(toPrint), idTabs);
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
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SurveyActivity.NEW_SURVEY_KEY, true);
                    intentWH.putExtras(bundle);
                    // Put Survey Answer Index
                    startActivity(intentWH);
                } else {
                    AppSession.getInstance().setCurrentSurvey(toPrint.get(position), AppSettings.SURVEY_SELECTED_NEW);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(SurveyActivity.NEW_SURVEY_KEY, true);
                    Intent intent = new Intent(getContext(), SurveyActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
}
