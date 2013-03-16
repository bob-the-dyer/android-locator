package ru.spb.cupchinolabs.androidlocator;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 05.03.13
 * Time: 0:26
 */
public final class LocatorProviderContract {

    public static final String AUTHORITY = "ru.spb.cupchinolabs.androidlocator.provider";
    public static final String LOCATOR_PROVIDER_URI = "content://" + AUTHORITY;
    public static final String LOCATION_TABLE_NAME = "location";
    public static final String LOCATION_TABLE_URI = LOCATOR_PROVIDER_URI + "/" + LOCATION_TABLE_NAME;
    public static final String DEFAULT_SORT_ORDER = Location._ID + " DESC";

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.androidlocator." + LOCATION_TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.androidlocator." + LOCATION_TABLE_NAME;

    public final class Location implements BaseColumns {
        public static final String PROVIDER = "PROVIDER";
        public static final String TIME = "TIME";
        public static final String LONGITUDE = "LONGITUDE";
        public static final String LATITUDE = "LATITUDE";
    }

    private LocatorProviderContract() {
    }

}
