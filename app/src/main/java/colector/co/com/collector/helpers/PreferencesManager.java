package colector.co.com.collector.helpers;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jose Rodriguez on 30/06/2016.
 */
public class PreferencesManager {
    public static final String ACTIVE_ACCOUNT = "colector.co.com.collector.LONG_ACTIVE_ACCOUNT_ID";
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

    public boolean isActiveAccount(){
        return getPrefs().getBoolean(PreferencesManager.ACTIVE_ACCOUNT, false);
    }

    public SharedPreferences.Editor getEditor() {
        return mPreferences.edit();
    }

    public SharedPreferences getPrefs() {
        return mPreferences;
    }
}