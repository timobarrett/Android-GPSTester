package fivetwentysix.ware.com.gpstester;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LocationAdapter extends BaseAdapter {
    private static final String LOG_TAG = "gpstester- "+LocationAdapter.class.getSimpleName();
    private Context mContext;
    private LayoutInflater mLayoutInflator;

    LocationAdapter(Context context, ArrayList<LocationDataPoint>locationDataPoints){
        mContext = context;
    }
    @Override
    public int getCount() { return LocationDataStore.getInstance().getmLocationDataPoints().size();}

    @Override
    public Object getItem(int position){
        Log.d(LOG_TAG,"IN getItem");
        return LocationDataStore.getInstance().getmLocationDataPoints().get(position);}

    @Override
    public long getItemId(int position){
        Log.d(LOG_TAG,"IN getIteId");
        return Math.round(LocationDataStore.getInstance().getmLocationDataPoints().get(position).getAccuracy());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Log.d(LOG_TAG,"IN getView");
        TextView lat;
        TextView lon;
        TextView accuracy;
        TextView timestamp;
        View sv = null;
        mLayoutInflator = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mLayoutInflator != null){
            sv = mLayoutInflator.inflate(R.layout.gps_view,parent,false);
            lat = sv.findViewById(R.id.lat);
            lon = sv.findViewById(R.id.lon);
            accuracy = sv.findViewById(R.id.accuracy);
            timestamp = sv.findViewById(R.id.timestamp);
            lat.setText(Double.toString(LocationDataStore.getInstance().getmLocationDataPoints().get(position).getLatitude()));
            lon.setText(Double.toString(LocationDataStore.getInstance().getmLocationDataPoints().get(position).getLongitude()));
            int accur = (int)Math.round(LocationDataStore.getInstance().getmLocationDataPoints().get(position).getAccuracy());
            accuracy.setText(Integer.toString(accur));
            timestamp.setText(returnFormattedTime(LocationDataStore.getInstance().getmLocationDataPoints().get(position).getTimeStamp()));
        }
        return sv;
    }

    private String returnFormattedTime(long nanos){
        Log.d(LOG_TAG,"IN returnFormattedTime");
        StringBuilder stTime = new StringBuilder();
        Long days= TimeUnit.NANOSECONDS.toDays(nanos);
        nanos -= TimeUnit.DAYS.toNanos(days);
        Long hours=TimeUnit.NANOSECONDS.toHours(nanos);
        nanos -= TimeUnit.HOURS.toNanos(hours);
        long minutes=TimeUnit.NANOSECONDS.toMinutes(nanos);
        nanos -= TimeUnit.MINUTES.toNanos(minutes);
        long seconds= TimeUnit.NANOSECONDS.toSeconds(nanos);
        nanos -= TimeUnit.SECONDS.toNanos(seconds);
        Long millisec = TimeUnit.NANOSECONDS.toMillis(nanos);
        if (days!=0){
            stTime.append(days.toString()).append(":");}
        if (hours!=0 || days !=0){
            stTime.append(hours.toString()).append(":");}
        stTime.append(String.format("%02d",minutes)).append(":").append(String.format("%02d",seconds)).append(".").append(millisec.toString());
        return stTime.toString();
    }
}
