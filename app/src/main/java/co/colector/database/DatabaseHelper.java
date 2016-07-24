package co.colector.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import co.colector.listeners.OnDataBaseSave;
import co.colector.model.Survey;
import co.colector.model.SurveySave;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public class DatabaseHelper {

    private static Realm realm;
    private static DatabaseHelper instance;


    public static DatabaseHelper getInstance() {
        if (instance == null) {
            realm = Realm.getDefaultInstance();
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public void addSurvey(final SurveySave surveySave, final OnDataBaseSave callback) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(surveySave);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                callback.onError();
            }
        });
    }

    public Long getNewSurveyIndex() {
        RealmResults<SurveySave> results = realm.where(SurveySave.class).findAll();
        return results.size() + 1l;
    }

    public void addSurveyAvailable(final List<Survey> surveys, final OnDataBaseSave callback) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Survey survey : surveys) realm.copyToRealmOrUpdate(survey);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                callback.onError();
            }
        });
    }

    public ArrayList<Survey> getSurveysDone(ArrayList<Survey> surveys) {
        ArrayList<Survey> surveyFilled = new ArrayList<>();
        for (Survey survey : surveys) {
            RealmResults<SurveySave> results = realm.where(SurveySave.class)
                    .equalTo("instanceId", survey.getForm_id()).findAll().where()
                    .equalTo("uploaded", false).findAll();
            for (SurveySave surveySave : results) {
                Survey surveyWithAnswer = new Survey(survey, surveySave);
                surveyFilled.add(surveyWithAnswer);
            }
        }
        return surveyFilled;
    }

    public void deleteSurveysDone(Long saveDataId) {
        final SurveySave result = realm.where(SurveySave.class).equalTo("id", saveDataId).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteFromRealm();
            }
        });
    }

    public void updateRealmSurveySave(final Long id, final OnDataBaseSave callback) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SurveySave result = realm.where(SurveySave.class).equalTo("id", id).findFirst();
                result.setUploaded(true);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                callback.onError();
            }
        });
    }

    public ArrayList<Survey> getSurveysUploaded(ArrayList<Survey> surveys) {
        ArrayList<Survey> surveyFilled = new ArrayList<>();
        for (Survey survey : surveys) {
            RealmResults<SurveySave> results = realm.where(SurveySave.class)
                    .equalTo("instanceId", survey.getForm_id()).findAll().where()
                    .equalTo("uploaded", true).findAll();
            for (SurveySave surveySave : results) {
                Survey surveyWithAnswer = new Survey(survey, surveySave);
                surveyFilled.add(surveyWithAnswer);
            }
        }
        return surveyFilled;
    }

    public List<Survey> getSurveys() {
        RealmResults<Survey> results = realm.where(Survey.class).findAll();
        return results.subList(0, results.size());
    }

    public void deleteDatabase() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Realm.getDefaultInstance().deleteAll();
            }
        });
    }
}