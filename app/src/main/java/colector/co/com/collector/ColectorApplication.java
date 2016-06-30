package colector.co.com.collector;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;

import colector.co.com.collector.helpers.PreferencesManager;
import colector.co.com.collector.network.ApiCallsManager;
import colector.co.com.collector.network.BusProvider;
import colector.co.com.collector.utils.PrefsUtils;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Jose Rodriguez on 11/06/2016.
 */
public class ColectorApplication extends Application {

    private static ColectorApplication mContext;
    private Bus bus = BusProvider.getBus();
    private RequestManager glide;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = this;

        ApiCallsManager apiCallsManager = new ApiCallsManager(this, bus);
        bus.register(apiCallsManager);
        bus.register(this);
        PrefsUtils.initializeInstance(this);

        // Init Glide
        glide = Glide.with(this);

        PreferencesManager.initializeInstance(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public static ColectorApplication getInstance() {
        return mContext;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public RequestManager getGlideInstance() {
        return glide;
    }
}