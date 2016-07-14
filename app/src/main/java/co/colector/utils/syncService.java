package co.colector.utils;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import co.colector.R;
import co.colector.http.AsyncResponse;
import co.colector.http.BackgroundTask;
import co.colector.http.ResourceNetwork;
import co.colector.model.Survey;
import co.colector.model.request.SendSurveyRequest;
import co.colector.model.response.ErrorResponse;
import co.colector.model.response.SendSurveyResponse;
import co.colector.persistence.dao.SurveyDAO;
import co.colector.session.AppServices;
import co.colector.settings.AppSettings;

public class syncService extends IntentService {
    private List<Survey> toPrint;

    private static final String TAG =
            "send Done Surveys";

    @Override
    protected void onHandleIntent(Intent arg0) {
        Log.i(TAG, "Intent Service started");
    }

    public syncService() {
        super("MyIntentService");
    }

    Boolean sending;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sending = false;

        if (AppSettings.SERVICE_FLAG_UPLOAD == (int) intent.getFlags()) {
            AppServices.getInstance().setSurveyDone(new SurveyDAO(syncService.this).getSurveyDone("FALSE"));
            toPrint = AppServices.getInstance().getSurveyDone();
            Log.i(TAG, "Service Uploading onStartCommand " + startId);
            return uploadAll();

        } else if (AppSettings.SERVICE_FLAG_DELETE == (int) intent.getFlags()) {
            AppServices.getInstance().setSurveyDone(new SurveyDAO(syncService.this).getSurveyDone("ENVIADO"));
            toPrint = AppServices.getInstance().getSurveyDone();
            Log.i(TAG, "Service Deleting onStartCommand " + startId);
            return deleteAll();

        }
        return 0;
    }

    private int deleteAll() {
        Runnable r = new Runnable() {
            public void run() {
                for (int i = 0; i < toPrint.size(); i++) {
                    int minutesSending = 0;
                    while (sending) {
                        try {
                            Thread.sleep(200); // Waits for 1 second (1000 milliseconds)
                            minutesSending++;
                            if (minutesSending == 180) {
                                stopSelf();
                                Log.i(TAG, "Tarda mucho Eliminando");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    sending = true;

                    //tarea larga
                    final Survey item = (Survey) toPrint.get(i);
                    new SurveyDAO(syncService.this).deleteSurveysInstance(Long.parseLong(item.getInstanceId().toString()));
                    sending = false;

                }
                stopSelf();
            }
        };
        Thread t = new Thread(r);
        t.start();

        return 0;
    }

    private int uploadAll() {
        Runnable r = new Runnable() {
            public void run() {
                for (int i = 0; i < toPrint.size(); i++) {
                    int minutesSending = 0;
                    while (sending) {
                        try {
                            Thread.sleep(1000); // Waits for 1 second (1000 milliseconds)
                            minutesSending++;
                            if (minutesSending == 180) {
                                stopSelf();
                                Log.i(TAG, "Tarda mucho enviando");
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    sending = true;
                    final Survey item = toPrint.get(i);
                    SendSurveyRequest toSend = new SendSurveyRequest(item);
                    AsyncResponse callback = new AsyncResponse() {
                        @Override
                        public void callback(Object output, String Sended) {
                            if (output instanceof SendSurveyResponse) {
                                SendSurveyResponse response = (SendSurveyResponse) output;

                                if (response.getResponseCode().equals(AppSettings.HTTP_OK)) {
                                    new SurveyDAO(syncService.this).statusSurveyInstance(Long.parseLong(String.valueOf(item.getInstanceId())), item.getForm_precargados());
                                } else {
                                    Toast.makeText(syncService.this, response.getResponseDescription(), Toast.LENGTH_LONG).show();
                                }
                            } else if (output instanceof ErrorResponse) {
                                Toast.makeText(syncService.this, ((ErrorResponse) output).getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(syncService.this, syncService.this.getString(R.string.survey_save_send_error), Toast.LENGTH_LONG).show();
                            }
                            sending = false;
                        }
                    };
                    BackgroundTask bt = new BackgroundTask(syncService.this, toSend, new SendSurveyResponse(), callback, null, true);
                    bt.execute(AppSettings.URL_BASE + ResourceNetwork.URL_SEND_SURVEY_DEF);
                }
                stopSelf();
            }
        };
        Thread t = new Thread(r);
        t.start();
        return 0;
    }
}