package ru.spb.cupchinolabs.androidlocator.locators.yandex;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 07.03.13
 * Time: 11:17
 */
public class NetworkDataRetriever extends PhoneStateListener {

    private static final String TAG = NetworkDataRetriever.class.getSimpleName();

    private final int timeoutInSec;
    private final WifiManager wifiManager;
    private final TelephonyManager telephonyManager;

    public NetworkDataRetriever(int timeoutInSec, Context context) {
        this.timeoutInSec = timeoutInSec;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public NetworkData retrieve() {
        return
                new NetworkData()
                        .setGsmCellList(scanGsm())
                        .setWifiNetworkList(scanWifi())
                        .setIp(null); //TODO retrieve ip
    }

    private List<WifiNetwork> scanWifi() {
        Log.d(TAG, "scanWifi");

        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            wifiManager.startScan();
        } else {
            Log.d(TAG, "wifi is disabled, skipping");
            return null;
        }

        sleep();

        return buildWifiNetworks(wifiManager.getScanResults());
    }

    private List<GsmCell> scanGsm() {
        Log.d(TAG, "scanGsm");

        if (telephonyManager == null) {
            Log.d(TAG, "gsm is disabled, skipping");
            return null;
        }

        Log.d(TAG, "NetworkType is " + networkTypeStr.get(telephonyManager.getNetworkType()));

        String networkOperator = telephonyManager.getNetworkOperator();
        if (networkOperator != null && networkOperator.length() > 3) {
            Log.d(TAG, "countryCode and operatorId are unavailable, skipping");
            return null;
        }

        telephonyManager.listen(this,
                PhoneStateListener.LISTEN_SIGNAL_STRENGTH | PhoneStateListener.LISTEN_CELL_LOCATION | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

        sleep();

        telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);


        String countryCode = networkOperator.substring(0, 3);
        String operatorId = networkOperator.substring(3);

        return buildGcmCells(countryCode, operatorId);
    }

    private List<GsmCell> buildGcmCells(String countryCode, String operatorId) {
        List<GsmCell> result = new ArrayList<>();

        List<NeighboringCellInfo> cellList = telephonyManager.getNeighboringCellInfo();

        for (NeighboringCellInfo cell : cellList) {
            int cellId = cell.getCid();
            int lac = cell.getLac();

            int signalStrength = cell.getRssi();

            if (cellId == NeighboringCellInfo.UNKNOWN_CID) {
                cellId = cell.getPsc();
            }
            if (cellId != NeighboringCellInfo.UNKNOWN_CID) {
                String lacAsString = (lac != NeighboringCellInfo.UNKNOWN_CID) ? String.valueOf(lac) : "";
                String signalStrengthAsString = "";
                if (signalStrength != NeighboringCellInfo.UNKNOWN_RSSI) {
                    if ("gsm".equals(getRadioType(telephonyManager.getNetworkType()))) {
                        signalStrengthAsString = String.valueOf(-113 + 2 * signalStrength);
                    } else {
                        signalStrengthAsString = String.valueOf(signalStrength);
                    }
                }
                GsmCell gsmCell = new GsmCell();
                gsmCell.countrycode = countryCode;
                gsmCell.operatorid = operatorId;
                gsmCell.age = "0";
                gsmCell.cellid = String.valueOf(cellId);
                gsmCell.lac = String.valueOf(lacAsString);
                gsmCell.signal_strength = signalStrengthAsString;
                result.add(gsmCell);
            }
        }
        return result;
    }

    private List<WifiNetwork> buildWifiNetworks(List<ScanResult> wifiNetworks) {
        List<WifiNetwork> result = new ArrayList<>();

        if (wifiNetworks != null && wifiNetworks.size() > 0) {
            for (ScanResult net : wifiNetworks) {
                WifiNetwork wifiNetwork = new WifiNetwork();
                wifiNetwork.mac = net.BSSID.toUpperCase();
                char[] mac = net.BSSID.toUpperCase().toCharArray();
                wifiNetwork.signal_strength = String.valueOf(net.level);
                char ch;
                StringBuilder ssid = new StringBuilder(12);
                for (int i = 0; i < mac.length; i++) {
                    ch = mac[i];
                    if (ch != ':') {
                        ssid.append(ch);
                    }
                }
                wifiNetwork.age = "0";
                result.add(wifiNetwork);
            }
        }
        return result;
    }

    private String getRadioType(int networkType) {
        switch (networkType) {
            case -1:
                return "NONE";
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "gsm";
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "cdma";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return "UNKNOWN";
        }
    }

    private void sleep() {
        try {
            Log.d(TAG, "start sleeping");
            Thread.sleep(timeoutInSec * 1000 / 2);
            Log.d(TAG, "stop sleeping");
        } catch (InterruptedException e) {
            Log.d(TAG, "interrupted");
            Thread.currentThread().interrupt();
        }
    }

    public static Map<Integer, String> networkTypeStr;

    static {
        networkTypeStr = new HashMap<>();
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_GPRS, "GPRS");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_EDGE, "EDGE");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_UMTS, "UMTS");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_HSDPA, "HSDPA");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_HSUPA, "HSUPA");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_HSPA, "HSPA");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_CDMA, "CDMA");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_EVDO_0, "EVDO_0");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_EVDO_A, "EVDO_A");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_1xRTT, "1xRTT");
//        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_IDEN, "IDEN");
        networkTypeStr.put(TelephonyManager.NETWORK_TYPE_UNKNOWN, "UNKNOWN");
    }

}