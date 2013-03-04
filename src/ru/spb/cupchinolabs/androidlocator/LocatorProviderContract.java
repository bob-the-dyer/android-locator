package ru.spb.cupchinolabs.androidlocator;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 05.03.13
 * Time: 0:26
 */
public final class LocatorProviderContract {

    public static final String AUTHORITY = "ru.spb.cupchinolabs.androidlocator.provider";
    public static final String PROVIDER_URI = "content://" + AUTHORITY;
    public static final String LOCATION_TABLE_NAME = "location";
    public static final String LOCATION_TABLE_URI = PROVIDER_URI + "/" + LOCATION_TABLE_NAME;


    public final class Location {
        public static final String PROVIDER = "PROVIDER";
        public static final String TIME = "TIME";
        public static final String LONGITUDE = "LONGITUDE";
        public static final String LATITUDE = "LATITUDE";
        public static final String _ID = "_ID";
    }
}
