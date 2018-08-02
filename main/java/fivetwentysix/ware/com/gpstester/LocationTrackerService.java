package fivetwentysix.ware.com.gpstester;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationTrackerService extends Service implements
        LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private static final String LOG_TAG = "gpstester- "+ LocationTrackerService.class.getSimpleName();
    private Looper mServiceLooper;
    private LocationRequest mLocationReq = null;
    private GoogleApiClient mGoogleApiClient;



    public LocationTrackerService(){}

    @Override
    public void onCreate(){
        Log.d(LOG_TAG,"IN onCreate");
    }

    @Override
    public void onLocationChanged(Location location){
        Log.d(LOG_TAG,"IN onLocationChanged");
        if (location != null){
            addDataToRepository(location);
        }
    }

    private void addDataToRepository(Location location){
        Log.d(LOG_TAG,"IN addDataToRepository");
        Intent intent = new Intent(Constants.DATA_UPDATE_BROADCAST);
        intent.putExtra("LAT",location.getLatitude());
        intent.putExtra("LON",location.getLongitude());
        intent.putExtra("ACC",location.getAccuracy());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        LocationDataPoint locationDataPoint = new LocationDataPoint(location.getLatitude(),location.getLongitude(),location.getAccuracy());
        LocationDataStore.getInstance().setLocationDataStore(locationDataPoint);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(LOG_TAG,"IN onStartCommand");
       // gp = intent.getParcelableExtra("GoogleObj");
        Log.d(LOG_TAG, "BEFORE setupGoogleClientApi");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
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
    /**
     * setup the location request properties
     *  smallest distance and balanced power caused issues
     */
    void setupLocationRequest(){
        Log.d(LOG_TAG,"IN setupLocationRequest");
        mLocationReq = new LocationRequest();
        mLocationReq.setInterval(Constants.LOCATION_INTERVAL_IN_MILLISECONDS);
        mLocationReq.setFastestInterval(Constants.LOCATION_INTERVAL_IN_MILLISECONDS);
        mLocationReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    void startLocationUpdates(){
        Log.d(LOG_TAG,"IN startLocationUpdates");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationReq, this);
        }
        catch(SecurityException se){
            Log.e(LOG_TAG,"Exception - "+ se.getMessage());
        }
    }
    /**
     * stop location updates - called
     */
    void stopLocationUpdates() {
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        Log.d(LOG_TAG, "IN stopLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    synchronized void setupGoogleClientApi(Context context) {

        }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.d(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
    /**
     * called when connected status returned from google play services
     * @param connectionHint - Bundle with connection information
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(LOG_TAG,"IN onConnected ");
        setupLocationRequest();
        startLocationUpdates();
    }
    /**
     *
     * @param status - status returned
     */
    public void onResult(@NonNull Status status){
        Log.d(LOG_TAG,"onResult");
    }
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        Log.d(LOG_TAG,"GoogleApiClient Connection was suspended - cause = "+cause);
    }

}
