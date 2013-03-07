package ru.spb.cupchinolabs.androidlocator.locators.yandex;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 07.03.13
 * Time: 4:59
 */
public class YandexLocatorRequestBuilder {

    public static final String API_KEY = "AHfzMFEBAAAAt4_RTQQAk57mvJS6SMGh4nwNNOB5HVMrqkEAAAAAAAAAAADMKekl28qO50dzsm-9AF83cDEjvg==";

    public static final String COMMON = "   \"common\": {\n" +
            "      \"version\": \"1.0\",\n" +
            "      \"api_key\": \"" + API_KEY + "\"\n" +
            "   }";

    private List<GsmCell> gsmCells;
    private List<WifiNetwork> wifiNetworks;
    private Ip ip;

    public String build() {
        StringBuffer sb = new StringBuffer();
        sb.append("json={\n");
        sb.append(COMMON);

        if (gsmCells != null && gsmCells.size() >= 1) {
            sb.append(",\n");
            sb.append("   \"gsm_cells\": [\n");
            for (int i = 0, size = gsmCells.size(); i < size; i++) {
                GsmCell gsmCell = gsmCells.get(i);
                if (i != 0) {
                    sb.append(",\n");
                }
                sb.append("       {\n");
                sb.append("          \"countrycode\": " + gsmCell.countrycode + ",\n" +
                        "          \"operatorid\": " + gsmCell.operatorid + ",\n" +
                        "          \"cellid\": " + gsmCell.cellid + ",\n" +
                        "          \"lac\": " + gsmCell.lac + ",\n" +
                        "          \"signal_strength\": " + gsmCell.signal_strength + ",\n" +
                        "          \"age\": " + gsmCell.age + "\n"
                );
                sb.append("       }\n");
            }
            sb.append("   ]");
        }
        if (wifiNetworks != null && wifiNetworks.size() >= 1) {
            sb.append(",\n");
            sb.append("   \"wifi_networks\": [\n");
            for (int i = 0, size = wifiNetworks.size(); i < size; i++) {
                WifiNetwork wifiNetwork = wifiNetworks.get(i);
                if (i != 0) {
                    sb.append(",\n");
                }
                sb.append("       {\n");
                sb.append("          \"mac\": \"" + wifiNetwork.mac + "\",\n" +
                        "          \"signal_strength\": " + wifiNetwork.signal_strength + ",\n" +
                        "          \"age\": " + wifiNetwork.age + "\n"
                );
                sb.append("       }\n");
            }
            sb.append("   ]");
        }
        if (ip != null) {
            sb.append(",\n");
            sb.append("   \"ip\": {\n" +
                    "     \"address_v4\": \"" + ip.address_v4 + "\"\n" +
                    "   }");
        }
        sb.append("\n}");
        return sb.toString();
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
