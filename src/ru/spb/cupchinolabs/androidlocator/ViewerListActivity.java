package ru.spb.cupchinolabs.androidlocator;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 04.03.13
 * Time: 21:46
 */
public class ViewerListActivity extends ListActivity {

    public static final String TAG = ViewerListActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        if (savedInstanceState != null) {
            //TODO restore current position in list
        }

        Cursor cursor = getContentResolver().query(
                Uri.parse(LocatorProviderContract.LOCATION_TABLE_URI),
                new String[]{
                        LocatorProviderContract.Location._ID,
                        LocatorProviderContract.Location.TIME,
                        LocatorProviderContract.Location.LATITUDE,
                        LocatorProviderContract.Location.LONGITUDE,
                        LocatorProviderContract.Location.PROVIDER
                },
                null,
                new String[]{""},
                LocatorProviderContract.Location._ID + " DESC"); //TODO or by TIME?

        if (null == cursor) {

            Log.d(TAG, "error occurred, returning null cursor");

        } else if (cursor.getCount() < 1) {

            Log.d(TAG, "cursor returns an empty result");

            //TODO show empty message
        } else {
            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    this,
                    R.layout.viewer_list_entry,
                    cursor,
                    new String[]{
                            LocatorProviderContract.Location.TIME,
                            LocatorProviderContract.Location.PROVIDER,
                            LocatorProviderContract.Location.LATITUDE,
                            LocatorProviderContract.Location.LONGITUDE
                    },
                    new int[]{R.id.time, R.id.provider, R.id.latitude, R.id.longitude});
            setListAdapter(cursorAdapter);
            //TODO  notifyDataSetChanged()
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        //TODO retrieve fresh list?

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        //TODO save current position in list (scroll position of a ListView?)
    }
}