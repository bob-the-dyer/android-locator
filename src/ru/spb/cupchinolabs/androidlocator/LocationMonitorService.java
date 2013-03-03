package ru.spb.cupchinolabs.androidlocator;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;

import static ru.spb.cupchinolabs.androidlocator.Utils.print;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 02.03.13
 * Time: 9:49
 */
public class LocationMonitorService extends Service {

    private static final String TAG = "LocationMonitorService";

    private Thread workerThread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifyOnStartCommand();
        workerThread = new LocationMonitorServiceWorker(
                (LocationManager) getSystemService(Context.LOCATION_SERVICE));
        workerThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        print("Location monitor service - someone is binding ???", this, TAG);
        return null;
    }

    @Override
    public void onCreate() {
        print("Location monitor service - creating ...", this, TAG);
    }

    @Override
    public void onDestroy() {
        //TODO release all the resources
        workerThread.interrupt();
        notifyOnDestroy();
    }

    private void notifyOnDestroy() {
        //TODO replace Toast with notification
        print("Location monitor service - destroying ...", this, TAG);
        stopForeground(true);
    }

    private void notifyOnStartCommand() {
        Notification notification = new Notification(
                R.drawable.triangle, getText(R.string.locator_notification_ticker_text), System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, LocationMonitorActivity.class), 0);
        notification.setLatestEventInfo(this, getText(R.string.locator_notification_content_title),
                getText(R.string.locator_notification_content_text), pendingIntent);

        startForeground(R.string.locator_notification_content_title, notification);
        print("Location monitor service - starting ...", this, TAG);
    }
}
