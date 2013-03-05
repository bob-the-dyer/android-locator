package ru.spb.cupchinolabs.androidlocator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * <p/>
 * TODO seems like it survives longer then I expect, clarify ContentProvider lifecycle.
 * Test case: locator on, then off and back button -> app is expected to be closed? provider is still alive
 */
public class LocatorProvider extends ContentProvider {

    private static final String TAG = LocatorProvider.class.getSimpleName();

    private UriMatcher uriMatcher;

    private List<Location> locations;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");

        uriMatcher = new UriMatcher(0);

        uriMatcher.addURI(AUTHORITY, LOCATION_TABLE_NAME, 1);
        uriMatcher.addURI(AUTHORITY, LOCATION_TABLE_NAME + "/#", 2);

        locations = new ArrayList<>(); //TODO deserialize and serialize on insert
        return true;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");

        //TODO check whether locations was initialized, if not -> read the file, if there is no file -> create file -> open a handle

        //TODO implement sortOrder

        //TODO implement selection and selectionArgs

        MatrixCursor matrixCursor = null;

        switch (uriMatcher.match(uri)) {
            case 1:
                matrixCursor = new MatrixCursor(projections, locations.size());
                for (int i = locations.size() - 1; i >= 0; i--) {
                    Location location = locations.get(i);
                    buildRow(projections, matrixCursor, location, i);
                }
                break;
            case 2:
                matrixCursor = new MatrixCursor(projections, 1);
                int id = Integer.valueOf(uri.getLastPathSegment());
                Location location = locations.get(id);
                buildRow(projections, matrixCursor, location, id);
                break;
            default: {
                //TODO handle unsupported URI
            }
        }
        return matrixCursor;
    }

    private void buildRow(String[] projections, MatrixCursor matrixCursor, Location location, int id) {
        MatrixCursor.RowBuilder builder = matrixCursor.newRow();
        for (String projection : projections) {
            switch (projection) {
                case PROVIDER:
                    builder.add(location.getProvider());
                    break;
                case TIME:
                    builder.add(DateFormat.getDateTimeInstance().format(new Date(location.getTime())));
                    break;
                case LONGITUDE:
                    builder.add(location.getLongitude());
                    break;
                case LATITUDE:
                    builder.add(location.getLatitude());
                    break;
                case _ID:
                    builder.add(id);
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
    public synchronized Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(TAG, "insert");

        //TODO if no file -> create file and initialize handle

        int index;

        Location location = new Location(contentValues.getAsString(PROVIDER));

        location.setTime(contentValues.getAsLong(TIME));
        location.setLongitude(contentValues.getAsDouble(LONGITUDE));
        location.setLatitude(contentValues.getAsDouble(LATITUDE));

        locations.add(location);

        index = locations.size();

        Log.d(TAG, "inserted location->" + location);
        Log.d(TAG, "locations.size()->" + locations.size());

        //TODO write to the file asynchronously

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(PROVIDER_URI + "/" + index);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new IllegalArgumentException("delete is not supported");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new IllegalArgumentException("update is not supported");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        saveMyself();
    }

    private void saveMyself() {
        Log.d(TAG, "saveMyself");
    }
}
