package com.example.weather;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private TextView pm;
    private TextView TMiView;
    private TextView TMmView;
    private TextView PressView;
    private TextView HumView;
    private LinearLayout listvilles;
    private ImageView iconView;



    private RequestQueue requestQueue;
    private TextView DescriptionView;
    private TextView TemperatureView;
    private TextView CityView;
    private TextView TimeView;
    private TextView dateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        requestQueue = Volley.newRequestQueue(this);

        DescriptionView = (TextView) findViewById(R.id.description);
        TemperatureView = (TextView) findViewById(R.id.degree);
        CityView = (TextView) findViewById(R.id.city);
        pm = (TextView) findViewById(R.id.dORn);

        PressView = (TextView) findViewById(R.id.pression);
        HumView = (TextView) findViewById(R.id.humidite);
        TimeView = (TextView) findViewById(R.id.hour);
        dateView = (TextView) findViewById(R.id.Date);
        TMiView = (TextView) findViewById(R.id.tmin);
        TMmView = (TextView) findViewById(R.id.tmax);
        listvilles = (LinearLayout) findViewById(R.id.main_list);
        iconView = (ImageView) findViewById(R.id.icon);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("back","reched");

    }






    //on va recuperer la ville et envoyer un request get avec la ville

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              WeatherAPI(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }
    //recuperer l'icon qui correspondre a l'état de weather
    public void GetWeatherIcon(String icon) {
        String apiURI = String.format("https://openweathermap.org/img/wn/%s@2x.png", icon);
        ImageRequest imageRequest = new ImageRequest(apiURI, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                iconView.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(imageRequest);
    }

    public void WeatherAPI(String query) {
        String apiURI = "http://api.openweathermap.org/data/2.5/weather?q=+" + query + "+&APPID=" + getString(R.string.API_KEY);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiURI, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(JSONObject response) {
                AfficherResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void AfficherResult(JSONObject weatherData) {
        try {
            final Double toC = -273.15;
            JSONObject location = weatherData.getJSONObject("coord");
            JSONObject weather = weatherData.getJSONArray("weather").getJSONObject(0);
            JSONObject main = weatherData.getJSONObject("main");
            String temperature = (int) (main.getDouble("temp") + toC) + "°C";
            String tmin = (int) (main.getDouble("temp_min") + toC) + " °C";
            String tmax = (int) (main.getDouble("temp_max") + toC) + " °C";
            String pressure = main.getInt("pressure") + " hPa";
            String humidity = main.getInt("humidity") + "";
            String cityNameAndMain = weatherData.getString("name") + " , " + weatherData.getJSONObject("sys").getString("country");

            Date dt = new Date(weatherData.getLong("dt") * 1000);
            String Time  = new SimpleDateFormat("hh:mm a", Locale.US).format(dt);
            String date = new SimpleDateFormat("EEEE, d MMM YYYY", Locale.ENGLISH).format(dt);
            String[] TimeData = Time.split(" ");


            DescriptionView.setText(weather.getString("description"));
            TemperatureView.setText(temperature);
            CityView.setText(cityNameAndMain);
            pm.setText(TimeData[1]);
            TimeView.setText(TimeData[0]);
            TMiView.setText(tmin);
            TMmView.setText(tmax);
            PressView.setText(pressure);
            HumView.setText(humidity);
            dateView.setText(date);


            //afficher l'icon qui correspondre a l'état de weather
            GetWeatherIcon(weather.getString("icon"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
