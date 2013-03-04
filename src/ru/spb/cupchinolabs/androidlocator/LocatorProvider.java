package ru.spb.cupchinolabs.androidlocator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static ru.spb.cupchinolabs.androidlocator.LocatorProviderContract.*;
import static ru.spb.cupchinolabs.androidlocator.LocatorProviderContract.Location.*;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 04.03.13
 * Time: 23:30
 * <p/>
 * <p/>
 * TODO rework to store locations in SQLite
 * <p/>
 * TODO think of a better solution for provider's thread safety
 */
public class LocatorProvider extends ContentProvider {

    private static final String TAG = LocatorProvider.class.getSimpleName();

    private UriMatcher uriMatcher;

    private List<Location> locations;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");

        uriMatcher = new UriMatcher(0);

        uriMatcher.addURI(AUTHORITY, LOCATION_TABLE_NAME,        1);
        uriMatcher.addURI(AUTHORITY, LOCATION_TABLE_NAME + "/#", 2);

        locations = new ArrayList<>();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");

        MatrixCursor matrixCursor = null;

        switch (uriMatcher.match(uri)) {
            case 1:
                synchronized (locations) {
                    matrixCursor = new MatrixCursor(projections, locations.size());
                    for (Location location : locations) {
                        buildRow(projections, matrixCursor, location);
                    }
                }
                break;
            case 2:
                synchronized (locations) {
                    matrixCursor = new MatrixCursor(projections, 1);
                    Location location = locations.get(Integer.valueOf(uri.getLastPathSegment()));
                    buildRow(projections, matrixCursor, location);
                }
                break;
            default: {
                //TODO handle unsupported URI
            }
        }
        return matrixCursor;
    }

    private void buildRow(String[] projections, MatrixCursor matrixCursor, Location location) {
        MatrixCursor.RowBuilder builder = matrixCursor.newRow();
        for (String projection : projections) {
            switch (projection) {
                case PROVIDER:
                    builder.add(location.getProvider());
                    break;
                case TIME:
                    builder.add(location.getTime());
                    break;
                case LONGITUDE:
                    builder.add(location.getLongitude());
                    break;
                case LATITUDE:
                    builder.add(location.getLatitude());
                    break;
                default: {
                    //TODO handle not existed projection
                }
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(TAG, "insert");

        int index;

        synchronized (locations) {
            Location location = new Location(contentValues.getAsString(PROVIDER));

            location.setTime(contentValues.getAsLong(TIME));
            location.setLongitude(contentValues.getAsDouble(LONGITUDE));
            location.setLatitude(contentValues.getAsDouble(LATITUDE));

            locations.add(location);

            index = locations.size();

            Log.d(TAG, "inserted location->" + location);
            Log.d(TAG, "locations.size()->" + locations.size());
        }
        return Uri.parse(PROVIDER_URI + index);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
