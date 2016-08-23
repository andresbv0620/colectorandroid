package co.colector.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import co.colector.model.User;


/**
 * Created by Jose Rodriguez on 11/06/2016.
 */
public class PrefsUtils {

    public static final String LONG_ACTIVE_ACCOUNT_ID = "colector.co.com.collector.LONG_ACTIVE_ACCOUNT_ID";
    public static final String STRING_ACTIVE_OAUTH_TOKEN = "colector.co.com.collector.STRING_ACTIVE_OAUTH_TOKEN";
    private static final String PREF_NAME = "colector.co.com.collector.COLECTOR_PREFERENCES";
    public static final String USERS_LOGGED_LIST = "colector.co.com.collector.USERS_LOGGED_LIST";
    private static PrefsUtils sInstance;
    private final SharedPreferences mPreferences;

    private PrefsUtils(Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PrefsUtils(context);
        }
    }

    public List<User> getUserList(){
        String stringList = mPreferences.getString(USERS_LOGGED_LIST, "");
        if (!stringList.isEmpty()) {
            Type type = new TypeToken<List<User>>(){}.getType();
            List<User> users = new Gson().fromJson(stringList, type);
            return users;
        }
        else return new ArrayList<User>();
    }

    public void updateList(List<User> users){
        SharedPreferences.Editor edit = mPreferences.edit();
        String usersJsonString = new Gson().toJson(users);
        edit.putString(USERS_LOGGED_LIST, usersJsonString);
        edit.commit();
    }

    public static synchronized PrefsUtils getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PrefsUtils.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setActiveAccount(long accountID, String authToken) {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putLong(PrefsUtils.LONG_ACTIVE_ACCOUNT_ID, accountID);
        edit.putString(PrefsUtils.STRING_ACTIVE_OAUTH_TOKEN, authToken);
        edit.commit();
    }

    public SharedPreferences.Editor getEditor() {
        return mPreferences.edit();
    }

    public SharedPreferences getPrefs() {
        return mPreferences;
    }
}
