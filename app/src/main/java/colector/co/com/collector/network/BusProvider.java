package colector.co.com.collector.network;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Jose Rodriguez on 11/06/2016.
 */
public final class BusProvider {

    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    public static Bus getBus() {
        return BUS;
    }

    private BusProvider() {
    }
}
