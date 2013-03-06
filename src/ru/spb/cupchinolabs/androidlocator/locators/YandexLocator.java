package ru.spb.cupchinolabs.androidlocator.locators;

import android.location.Location;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 06.03.13
 * Time: 19:24
 */
public class YandexLocator extends AbstractChainedLocator {

    private static final String TAG = YandexLocator.class.getSimpleName();

    private final int timeout;

    public YandexLocator(int timeout) {
        this.timeout = timeout;
    }

    @Override
    protected Location locateImpl() {
        return null;
    }

}
