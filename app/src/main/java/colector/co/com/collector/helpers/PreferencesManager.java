package colector.co.com.collector.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import colector.co.com.collector.model.response.ResponseData;

/**
 * Created by Jose Rodriguez on 30/06/2016.
 */
public class PreferencesManager {
    public static final String ACTIVE_ACCOUNT = "colector.co.com.collector.LONG_ACTIVE_ACCOUNT_ID";
    public static final String RESPONSE_OBJECT_ACCOUNT = "colector.co.com.collector.RESPONSE_OBJECT_ACCOUNT";
    public static final String LATITUDE_SURVEY = "colector.co.com.collector.LATITUDE_SURVEY";
    public static final String LONGITUDE_SURVEY = "colector.co.com.collector.LONGITUDE_SURVEY";
    private static final String PREF_NAME = "colector.co.com.collector.COLECTOR_PREFERENCES";
    private static PreferencesManager sInstance;
    private final SharedPreferences mPreferences;

    private PreferencesManager(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setActiveAccount() {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean(PreferencesManager.ACTIVE_ACCOUNT, true);
        edit.commit();
    }

    public void logoutAccount() {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean(PreferencesManager.ACTIVE_ACCOUNT, false);
        edit.commit();
    }

    public void storeResponseData(ResponseData responseData){
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(PreferencesManager.RESPONSE_OBJECT_ACCOUNT, new Gson().toJson(responseData));
        edit.commit();
    }

    public ResponseData getUserData(){
        return new Gson().fromJson(getPrefs().getString(PreferencesManager.RESPONSE_OBJECT_ACCOUNT, ""),ResponseData.class);
    }

    public boolean isActiveAccount(){
        return getPrefs().getBoolean(PreferencesManager.ACTIVE_ACCOUNT, false);
    }

    public void setCoordinates(String latitude, String longitude){
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(LATITUDE_SURVEY, latitude);
        edit.putString(LONGITUDE_SURVEY, longitude);
        edit.commit();
    }

    public void resetCoordinates(){
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(LATITUDE_SURVEY, "0.0");
        edit.putString(LONGITUDE_SURVEY, "0.0");
        edit.commit();
    }

    public SharedPreferences.Editor getEditor() {
        return mPreferences.edit();
    }

    public SharedPreferences getPrefs() {
        return mPreferences;
    }
}