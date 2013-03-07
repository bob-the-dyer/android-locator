package ru.spb.cupchinolabs.androidlocator.locators.yandex;

import android.location.Location;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import ru.spb.cupchinolabs.androidlocator.locators.AbstractChainedLocator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 06.03.13
 * Time: 19:24
 * <p/>
 * TODO rework to JSON!
 */
public class YandexLocator extends AbstractChainedLocator {

    private static final String TAG = YandexLocator.class.getSimpleName();

    private final int timeoutInMillis;

    public YandexLocator(int timeoutInSec) {
        this.timeoutInMillis = timeoutInSec * 1000;
    }

    @Override
    protected Location locateImpl() {
        YandexLocatorRequestBuilder builder = new YandexLocatorRequestBuilder();

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

        builder.setWifiNetworks(wifiNetworks);
        builder.setGsmCells(gsmCells);

        Ip ip = new Ip();
        ip.address_v4 = "178.247.233.32";
        builder.setIp(ip);

        HttpURLConnection conn = null;
        try {
            String request = "json=" + builder.buildJSON().toString();

            Log.d(TAG, "yandex.locator request -> " + request);

            URL url = new URL("http://api.lbs.yandex.net/geolocation");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(timeoutInMillis);
            conn.setConnectTimeout(timeoutInMillis);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", Integer.toString(request.getBytes().length));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(request);
            wr.flush();
            wr.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "responseCode -> " + responseCode);

            InputStream is;

            if (200 != responseCode) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            String jsonResponseAsString = readResponseAsString(is);
            Log.d(TAG, "response json -> " + jsonResponseAsString);

            if (200 != responseCode) {
                Log.d(TAG, "error occurred, returning null");
                return null;
            }

            JSONObject position = new JSONObject(jsonResponseAsString)
                    .getJSONObject("position");

            if (100000 == position.getDouble("precision")) {
                Log.d(TAG, "precision is 100000, returning null");
// TODO uncomment return null;
            }

            return createLocationFromJSON(position);
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            return null;
        } catch (JSONException e) {
            Log.e(TAG, "JSONException", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String readResponseAsString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line + System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    private Location createLocationFromJSON(JSONObject jsonResponse) throws JSONException {
        Location location = new Location("yandex");
        location.setTime(new Date().getTime());
        location.setLatitude(jsonResponse.getDouble("latitude"));
        location.setLongitude(jsonResponse.getDouble("longitude"));
        return location;
    }

}
