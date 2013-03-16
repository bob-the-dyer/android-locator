package ru.spb.cupchinolabs.androidlocator;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 06.03.13
 * Time: 15:53
 */
public class PersistentLocatorProvider extends ContentProvider {

    private static final String TAG = PersistentLocatorProvider.class.getSimpleName();

    private LocatorDbHelper dbHelper;
    private SQLiteDatabase db;

    private static final int INCOMING_LOCATION_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_LOCATION_URI_INDICATOR = 2;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(LocatorProviderContract.AUTHORITY, "location", INCOMING_LOCATION_COLLECTION_URI_INDICATOR);
        uriMatcher.addURI(LocatorProviderContract.AUTHORITY, "location/#", INCOMING_SINGLE_LOCATION_URI_INDICATOR);
    }

    private static Map<String, String> projectionMap;

    static {
        projectionMap = new HashMap<>();
        projectionMap.put(LocatorProviderContract.Location._ID, LocatorProviderContract.Location._ID);
        projectionMap.put(LocatorProviderContract.Location.TIME, LocatorProviderContract.Location.TIME);
        projectionMap.put(LocatorProviderContract.Location.LATITUDE, LocatorProviderContract.Location.LATITUDE);
        projectionMap.put(LocatorProviderContract.Location.LONGITUDE, LocatorProviderContract.Location.LONGITUDE);
        projectionMap.put(LocatorProviderContract.Location.PROVIDER, LocatorProviderContract.Location.PROVIDER);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new LocatorDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case INCOMING_LOCATION_COLLECTION_URI_INDICATOR:
                qb.setTables(LocatorProviderContract.LOCATION_TABLE_NAME);
                qb.setProjectionMap(projectionMap);
                break;
            case INCOMING_SINGLE_LOCATION_URI_INDICATOR:
                qb.setTables(LocatorProviderContract.LOCATION_TABLE_NAME);
                qb.setProjectionMap(projectionMap);
                qb.appendWhere(LocatorProviderContract.Location._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = LocatorProviderContract.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        db = dbHelper.getReadableDatabase();

        Cursor cursor = qb.query(db, projections, selection, selectionArgs, null, null, orderBy);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(TAG, "insert");

        db = dbHelper.getWritableDatabase();

        //TODO probably uri should be analyzed instead of hardcoded table name
        long newRowId = db.insert(LocatorProviderContract.LOCATION_TABLE_NAME, null, contentValues);

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case INCOMING_LOCATION_COLLECTION_URI_INDICATOR:
                return LocatorProviderContract.CONTENT_TYPE;
            case INCOMING_SINGLE_LOCATION_URI_INDICATOR:
                return LocatorProviderContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new IllegalArgumentException("delete is not supported");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new IllegalArgumentException("update is not supported");
    }


}
