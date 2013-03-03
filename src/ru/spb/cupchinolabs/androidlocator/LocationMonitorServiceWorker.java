package ru.spb.cupchinolabs.androidlocator;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 14:34
 */
public class LocationMonitorServiceWorker extends Thread {

    private static final String TAG = "LocationMonitorService-workerThread";

    private GpsLocationProvider headLocationProvider; //TODO think of where to initialize: constructor or run

    public LocationMonitorServiceWorker(LocationManager locationManager) {
        super("LMS-workerThread");

        headLocationProvider = new GpsLocationProvider(locationManager);
        AbstractChainedLocationProvider networkLocationProvider = new NetworkLocationProvider(locationManager);

        networkLocationProvider.setNext(networkLocationProvider);
        networkLocationProvider.setNext(new DeadEndLocationProvider());
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Location location = headLocationProvider.locate();
                Log.d(TAG, "location->" + location);
                if (location != null) {
                    //TODO store location
                } else {
                    //TODO
                }
                Thread.sleep(30 * 1000);
                Log.d(TAG, "after sleep");
            } catch (InterruptedException e) {
                Log.d(TAG, "interrupted");
                Thread.currentThread().interrupt();
            }
        }
        Log.d(TAG, "stopping");
        //TODO release resources: storage, something else ??
    }

}
