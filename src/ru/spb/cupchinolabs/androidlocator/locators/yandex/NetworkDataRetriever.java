package ru.spb.cupchinolabs.androidlocator.locators.yandex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 07.03.13
 * Time: 11:17
 */
public class NetworkDataRetriever {

    public NetworkData retrieve() {
        GsmCell gsmCell = new GsmCell();
        gsmCell.age = "5555";
        gsmCell.cellid = "42332";
        gsmCell.countrycode = "250";
        gsmCell.lac = "36002";
        gsmCell.signal_strength = "-80";
        gsmCell.operatorid = "99";

        List<GsmCell> gsmCells = new ArrayList<>();
        gsmCells.add(gsmCell);

        List<WifiNetwork> wifiNetworks = new ArrayList<>();

        WifiNetwork wifiNetwork = new WifiNetwork();
        wifiNetwork.mac = "00-1C-F0-E4-BB-F5";
        wifiNetwork.signal_strength = "-88";
        wifiNetwork.age = "0";

        Ip ip = new Ip();
        ip.address_v4 = "178.247.233.32";

        NetworkData networkData = new NetworkData();

        networkData.setGsmCellList(gsmCells);
        networkData.setWifiNetworkList(wifiNetworks);
        networkData.setIp(ip);

        return networkData;
    }
}
