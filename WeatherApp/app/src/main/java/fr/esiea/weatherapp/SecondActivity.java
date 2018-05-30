package fr.esiea.weatherapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class SecondActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        /*IntentFilter intentFilter = new IntentFilter(WEATHER_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new SecondActivity.WeatherUpdate(),intentFilter);*/

        GetWeatherService.startActionWeather(this);

        JSONObject weatherObject = getWeatherObjectFromFile();

        TextView textView = findViewById(R.id.as_title);
        TextView textView1 = findViewById(R.id.as_temp);
        imageView = findViewById(R.id.as_img);

        try {
            textView.setText(weatherObject.getString("title"));
            textView1.setText(weatherObject.getJSONArray("consolidated_weather").getJSONObject(0).getString("the_temp") + "°C");
            URL url = new URL("https://www.metaweather.com/static/img/weather/png/64/" + weatherObject.getJSONArray("consolidated_weather").getJSONObject(0).getString("weather_state_abbr") + ".png");
            loadImageFromUrl(url);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private void loadImageFromUrl(URL url) {
        Picasso.with(this).load(String.valueOf(url)).placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher).into(imageView,new com.squareup.picasso.Callback(){
            public void onSuccess(){

            }
            public void onError(){

            }
        });
    }


    //public static final String WEATHER_UPDATE="fr.esiea.weatherapp.WEATHER_UPDATE";

    /*public class WeatherUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Log.d("TAG", getIntent().getAction());
        }
    }*/

    public JSONObject getWeatherObjectFromFile(){
        try {
            InputStream is = new FileInputStream(getCacheDir() + "/" + "weather.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer,"UTF-8"));
        } catch (IOException e){
            e.printStackTrace();
            return new JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                NotificationCompat.Builder builder = new NotificationCompat.Builder(SecondActivity.this)
                        .setSmallIcon(android.R.drawable.ic_delete)
                        .setContentTitle(getString(R.string.title))
                        .setContentText(getString(R.string.msg))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SecondActivity.this);
                notificationManager.notify(0, builder.build());
                return true;
            case R.id.action_delete:
                AlertDialog.Builder buildr = new AlertDialog.Builder(SecondActivity.this);
                buildr.setMessage(R.string.msg)
                        .setTitle(R.string.dialog)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog dialog = buildr.create();
                dialog.show();
                return true;
            case R.id.action_settings:
                Toast.makeText(this,R.string.action_toast,Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
