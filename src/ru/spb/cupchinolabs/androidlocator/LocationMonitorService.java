package ru.spb.cupchinolabs.androidlocator;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static ru.spb.cupchinolabs.androidlocator.Utils.print;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 02.03.13
 * Time: 9:49
 */
public class LocationMonitorService extends Service {

    private static final String TAG = LocationMonitorService.class.getSimpleName();

    private static final int INTERVAL_IN_SEC = 10;
    private static final int TIMEOUT_IN_SEC  = 20;

    private Handler handler;
    private Timer timer;

    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            LocationManager manager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationProvider provider =
                    new LocationServiceProvider(GPS_PROVIDER, manager, handler, TIMEOUT_IN_SEC)
                            .setNext(new LocationServiceProvider(NETWORK_PROVIDER, manager, handler, TIMEOUT_IN_SEC)
                                    .setNext(new DeadEndLocationProvider()));

            Location location = provider.get();

            Log.d(TAG, "location->" + location);

            if (location != null) {
                //TODO store location
            } else {
                //TODO
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifyOnStartCommand();
        return START_STICKY;//TODO think about it  again
    }

    @Override
    public void onCreate() {
        print("Location monitor service - creating ...", this, TAG);
        handler = new Handler();
        timer = new Timer("LMS-timer");
        timer.schedule(timerTask, 1000L, INTERVAL_IN_SEC * 1000L);
    }

    @Override
    public void onDestroy() {
        notifyOnDestroy();
        timer.cancel();
        timer = null;
        handler = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
