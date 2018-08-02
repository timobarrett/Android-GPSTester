package fivetwentysix.ware.com.gpstester;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {
    private final String LOG_TAG = "gpstester- " + LocationActivity.class.getSimpleName();
    private ArrayList<LocationDataPoint>mLocationDataPoint = new ArrayList<>();
    private ListView locationList = null;
    private LocationAdapter locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.d(LOG_TAG,"IN onCreate");
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG,"before addAll ");
        mLocationDataPoint.addAll(LocationDataStore.getInstance().getmLocationDataPoints());
        setContentView(R.layout.gps_base);
        locationList = (ListView)findViewById(R.id.location_list_view);
        locationAdapter = new LocationAdapter(getApplicationContext(),mLocationDataPoint);
        locationList.setAdapter(locationAdapter);
    }

}
