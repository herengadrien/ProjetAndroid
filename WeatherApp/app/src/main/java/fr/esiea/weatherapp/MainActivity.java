package fr.esiea.weatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter(LOCATION_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new MainActivity.LocationUpdate(),intentFilter);

        final EditText editText = findViewById(R.id.location);
        recyclerView = findViewById(R.id.rv_weather);

        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                GetWeatherService.setLocation(s.toString());

                GetWeatherService.startActionLocation(getApplicationContext());

                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
                recyclerView.setAdapter(new MainActivity.LocationAdapter(getLocationArrayFromFile()));
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
    }

    public static final String LOCATION_UPDATE="fr.esiea.weatherapp.LOCATION_UPDATE";

    public class LocationUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Log.d("TAG", getIntent().getAction());
            ((LocationAdapter) recyclerView.getAdapter()).setNewLocation(getLocationArrayFromFile());
        }
    }

    public JSONArray getLocationArrayFromFile(){
        try {
            InputStream is = new FileInputStream(getCacheDir() + "/" + "location.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONArray(new String(buffer,"UTF-8"));
        } catch (IOException e){
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationHolder> {

        private JSONArray locationArray;

        public LocationAdapter(JSONArray locationArray){
            this.locationArray = locationArray;
        }

        @Override
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_element,parent,false);
            LocationHolder locationHolder = new LocationHolder(view);
            return locationHolder;
        }

        @Override
        public void onBindViewHolder(LocationHolder holder, int position) {
            try {
                final String woeid = locationArray.getJSONObject(position).getString("woeid");
                holder.getName().setText(locationArray.getJSONObject(position).getString("title"));
                holder.getName().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GetWeatherService.setWoeid(woeid);
                        startActivity(new Intent(getApplicationContext(), SecondActivity.class));
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return locationArray.length();
        }

        public void setNewLocation(JSONArray locationArray) {
            this.locationArray = locationArray;
            notifyDataSetChanged();
        }

        public class LocationHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public LocationHolder(View itemView) {
                super(itemView);

                textView = itemView.findViewById(R.id.rv_text);
            }

            public TextView getName() {
                return textView;
            }
        }
    }
}
