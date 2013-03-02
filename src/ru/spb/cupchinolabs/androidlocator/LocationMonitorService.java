package ru.spb.cupchinolabs.androidlocator;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 02.03.13
 * Time: 9:49
 */
public class LocationMonitorService extends Service {

    private static final String TAG = "LocationMonitorService";

    protected static final int MSG_START = 1;
    protected static final int MSG_STOP = 2;

    final private Messenger messenger = new Messenger(new IncomingHandler());

    /**
     * Handler of incoming messages from clients.
     */
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    notifyOnStart();
                    //TODO start worker thread (see HandlerThread, Looper)
                    //TODO replace Toast with notification
                    //TODO I must be undestroyable
                    break;
                case MSG_STOP:
                    notifyOnStop();
                    //TODO stop worker thread
                    //TODO I must be destroyable
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        String text = "Location monitor service - binding ...";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
        //TODO init ContentProvider, check wifi, gps?
        String text = "Location monitor service - creating ...";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
    }

    @Override
    public void onDestroy() {
        //TODO release all the resources
        String text = "Location monitor service - destroying ...";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
    }

    private void notifyOnStop() {
        //TODO replace Toast with notification
        String text = "Location monitor service is stopping ...";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
        stopForeground(true);
    }

    private void notifyOnStart() {

        Notification notification = new Notification(
                R.drawable.triangle, getText(R.string.locator_notification_ticker_text), System.currentTimeMillis());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, LocationMonitorAction.class), 0);

        notification.setLatestEventInfo(this, getText(R.string.locator_notification_content_title),
                getText(R.string.locator_notification_content_text), pendingIntent);

        startForeground(R.string.locator_notification_content_title, notification);

        String text = "Location monitor service is starting ...";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
    }
}
