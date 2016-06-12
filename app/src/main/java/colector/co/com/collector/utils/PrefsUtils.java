package colector.co.com.collector.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Jose Rodriguez on 11/06/2016.
 */
public class PrefsUtils {

    public static final String LONG_ACTIVE_ACCOUNT_ID = "colector.co.com.collector.LONG_ACTIVE_ACCOUNT_ID";
    public static final String STRING_ACTIVE_OAUTH_TOKEN = "colector.co.com.collector.STRING_ACTIVE_OAUTH_TOKEN";
    private static final String PREF_NAME = "colector.co.com.collector.COLECTOR_PREFERENCES";
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
