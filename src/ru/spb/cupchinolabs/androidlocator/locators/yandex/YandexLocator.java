package ru.spb.cupchinolabs.androidlocator.locators.yandex;

import android.location.Location;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import ru.spb.cupchinolabs.androidlocator.locators.AbstractChainedLocator;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: VladimirK
 * Date: 06.03.13
 * Time: 19:24
 */
public class YandexLocator extends AbstractChainedLocator {

    private static final String TAG = YandexLocator.class.getSimpleName();

    private final int timeoutInMillis;
    private NetworkDataRetriever retriever;

    public YandexLocator(NetworkDataRetriever retriever, int timeoutInSec) {
        this.retriever = retriever;
        this.timeoutInMillis = timeoutInSec * 1000;
    }

    @Override
    protected Location locateImpl() {

        if (retriever == null){
            Log.d(TAG, "retriever is null, skipping");
            return null;
        }

        NetworkData data = retriever.retrieve();

        if (!data.isSufficient()) {
            Log.d(TAG, "Network data is insufficient for locator, skipping");
            return null;
        }

        HttpURLConnection conn = null;

        try {
            JSONObject jsonRequest = buildJSONRequest(data);

            String request = "json=" + jsonRequest.toString();

            Log.d(TAG, "yandex.locator request -> " + request);

            conn = buildHttpUrlConnection(conn, request);

            doPost(conn, request);

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "responseCode -> " + responseCode);

            InputStream is;

            if (200 != responseCode) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            String jsonResponseAsString = readResponseAsString(is);

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

    private void doPost(HttpURLConnection conn, String request) throws IOException {
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(request);
        wr.flush();
        wr.close();
    }

    private HttpURLConnection buildHttpUrlConnection(HttpURLConnection conn, String request) throws IOException {
        URL url = new URL("http://api.lbs.yandex.net/geolocation");
        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(timeoutInMillis);
        conn.setConnectTimeout(timeoutInMillis);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Length", Integer.toString(request.getBytes().length));
        return conn;
    }

    private JSONObject buildJSONRequest(NetworkData data) throws JSONException {
        JSONRequestBuilder jsonBuilder = new JSONRequestBuilder();

        jsonBuilder.setWifiNetworks(data.getWifiNetworkList());
        jsonBuilder.setGsmCells(data.getGsmCellList());
        jsonBuilder.setIp(data.getIp());

        return jsonBuilder.build();
    }

    private String readResponseAsString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line + System.getProperty("line.separator"));
        }
        String response = sb.toString();
        Log.d(TAG, "response json -> " + response);
        return response;
    }

    private Location createLocationFromJSON(JSONObject jsonResponse) throws JSONException {
        Location location = new Location("yandex");
        location.setTime(new Date().getTime());
        location.setLatitude(jsonResponse.getDouble("latitude"));
        location.setLongitude(jsonResponse.getDouble("longitude"));
        return location;
    }

}
