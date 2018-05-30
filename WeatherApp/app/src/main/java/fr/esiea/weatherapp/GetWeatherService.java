package fr.esiea.weatherapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetWeatherService extends IntentService {

    private static final String ACTION_LOCATION = "fr.esiea.weatherapp.action.LOCATION";
    private static final String ACTION_WEATHER = "fr.esiea.weatherapp.action.WEATHER";
    private static String location;
    private static String woeid;

    public GetWeatherService() {
        super("GetWeatherService");

    }

    public static String getLocation() {
        return location;
    }

    public static void setLocation(String city) {
        location = city;
    }

    public static String getWoeid() {
        return woeid;
    }

    public static void setWoeid(String id) {
        woeid =id;
    }

    public static void startActionLocation(Context context) {
        Intent intent = new Intent(context, GetWeatherService.class);
        intent.setAction(ACTION_LOCATION);
        context.startService(intent);
    }

    public static void startActionWeather(Context context) {
        Intent intent = new Intent(context, GetWeatherService.class);
        intent.setAction(ACTION_WEATHER);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOCATION.equals(action)){
                handleActionLocation();
            } else if (ACTION_WEATHER.equals(action)){
                handleActionWeather();
            }
        }
    }

    private void copyInputStreamToFile(InputStream in, File file){
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handleActionLocation() {
        Log.i("TAG", "Thread service name: " + Thread.currentThread().getName());
        URL url = null;
        try {
            url = new URL("https://www.metaweather.com/api/location/search/?query=" + location);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                copyInputStreamToFile(conn.getInputStream(),
                        new File(getCacheDir(), "location.json"));
                Log.i("TAG", "Location json downloaded!");
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.LOCATION_UPDATE));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleActionWeather() {
        Log.i("TAG", "Thread service name: " + Thread.currentThread().getName());
        URL url = null;
        try {
            url = new URL("https://www.metaweather.com/api/location/" + woeid + "/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                copyInputStreamToFile(conn.getInputStream(),
                        new File(getCacheDir(), "weather.json"));
                Log.i("TAG", "Weather json downloaded!");
                //LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SecondActivity.WEATHER_UPDATE));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
