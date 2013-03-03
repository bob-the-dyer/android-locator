package ru.spb.cupchinolabs.androidlocator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import static ru.spb.cupchinolabs.androidlocator.Utils.print;

public class LocationMonitorActivity extends Activity {

    private static final String TAG = LocationMonitorActivity.class.getSimpleName();

    private static final String IS_ON = "IS_ON";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_monitor_control);

        ((ToggleButton) findViewById(R.id.togglebutton))
                .setChecked(getPreferences(Context.MODE_PRIVATE).getBoolean(IS_ON, false));

        print("onCreate", null, TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        print("onStart", null, TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        print("onStop", null, TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_ON, ((ToggleButton) findViewById(R.id.togglebutton)).isChecked())
                .commit();

        print("onDestroy", null, TAG);
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            startService(new Intent(LocationMonitorService.class.getName()));
        } else {
            stopService(new Intent(LocationMonitorService.class.getName()));
        }
        print("onToggleClicked:" + on, null, TAG);
    }

}
