package colector.co.com.collector.database;

import android.util.Log;

import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.model.SurveySave;
import io.realm.Realm;

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

    public void addSurvey(final SurveySave surveySave, final OnDataBaseSave callback){
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

}
