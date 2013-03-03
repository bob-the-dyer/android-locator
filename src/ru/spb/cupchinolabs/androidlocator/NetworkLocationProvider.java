package ru.spb.cupchinolabs.androidlocator;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:12
 */
public class NetworkLocationProvider extends AbstractChainedLocationProvider {

    private LocationManager locationManager;

    public NetworkLocationProvider(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    @Override
    protected Location provideLocation() {
        return null;  //TODO
    }
}
