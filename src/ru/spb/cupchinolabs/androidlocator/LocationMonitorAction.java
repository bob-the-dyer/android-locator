package ru.spb.cupchinolabs.androidlocator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class LocationMonitorAction extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_monitor_control);
        //TODO check liveness of service
        //TODO set toggle button value
    }


    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        Intent intent = new Intent(this, LocationMonitorService.class);
        if (on) {
            startService(intent);
        } else {
            stopService(intent);
        }
    }

    //TODO startService
    //TODO stopService
    //bindService() - onStartCommand() is not called
    //unbindService()
}
