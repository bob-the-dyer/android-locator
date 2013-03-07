package ru.spb.cupchinolabs.androidlocator.locators.yandex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 07.03.13
 * Time: 4:59
 */
public class YandexLocatorRequestBuilder {

    public static final String API_KEY =
            "AHfzMFEBAAAAt4_RTQQAk57mvJS6SMGh4nwNNOB5HVMrqkEAAAAAAAAAAADMKekl28qO50dzsm-9AF83cDEjvg==";

    private List<GsmCell> gsmCells;
    private List<WifiNetwork> wifiNetworks;
    private Ip ip;

    public JSONObject buildJSON() throws JSONException {
        JSONObject jsonResult = new JSONObject();

        JSONObject common = new JSONObject();
        common.put("version", "1.0");
        common.put("api_key", API_KEY);

        jsonResult.put("common", common);

        if (gsmCells != null && gsmCells.size() >= 1) {
            JSONArray jsonGsmCellArray = new JSONArray();
            for (GsmCell gsmCell : gsmCells) {
                JSONObject jsonCell = new JSONObject();
                jsonCell.put("countrycode", gsmCell.countrycode);
                jsonCell.put("age", gsmCell.age);
                jsonCell.put("cellid", gsmCell.cellid);
                jsonCell.put("lac", gsmCell.lac);
                jsonCell.put("operatorid", gsmCell.operatorid);
                jsonCell.put("signal_strength", gsmCell.signal_strength);
                jsonGsmCellArray.put(jsonCell);
            }
            jsonResult.put("gsm_cells", jsonGsmCellArray);
        }

        if (wifiNetworks != null && wifiNetworks.size() >= 1) {
            JSONArray jsonWifiNetworkArray = new JSONArray();
            for (WifiNetwork wifiNetwork : wifiNetworks) {
                JSONObject jsonWifiNetwork = new JSONObject();
                jsonWifiNetwork.put("age", wifiNetwork.age);
                jsonWifiNetwork.put("mac", wifiNetwork.mac);
                jsonWifiNetwork.put("signal_strength", wifiNetwork.signal_strength);
                jsonWifiNetworkArray.put(jsonWifiNetwork);
            }
            jsonResult.put("wifi_networks", jsonWifiNetworkArray);
        }

        if (ip != null) {
            JSONObject jsonIp = new JSONObject();
            jsonIp.put("address_v4", ip.address_v4);
            jsonResult.put("ip", jsonIp);
        }

        return jsonResult;
    }

    public void setGsmCells(List<GsmCell> gsmCells) {
        this.gsmCells = gsmCells;
    }

    public void setWifiNetworks(List<WifiNetwork> wifiNetworks) {
        this.wifiNetworks = wifiNetworks;
    }

    public void setIp(Ip ip) {
        this.ip = ip;
    }

}
