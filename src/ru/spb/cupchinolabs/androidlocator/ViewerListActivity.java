package ru.spb.cupchinolabs.androidlocator;

import android.app.ListActivity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 04.03.13
 * Time: 21:46
 */
public class ViewerListActivity extends ListActivity {

    private static final String TAG = ViewerListActivity.class.getSimpleName();

    private static final Uri PARSED_LOCATION_URI = Uri.parse(LocatorProviderContract.LOCATION_TABLE_URI);

    private final static String[] PROJECTION = {
            LocatorProviderContract.Location._ID,
            LocatorProviderContract.Location.TIME,
            LocatorProviderContract.Location.LATITUDE,
            LocatorProviderContract.Location.LONGITUDE,
            LocatorProviderContract.Location.PROVIDER
    };

    private final static String[] FROM_COLUMNS = {
            LocatorProviderContract.Location.TIME,
            LocatorProviderContract.Location.PROVIDER,
            LocatorProviderContract.Location.LATITUDE,
            LocatorProviderContract.Location.LONGITUDE
    };

    private final static int[] TO_IDS = {
            R.id.time,
            R.id.provider,
            R.id.latitude,
            R.id.longitude
    };

    private ContentObserver contentObserver;
    private CursorAdapter cursorAdapter;
    private Handler handler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        handler = new Handler();
        Cursor cursor = createNewCursor();//TODO this is potentially a long-running operation, ANR is possible?
        if (null == cursor) {
            throw new IllegalStateException("error occurred -> provider is returning null as a cursor, URI is wrong");
        } else if (cursor.getCount() < 1) {
            Log.d(TAG, "cursor returns an empty result");
            //TODO show empty message
        }
        cursorAdapter = new ViewerListAdapter(cursor);
        setListAdapter(cursorAdapter);
        contentObserver = new ProviderContentObserver();
        getContentResolver().registerContentObserver(PARSED_LOCATION_URI, true, contentObserver);
    }

    private Cursor createNewCursor() {
        return getContentResolver().query( //TODO query not all rows
                PARSED_LOCATION_URI, PROJECTION, null, new String[]{}, LocatorProviderContract.Location._ID + " DESC");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        getContentResolver().unregisterContentObserver(contentObserver);
        contentObserver = null;
        cursorAdapter = null;
        handler = null;
    }

    private class ProviderContentObserver extends ContentObserver {

        private final String TAG = ViewerListActivity.TAG + "-ContentObserver";

        public ProviderContentObserver() {
            super(ViewerListActivity.this.handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG, "onChange");
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object... objects) {
                    final Cursor newCursor = createNewCursor();
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (cursorAdapter != null) {
                                    //TODO think of adding inserted entries to current underlying model instead of new cursor
                                    cursorAdapter.changeCursor(newCursor);
                                    cursorAdapter.notifyDataSetChanged();
                                } else {
                                    Log.d(TAG, "cursorAdapter is null, skipping");
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "handler is null, skipping");
                    }
                    return null;
                }
            }.execute();
        }
    }

    private class ViewerListAdapter extends SimpleCursorAdapter {

        public ViewerListAdapter(Cursor cursor) {
            super(ViewerListActivity.this, R.layout.viewer_list_entry, cursor, ViewerListActivity.FROM_COLUMNS, ViewerListActivity.TO_IDS);
        }

        @Override
        public void setViewText(TextView view, String text) {
            if (view.getId() == R.id.time) {
                super.setViewText(view, DateFormat.getDateTimeInstance().format(new Date(Long.parseLong(text))));
            } else {
                super.setViewText(view, text);
            }
        }
    }
}