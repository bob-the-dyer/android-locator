package ru.spb.cupchinolabs.androidlocator;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import static ru.spb.cupchinolabs.androidlocator.Utils.print;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 02.03.13
 * Time: 9:49
 */
public class LocationMonitorService extends Service {

    private static final String TAG = "LocationMonitorService";
    private static final int INTERVAL_IN_SECONDS_TO_SLEEP = 10;
    private static final int TIMEOUT = 20;

    private volatile boolean runMainLoop = true;
    private LocationListener locationListener;

//    public LocationMonitorService() {
//        super(TAG + "-thread");
//    }

//    @Override
    protected void onHandleIntent(Intent intent) {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {

            private static final String TAG = "LocationMonitorListener";

            public void onLocationChanged(Location location) {
                print("onLocationChanged:" + location, null, TAG);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                print("onStatusChanged:status->" + status + ",provider->" + provider, null, TAG);
            }

            public void onProviderEnabled(String provider) {
                print("onProviderEnabled:" + provider, null, TAG);
            }

            public void onProviderDisabled(String provider) {
                print("onProviderDisabled:" + provider, null, TAG);
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        LocationProvider headLocationProvider =
                new GpsLocationProvider(locationManager, TIMEOUT)
                        .setNext(new NetworkLocationProvider(locationManager, TIMEOUT)
                                .setNext(new DeadEndLocationProvider()));

//        while (runMainLoop) {
//
//            Location location = headLocationProvider.locate();
//
//            Log.d(TAG, "location->" + location);
//
//            if (location != null) {
//                //TODO store location
//            } else {
//                //TODO
//
//            }
//
//            if (!runMainLoop){
//                break;
//            }
//
//            try {
//                Thread.sleep(INTERVAL_IN_SECONDS_TO_SLEEP * 1000);
//                Log.d(TAG, "after sleep");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                Log.d(TAG, "interrupted");
//                Thread.currentThread().interrupt();
//            }
//        }

        //TODO release resources
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(null);
        notifyOnStartCommand();
        return START_STICKY;//TODO think about it
    }

    @Override
    public void onCreate() {
        print("Location monitor service - creating ...", this, TAG);
    }

    @Override
    public void onDestroy() {
        notifyOnDestroy();
        ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                .removeUpdates(locationListener);
        runMainLoop = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notifyOnDestroy() {
        //TODO replace Toast with notification
        print("Location monitor service - destroying ...", this, TAG);
        stopForeground(true);
    }

    private void notifyOnStartCommand() {
        Notification notification = new Notification(
                R.drawable.triangle, getText(R.string.locator_notification_ticker_text), System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, LocationMonitorActivity.class), 0);
        notification.setLatestEventInfo(this, getText(R.string.locator_notification_content_title),
                getText(R.string.locator_notification_content_text), pendingIntent);

        startForeground(R.string.locator_notification_content_title, notification);
        print("Location monitor service - starting ...", this, TAG);
    }
}
