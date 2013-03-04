package ru.spb.cupchinolabs.androidlocator.locators;

import android.location.Location;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:02
 */
abstract public class AbstractChainedLocator implements Locator {

    private Locator next;

    public AbstractChainedLocator setNext(AbstractChainedLocator next) {
        this.next = next;
        return this;
    }

    abstract protected Location locateImpl();

    @Override
    public Location locate() {
        Location location = locateImpl();
        if (location != null) {
            return location;
        } else if (next != null) {
            return next.locate();
        } else {
            return null;
        }
    }
}
