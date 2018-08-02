package fivetwentysix.ware.com.gpstester;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private final String LOG_TAG = "gpstester- "+ MainActivity.class.getSimpleName();
    private boolean mGpsPermissionGranted = false;
    private final int PERMISSION_REQUEST_GPS = 7;
    private Button mDisplayButton;
    private Button mStopButton;
    private LocationTrackerService locationTrackerService = null;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Log.d(LOG_TAG,"IN onCreate");
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .permitDiskReads()
                    .permitDiskWrites()
                    .penaltyLog()
                    .build());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplayButton = (Button)findViewById(R.id.displayButton);
        mStopButton = (Button)findViewById(R.id.stopButton);
        verifyPermissions();
        Bundle arguments = new Bundle();
        if (mGpsPermissionGranted){
            startApp();
        }

        mDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),LocationActivity.class);
            //    intent.putExtra("CityName",IncidentManagement.getInstance().getCity());
                startActivity(intent);
            }
        });
        mStopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                stopService(mIntent);
                logInformation();
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread thread, Throwable e){
                Log.e(LOG_TAG,e.getCause().getMessage());
                throw new RuntimeException(e);
            }
        });
    }
    @Override
    protected void onResume() {
        Log.d(LOG_TAG,"IN onResume");
        super.onResume();
    }
    @Override
    protected void onStop() {
        Log.d(LOG_TAG,"RunSpecificity - onStop");
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        Log.d(LOG_TAG,"IN onDestroy");
//        Utility.debugLog(LOG_TAG,"RunSpecificity - onDestroy");
        super.onDestroy();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantedResults){
        Log.d(LOG_TAG,"IN onRequestPermissionsResult");
        if (requestCode == PERMISSION_REQUEST_GPS){
            if (grantedResults.length == 1 && grantedResults[0] == PackageManager.PERMISSION_GRANTED){
                startApp();
                mGpsPermissionGranted = true;
            }else{
               Log.d(LOG_TAG,"Permission not granted");
            }
        }
    }
    private void verifyPermissions() {
        Log.d(LOG_TAG, "IN verifyPermissions");
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_GPS);
            }
            else{ mGpsPermissionGranted= true;}
        } else {
            int result = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (result == PackageManager.PERMISSION_GRANTED) {
                mGpsPermissionGranted = true;
            }
        }
    }

    private void startApp() {
        Log.d(LOG_TAG,"IN startApp");
        if (locationTrackerService == null){
            mIntent = new Intent(this, LocationTrackerService.class);
            startService(mIntent);
        }
    }

    private void logInformation(){
        FileOutputStream fos = null;
        try{
            fos = this.openFileOutput("gpslog",this.getApplicationContext().MODE_PRIVATE);
        }catch(FileNotFoundException e){Log.d(LOG_TAG,"File Not Found Exception");}
        if(fos !=null){
            ArrayList<LocationDataPoint> ldp = LocationDataStore.getInstance().getmLocationDataPoints();
            Iterator iter  = ldp.iterator();
            StringBuilder sb = new StringBuilder();
            try {
            while (iter.hasNext()){
                LocationDataPoint dp = (LocationDataPoint)iter.next();
                sb.append(" -- ").append(dp.getTimeStamp()).append(" -- ").append(Math.round(dp.getAccuracy())).append(" -- ").append(dp.getLatitude()).append(" -- ").append(dp.getLongitude());

                    fos.write(sb.toString().getBytes());
                    fos.flush();
                }
                fos.close();
        }   catch(IOException e){Log.d(LOG_TAG,"IOException");}

        }
    }

}
