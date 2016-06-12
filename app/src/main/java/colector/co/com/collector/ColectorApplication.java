package colector.co.com.collector;

import android.app.Application;

/**
 * Created by Jose Rodriguez on 11/06/2016.
 */
public class ColectorApplication extends Application {

    private static ColectorApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static ColectorApplication getInstance() {
        return mContext;
    }
}
