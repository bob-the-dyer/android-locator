package ru.spb.cupchinolabs.androidlocator;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

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

    @Override
    public boolean onCreate() {
        dbHelper = new LocatorDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query");

        db = dbHelper.getReadableDatabase();

        return db.query(
                LocatorProviderContract.LOCATION_TABLE_NAME,
                projections,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Log.d(TAG, "insert");

        db = dbHelper.getWritableDatabase();

        long newRowId = db.insert(LocatorProviderContract.LOCATION_TABLE_NAME, null, contentValues);

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, newRowId);
    }

    @Override
    public String getType(Uri uri) {
        return null;
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
