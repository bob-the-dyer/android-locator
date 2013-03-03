package ru.spb.cupchinolabs.androidlocator;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import static ru.spb.cupchinolabs.androidlocator.Utils.print;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:11
 */
public class GpsLocationProvider extends AbstractChainedLocationProvider {

    private LocationManager locationManager;
    private int timeout;

    public GpsLocationProvider(LocationManager locationManager, int timeout) {
        this.locationManager = locationManager;
        this.timeout = timeout;
    }

    @Override
    protected Location provideLocation() {

        final Location[] lastLocation = new Location[1];

//        LocationListener locationListener = new LocationListener() {
//
//            private static final String TAG = "LocationMonitorListener";
//
//            public void onLocationChanged(Location location) {
//                print("onLocationChanged:" + location, null, TAG);
//                lastLocation[0] = location;
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//                print("onStatusChanged:status->" + status + ",provider->" + provider, null, TAG);
//            }
//
//            public void onProviderEnabled(String provider) {
//                print("onProviderEnabled:" + provider, null, TAG);
//            }
//
//            public void onProviderDisabled(String provider) {
//                print("onProviderDisabled:" + provider, null, TAG);
//            }
//        };
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
//        try {
//            Thread.sleep(timeout * 1000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        locationManager.removeUpdates(locationListener);
        return lastLocation[0];
    }

}
