package colector.co.com.collector.database;

import java.util.List;

import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.SurveySave;
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

    public Long getNewSurveyIndex(final long saveDataId) {
        RealmResults<SurveySave> results = realm.where(SurveySave.class).equalTo("instanceId", saveDataId).findAll();
        return results.size() + 1l ;
    }

    public void addSurveyAvailable(final List<Survey> surveys, final OnDataBaseSave callback){
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

}
