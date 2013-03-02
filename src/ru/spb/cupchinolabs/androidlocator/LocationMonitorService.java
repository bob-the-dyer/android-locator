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

    protected static final int MSG_START = 1;
    protected static final int MSG_STOP  = 2;

    private static final String TAG = "LocationMonitorService";
    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
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

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        String text = "Location monitor service - binding ...";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
        return messenger.getBinder();
    }

    @Override
    public void onCreate() {
//        The system calls this method when the service is first created, to perform one -time setup procedures (before
//        it calls either onStartCommand () or onBind ()).If the service is already running, this method is not called.
        //TODO init ContentProvider, check wifi, gps?
        String text = "Location monitor service - creating ...";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
    }

    @Override
    public void onDestroy() {
//        The system calls this method when the service is no longer used and is being destroyed.Your service
//        should implement this to clean up any resources such as threads, registered listeners, receivers, etc.This
//        is the last call the service receives.
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
        Notification notification = new Notification(R.drawable.triangle, getText(R.string.locator_notification_ticker_text),
                System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, LocationMonitorAction.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.setLatestEventInfo(this, getText(R.string.locator_notification_content_title),
                getText(R.string.locator_notification_content_text), pendingIntent);
        startForeground(R.string.locator_notification_content_title, notification);
        String text = "Location monitor service is starting ...";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
    }
}
