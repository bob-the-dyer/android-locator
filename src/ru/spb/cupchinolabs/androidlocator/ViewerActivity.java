package ru.spb.cupchinolabs.androidlocator;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 04.03.13
 * Time: 21:46
 */
public class ViewerActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO use CursorLoader to load data async or another thread
        // see http://developer.android.com/guide/components/loaders.html

        String[] mProjection =
                {
                        LocatorProviderContract.Location._ID,
                        LocatorProviderContract.Location.TIME,
                        LocatorProviderContract.Location.LATITUDE,
                        LocatorProviderContract.Location.LONGITUDE,
                        LocatorProviderContract.Location.PROVIDER
                };

        // Defines a string to contain the selection clause
        String mSelectionClause = null;

        // Initializes an array to contain selection arguments
        String[] mSelectionArgs = {""};

        Object mSortOrder;
        Cursor cursor = getContentResolver().query(
                Uri.parse(LocatorProviderContract.LOCATION_TABLE_URI),   // The content URI of the words table
                mProjection,                        // The columns to return for each row
                mSelectionClause,                    // Selection criteria
                mSelectionArgs,                      // Selection criteria
                LocatorProviderContract.Location.TIME);// The sort order for the returned rows

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {

        } else if (cursor.getCount() < 1) {

        } else {

        }

        // Defines a list of columns to retrieve from the Cursor and load into an output row
        String[] mWordListColumns =
                {
                        LocatorProviderContract.Location.TIME,
                        LocatorProviderContract.Location.LATITUDE,
                        LocatorProviderContract.Location.LONGITUDE,
                        LocatorProviderContract.Location.PROVIDER
                };
/*
        // Defines a list of View IDs that will receive the Cursor columns for each row
        int[] mWordListItems = { R.id.time, R.id.latitude, R.id.longitude};

        // Creates a new SimpleCursorAdapter
        SimpleCursorAdapter mCursorAdapter = new SimpleCursorAdapter(
                getApplicationContext(),               // The application's Context object
                R.layout.wordlistrow,                  // A layout in XML for one row in the ListView
                cursor,                               // The result from the query
                mWordListColumns,                      // A string array of column names in the cursor
                mWordListItems,                        // An integer array of view IDs in the row layout
                0);                                    // Flags (usually none are needed)

        // Sets the adapter for the ListView
        mWordList.setAdapter(mCursorAdapter);
 */
    }
}