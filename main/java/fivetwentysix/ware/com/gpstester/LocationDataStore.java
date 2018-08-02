package fivetwentysix.ware.com.gpstester;

import android.util.Log;

import java.util.ArrayList;

public class LocationDataStore {
    protected final String LOG_TAG = "gpstester -"+LocationDataStore.class.getSimpleName();
    private final static LocationDataStore locationDataStore = new LocationDataStore();
    public static LocationDataStore getInstance() { return locationDataStore;}
    private ArrayList<LocationDataPoint> mLocationDataPoints = new ArrayList<>();


    public ArrayList<LocationDataPoint> getmLocationDataPoints(){
        Log.d(LOG_TAG,"IN getmLocationDataPoints");
        return mLocationDataPoints;
    }
    public void setLocationDataStore(LocationDataPoint locationDataPoint){
        Log.d(LOG_TAG,"IN setLocationDataStore "+ locationDataPoint.toString());
        mLocationDataPoints.add(locationDataPoint);
    }
}
