package ru.spb.cupchinolabs.androidlocator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 06.03.13
 * Time: 15:42
 */
public class LocatorDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "Locator.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LocatorProviderContract.LOCATION_TABLE_NAME + " (" +
                    LocatorProviderContract.Location._ID + " INTEGER PRIMARY KEY, " +
                    LocatorProviderContract.Location.TIME + " INTEGER, " +
                    LocatorProviderContract.Location.LATITUDE + " REAL, " +
                    LocatorProviderContract.Location.LONGITUDE + " REAL, " +
                    LocatorProviderContract.Location.PROVIDER + " TEXT" +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LocatorProviderContract.LOCATION_TABLE_NAME;

    public LocatorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
