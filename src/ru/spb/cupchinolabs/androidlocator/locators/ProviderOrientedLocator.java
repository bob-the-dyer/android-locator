package ru.spb.cupchinolabs.androidlocator.locators;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import ru.spb.cupchinolabs.androidlocator.Utils;

import java.util.concurrent.atomic.AtomicReference;

import static ru.spb.cupchinolabs.androidlocator.Utils.print;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:11
 */
public class ProviderOrientedLocator extends AbstractChainedLocator {

    private static final String TAG = ProviderOrientedLocator.class.getSimpleName();

    private final String providerName;
    private final LocationManager locationManager;
    private final Handler handler;
    private final int timeout;

    public ProviderOrientedLocator(String providerName, LocationManager locationManager, Handler handler, int timeout) {
        this.providerName = providerName;
        this.locationManager = locationManager;
        this.handler = handler;
        this.timeout = timeout;
    }

    @Override
    protected Location locateImpl() {

        final AtomicReference<Location> reference = new AtomicReference<>();

        final LocationListener listener =
                new LocationUpdatesListener(reference, providerName);

        handler.post(new Runnable() {
            @Override
            public void run() {
                locationManager.requestLocationUpdates(providerName, 0, 0, listener);
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
                locationManager.removeUpdates(listener);
            }
        });

        return reference.get();
    }

    private static class LocationUpdatesListener implements LocationListener {

        private final String TAG;
        private final AtomicReference<Location> locationHolder;

        public LocationUpdatesListener(AtomicReference<Location> locationHolder, String providerName) {
            this.locationHolder = locationHolder;
            TAG = ProviderOrientedLocator.TAG + "-listener-" + providerName;
        }

        public void onLocationChanged(Location location) {
            print("onLocationChanged:" + location, null, TAG);

            if (Utils.isBetterLocation(location, locationHolder.get())) {
                locationHolder.set(location);
            }
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