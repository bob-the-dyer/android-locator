package ru.spb.cupchinolabs.androidlocator.locators;

import android.location.Location;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:10
 */
public class DeadEndLocator extends AbstractChainedLocator {

    @Override
    protected Location locateImpl() {
        return null;  //TODO notify
    }

    @Override
    public AbstractChainedLocator setNext(AbstractChainedLocator next) {
        throw new UnsupportedOperationException("You can't set next provider to DeadEndLocator");
    }
}
