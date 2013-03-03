package ru.spb.cupchinolabs.androidlocator;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import static ru.spb.cupchinolabs.androidlocator.Utils.print;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 03.03.13
 * Time: 15:12
 */
public class NetworkLocationProvider extends AbstractChainedLocationProvider {

    private LocationManager locationManager;
    private int timeout;

    public NetworkLocationProvider(LocationManager locationManager, int timeout) {
        this.locationManager = locationManager;
        this.timeout = timeout;
    }

    @Override
    protected Location provideLocation() {
        final Location[] lastLocation = new Location[1];

        final LocationListener locationListener = new LocationListener() {

            private static final String TAG = "LocationMonitorListener";

            public void onLocationChanged(Location location) {
                print("onLocationChanged:" + location, null, TAG);
                lastLocation[0] = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                print("onStatusChanged:status->" + status + ",provider->" + provider, null, TAG);
            }

            public void onProviderEnabled(String provider) {
                print("onProviderEnabled:" + provider, null, TAG);
            }

            public void onProviderDisabled(String provider) {
                print("onProviderDisabled:" + provider, null, TAG);
            }
        };

              new Handler(){}.post(new Runnable() {
                  @Override
                  public void run() {
                      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                  }
              });


        try {
            Thread.sleep(timeout * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        locationManager.removeUpdates(locationListener);
        return lastLocation[0];

    }

//    class LooperThread extends Thread {
//
//        public Handler mHandler;
//
//        public void run() {
//            Looper.prepare();
//
//            mHandler = new Handler() {
//                public void handleMessage(Message msg) {
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//                }
//            };
//
//            Looper.loop();
//        }
//    }
}
