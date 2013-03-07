package ru.spb.cupchinolabs.androidlocator.locators.yandex;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 07.03.13
 * Time: 11:18
 */
public class NetworkData {

    private Ip ip;

    private List<WifiNetwork> wifiNetworkList;

    private List<GsmCell> gsmCellList;

    public Ip getIp() {
        return ip;
    }

    public void setIp(Ip ip) {
        this.ip = ip;
    }

    public List<WifiNetwork> getWifiNetworkList() {
        return wifiNetworkList;
    }

    public void setWifiNetworkList(List<WifiNetwork> wifiNetworkList) {
        this.wifiNetworkList = wifiNetworkList;
    }

    public List<GsmCell> getGsmCellList() {
        return gsmCellList;
    }

    public void setGsmCellList(List<GsmCell> gsmCellList) {
        this.gsmCellList = gsmCellList;
    }

    public boolean isSufficient() {
        return (wifiNetworkList != null && wifiNetworkList.size() >= 1) ||
                (gsmCellList != null && gsmCellList.size() >= 1) ||
                ip != null;
    }
}

