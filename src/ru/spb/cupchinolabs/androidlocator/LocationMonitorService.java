package ru.spb.cupchinolabs.androidlocator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 02.03.13
 * Time: 9:49
 */
public class LocationMonitorService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    // The system calls this method when another component, such as an activity, requests that the service be started,
    // by calling startService()
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
//        The system calls this method when another component wants to bind with the service (such as to perform RPC),
//        by calling bindService().In your implementation of this method, you must provide an
//        interface that clients use to communicate with the service,by returning an IBinder.You must always implement
//        this method, but if you don 't want to allow binding, then you should return null.
        return null;
    }

    @Override
    public void onCreate() {
//        The system calls this method when the service is first created, to perform one -time setup procedures (before
//        it calls either onStartCommand () or onBind ()).If the service is already running, this method is not called.
        //TODO startForeground()?
        //TODO Status Bar Notification ?
        //TODO start worker thread (see HandlerThread, Looper)
        Toast.makeText(this, "Location monitor service is starting ...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
//        The system calls this method when the service is no longer used and is being destroyed.Your service
//        should implement this to clean up any resources such as threads, registered listeners, receivers, etc.This
//        is the last call the service receives.
        Toast.makeText(this, "Location monitor service is stopping ...", Toast.LENGTH_SHORT).show();
        //TODO stopForeground()?
        //TODO stop worker thread
    }
}