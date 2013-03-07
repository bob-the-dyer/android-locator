package ru.spb.cupchinolabs.androidlocator.locators;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 05.03.13
 * Time: 11:03
 * <p/>
 * TODO This class should become a lightweight replacement of a android.location.Location all over the project
 */
public class LocationVO {

    private long time;
    private double latitude;
    private double longitude;
    private String provider; //TODO here type is questionable: Enum, byte, ...

}
