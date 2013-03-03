package ru.spb.cupchinolabs.androidlocator;

import android.location.Location;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:02
 */
abstract public class AbstractChainedLocationProvider implements LocationProvider {

    private LocationProvider nextProvider;

    public void setNext(LocationProvider nextProvider) {
        this.nextProvider = nextProvider;
    }

    abstract protected Location provideLocation();

    @Override
    public Location locate() {
        Location location = provideLocation();
        if (location != null) {
            return location;
        } else if (nextProvider != null) {
            return nextProvider.locate();
        } else {
            return null;
        }
    }
}
