package co.colector;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.MapView;

import co.colector.R;

public class MapsArcGisActivity extends Activity {
    MapView mMapView;
    double lat, lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_arcgis);
        mMapView = (MapView)findViewById(R.id.map);
    }
}
