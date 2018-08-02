package fivetwentysix.ware.com.gpstester;

import android.os.SystemClock;

public class LocationDataPoint {
    private double latatude = 0D;
    private double longitude = 0D;
    private double accuracy = 0D;
    private long timeStamp = 0L;

    public LocationDataPoint(double lat, double lon, double accuracy){
        this.latatude = lat;
        this.longitude = lon;
        this.accuracy = accuracy;
        this.timeStamp = SystemClock.elapsedRealtimeNanos();
    }

    public double getLatitude(){
        return latatude;
    }
    public double getLongitude(){
        return longitude;
    }
    public double getAccuracy(){
        return accuracy;
    }
    public long getTimeStamp() { return timeStamp;}
}
