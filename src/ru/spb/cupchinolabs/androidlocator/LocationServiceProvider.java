package ru.spb.cupchinolabs.androidlocator;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import static ru.spb.cupchinolabs.androidlocator.Utils.print;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:11
 */
public class LocationServiceProvider extends AbstractChainedLocationProvider {

    private static final String TAG = LocationServiceProvider.class.getSimpleName();

    private final String providerName;
    private final LocationManager locationManager;
    private final Handler handler;
    private final int timeout;

    public LocationServiceProvider(String providerName, LocationManager locationManager, Handler handler, int timeout) {
        this.providerName = providerName;
        this.locationManager = locationManager;
        this.handler = handler;
        this.timeout = timeout;
    }

    @Override
    protected Location provideLocation() {

        final Location[] locationHolder = new Location[1];

        final LocationListener locationListener =
                new OnLocationChangedListener(locationHolder);

        handler.post(new Runnable() {
            @Override
            public void run() {
                locationManager.requestLocationUpdates(providerName, 0, 0, locationListener);
            }
        });

        try {
            Thread.sleep(timeout * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                locationManager.removeUpdates(locationListener);
            }
        });

        return locationHolder[0];
    }

    private class OnLocationChangedListener implements LocationListener {

        private final String TAG;
        private final Location[] locationHolder;

        public OnLocationChangedListener(Location[] locationHolder) {
            this.locationHolder = locationHolder;
            TAG = LocationServiceProvider.TAG + "-listener-" + providerName;
        }

        public void onLocationChanged(Location location) {
            print("onLocationChanged:" + location, null, TAG);
            locationHolder[0] = location;
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
    }
}