package ru.spb.cupchinolabs.androidlocator.locators.yandex;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
 *
 * TODO zip requests to minimize traffic and response time
 */
public class YandexLocator extends AbstractChainedLocator {

    private static final String TAG = YandexLocator.class.getSimpleName();

    private final int timeoutInMillis;
    private NetworkDataBuilder builder;
    private ConnectivityManager connectivityManager;

    public YandexLocator(NetworkDataBuilder builder, ConnectivityManager connectivityManager, int timeoutInSec) {
        this.builder = builder;
        this.connectivityManager = connectivityManager;
        this.timeoutInMillis = timeoutInSec * 1000 / 2;
    }

    @Override
    protected Location locateImpl() {

        if (builder == null) {
            Log.d(TAG, "builder is null, skipping");
            return null;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Log.d(TAG, "no network or disconnected, skipping");
            return null;
        }

        NetworkData data = builder.build();

        if (!data.isSufficient()) {
            Log.d(TAG, "Network data is insufficient for locator, skipping");
            return null;
        }

        HttpURLConnection conn = null;

        try {
            JSONObject jsonRequest = createJSONRequest(data);

            String request = "json=" + jsonRequest.toString();

            Log.d(TAG, "yandex.locator request -> " + request);

            conn = createHttpUrlConnection(conn, request);

            postRequest(conn, request);

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "responseCode -> " + responseCode);

            InputStream is;

            if (200 != responseCode) {
                is = conn.getErrorStream();
            } else {
                is = conn.getInputStream();
            }

            String jsonResponseAsString = readResponse(is);

            if (200 != responseCode) {
                Log.d(TAG, "error occurred, returning null");
                return null;
            }

            JSONObject position = new JSONObject(jsonResponseAsString)
                    .getJSONObject("position");

            if (100000 == position.getDouble("precision")) {
                Log.d(TAG, "precision is 100000, returning null");
                return null;
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

    private void postRequest(HttpURLConnection conn, String request) throws IOException {
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(request);
        wr.flush();
        wr.close();
    }

    private HttpURLConnection createHttpUrlConnection(HttpURLConnection conn, String request) throws IOException {
        URL url = new URL("http://api.lbs.yandex.net/geolocation");
        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(timeoutInMillis);
        conn.setConnectTimeout(timeoutInMillis);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Length", Integer.toString(request.getBytes().length));
        return conn;
    }

    private JSONObject createJSONRequest(NetworkData data) throws JSONException {
        JSONRequestBuilder jsonBuilder = new JSONRequestBuilder();

        jsonBuilder.setWifiNetworks(data.getWifiNetworkList());
        jsonBuilder.setGsmCells(data.getGsmCellList());
        jsonBuilder.setIp(data.getIp());

        return jsonBuilder.build();
    }

    private String readResponse(InputStream is) throws IOException {
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
