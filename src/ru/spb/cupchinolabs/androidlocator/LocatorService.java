package ru.spb.cupchinolabs.androidlocator;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import ru.spb.cupchinolabs.androidlocator.locators.DeadEndLocator;
import ru.spb.cupchinolabs.androidlocator.locators.Locator;
import ru.spb.cupchinolabs.androidlocator.locators.ProviderOrientedLocator;
import ru.spb.cupchinolabs.androidlocator.locators.yandex.NetworkDataBuilder;
import ru.spb.cupchinolabs.androidlocator.locators.yandex.YandexLocator;

import java.util.Timer;
import java.util.TimerTask;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static ru.spb.cupchinolabs.androidlocator.LocatorProviderContract.LOCATION_TABLE_URI;
import static ru.spb.cupchinolabs.androidlocator.LocatorProviderContract.Location.*;
import static ru.spb.cupchinolabs.androidlocator.Utils.print;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 02.03.13
 * Time: 9:49
 */
public class LocatorService extends Service {

    private static final String TAG = LocatorService.class.getSimpleName();

    private static final int GPS_TIMEOUT_IN_SEC = 60;
    private static final int NETWORK_TIMEOUT_IN_SEC = 10;
    private static final int YANDEX_TIMEOUT_IN_SEC = 20;

    private static final int TASK_PAD_INTERVAL_IN_SEC = 30;

    private static final int TASK_REPEAT_INTERVAL_IN_SEC =
            Math.max(
                    Math.max(GPS_TIMEOUT_IN_SEC, NETWORK_TIMEOUT_IN_SEC),
                    YANDEX_TIMEOUT_IN_SEC
            ) + TASK_PAD_INTERVAL_IN_SEC;

    private Handler handler;
    private Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        return START_STICKY;//TODO think about it again
    }

    @Override
    public void onCreate() {
        print("Locator service - creating ...", null, TAG);

        handler = new Handler();

        timer = new Timer("LMS-timer");

        Locator locator = createLocatorChainOfResponsibilities();

        timer.schedule(new LMSTimerTask(locator, getContentResolver()), 1000, TASK_REPEAT_INTERVAL_IN_SEC * 1000);
    }

    private Locator createLocatorChainOfResponsibilities() {
        LocationManager manager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return
                new ProviderOrientedLocator(GPS_PROVIDER, manager, handler, GPS_TIMEOUT_IN_SEC)
                        .setNext(new YandexLocator(new NetworkDataBuilder(YANDEX_TIMEOUT_IN_SEC, this), YANDEX_TIMEOUT_IN_SEC)
                                .setNext(new ProviderOrientedLocator(NETWORK_PROVIDER, manager, handler, NETWORK_TIMEOUT_IN_SEC)
                                        .setNext(new DeadEndLocator())));
    }

    @Override
    public void onDestroy() {
        stopForeground();
        timer.cancel();
        timer = null;
        handler = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopForeground() {
        stopForeground(true);
        print(getText(R.string.locator_is_off_notification_content_text).toString(), this, TAG);
    }

    private void startForeground() {
        //TODO rework with NotificationManager
        Notification notification = new Notification(
                R.drawable.triangle, getText(R.string.locator_notification_ticker_text), System.currentTimeMillis());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, ControlActivity.class), 0);

        notification.setLatestEventInfo(this, getText(R.string.locator_notification_content_title),
                getText(R.string.locator_is_on_notification_content_text), pendingIntent);

        print(getText(R.string.locator_is_on_notification_content_text).toString(), this, TAG);

        startForeground(R.string.locator_notification_content_title, notification);
    }

    private static class LMSTimerTask extends TimerTask {

        private final Locator locator;
        private final ContentResolver contentResolver;

        public LMSTimerTask(Locator locator, ContentResolver contentResolver) {
            this.locator = locator;
            this.contentResolver = contentResolver;
        }

        @Override
        public void run() {

            Location location = locator.locate();

            Log.d(TAG, "location->" + location);

            if (location != null) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(TIME, location.getTime());
                contentValues.put(PROVIDER, location.getProvider());
                contentValues.put(LATITUDE, location.getLatitude());
                contentValues.put(LONGITUDE, location.getLongitude());

                contentResolver.insert(Uri.parse(LOCATION_TABLE_URI), contentValues);
            } else {
                //TODO nothing
            }
            //TODO notify of new provider was enabled
            //TODO notify of null location just once - first time
            //TODO propose to open settings in case of no providers available
        }
    }
}
