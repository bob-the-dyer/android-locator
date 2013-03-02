package ru.spb.cupchinolabs.androidlocator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

public class LocationMonitorAction extends Activity {

    private static final String TAG = "LocationMonitorAction";

    //TODO FIX BUG - handle navigation on back button, app shouldn't be destroyed

    private Messenger messenger;

    private boolean isBound;
    private ServiceConnection serviceConnection;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.location_monitor_control);
        //TODO check liveness of service ?
        //TODO set toggle button value ?

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        bindServiceIf(!isBound);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        unbindServiceIf(isBound);
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        Log.d(TAG, "onToggleClicked:" + on);
        int messageId;
        if (on) {
            messageId = LocationMonitorService.MSG_START;
        } else {
            messageId = LocationMonitorService.MSG_STOP;
        }
        Message msg = Message.obtain(null, messageId, 0, 0);
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unbindServiceIf(boolean unbind) {
        if (unbind) {
            unbindService(serviceConnection);
            serviceConnection = null;
            isBound = false;
        }
    }

    private void bindServiceIf(boolean bind) {
        if (bind){
            serviceConnection =  new ServiceConnection() {

                public void onServiceConnected(ComponentName className, IBinder service) {
                    messenger = new Messenger(service);
                    isBound = true;
                }

                public void onServiceDisconnected(ComponentName className) {
                    messenger = null;
                    isBound = false;
                }

            };
            bindService(new Intent(this, LocationMonitorService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

}
