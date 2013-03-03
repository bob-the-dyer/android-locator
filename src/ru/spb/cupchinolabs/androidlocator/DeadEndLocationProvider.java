package ru.spb.cupchinolabs.androidlocator;

import android.location.Location;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:10
 */
public class DeadEndLocationProvider extends AbstractChainedLocationProvider {

    @Override
    protected Location provideLocation() {
        return null;  //TODO notify
    }

    @Override
    public AbstractChainedLocationProvider setNext(AbstractChainedLocationProvider nextProvider) {
        throw new UnsupportedOperationException("You can't set next provider to DeadEndLocationProvider");
    }
}
