package fivetwentysix.ware.com.gpstester;

/************************************
* Change Log
*     7-Aug-2018 - replaced deprecated Location API with supported approach
*
*/
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class LocationTrackerService extends Service {
    private static final String LOG_TAG = "gpstester- "+ LocationTrackerService.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mLocation;
    private Boolean mRequestingLocationUpdates;
    public LocationTrackerService(){}


    @Override
    public void onCreate(){
        Log.d(LOG_TAG,"IN onCreate");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingClient = LocationServices.getSettingsClient(this);
        mRequestingLocationUpdates = false;

        setupLocationRequest();
        buildLocationSettingsRequest();
        createLocationCallback();
        getLastLocation();
        startLocationUpdates();

    }

    private void createLocationCallback(){
        Log.d(LOG_TAG,"IN createLocationCallback");
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                Log.d(LOG_TAG,"IN onLocationResult");
                super.onLocationResult(locationResult);
                addDataToRepository(locationResult.getLastLocation());
            }
        };
    }
    /**
     * setup the location request properties
     *  smallest distance and balanced power caused issues
     */
    private void setupLocationRequest(){
        Log.d(LOG_TAG,"IN setupLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(Constants.LOCATION_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.FAST_LOCATION_INTERVAL);

    }

   private void buildLocationSettingsRequest(){
        Log.d(LOG_TAG,"IN buildLocationSettingsRequest");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
   }

    /** this works on a real phone
     *
     */
    private void getLastLocation() {
        Log.d(LOG_TAG,"IN getLastLocation");
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                               mLocation = task.getResult();
                                Log.v(LOG_TAG,"GetLastLocation Success LAT = "+mLocation.getLatitude());
                            } else {
                                Log.v(LOG_TAG,"RESULT = "+task.getResult().toString());
 //                               Log.w(LOG_TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(LOG_TAG, "Lost location permission." + unlikely);
        }
    }

    private void addDataToRepository(Location location){
        Log.d(LOG_TAG,"IN addDataToRepository");
        LocationDataPoint locationDataPoint = new LocationDataPoint(location.getLatitude(),location.getLongitude(),location.getAccuracy());
        LocationDataStore.getInstance().setLocationDataStore(locationDataPoint);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(LOG_TAG,"IN onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.d(LOG_TAG,"IN onBind");
        return null;
    }

    @Override
    public void onDestroy(){
        Log.d(LOG_TAG,"IN onDestroy");
        stopLocationUpdates();
        stopSelf();
    }


    //implement the commented out parts in MainActivity / UI
    void startLocationUpdates(){
        Log.d(LOG_TAG,"IN startLocationUpdates");
        try {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, Looper.myLooper());

        } catch(SecurityException se){
            Log.e(LOG_TAG,"Exception - "+ se.getMessage());
        }
    }

    /**
     * stop location updates - called
     */
    void stopLocationUpdates() {
        Log.d(LOG_TAG, "IN stopLocationUpdates");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


}
