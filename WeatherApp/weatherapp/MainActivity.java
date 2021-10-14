package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Declare variables with there id's.
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    // Add WeatherRVModal variable here.
    // then initialise these down below in onCreate too.
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter; // adaptor class
    // User location-related variables
    // We need to ask for permission first when using user's current location.
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The line below makes the application full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        // initialise the variables here.
        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idPBLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEditCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);

        // API we will going to use.
        weatherRVModalArrayList = new ArrayList<>();
        // Initialise the adapter.
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        // Set this adapter to the RecyclerView.
        weatherRV.setAdapter(weatherRVAdapter);

        // Inisialise the LocationManager.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Check whether the user has granted permission or not.
        // if the permission is not granted,
        // ask the user to grant the permission.
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        // Once the permission is granted,
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        // Get the user's city name using uer's current location obtained upon the granted permission.
        String cityName = getCityName(location.getLongitude(), location.getLatitude());
        // Pass the obtained cityName into getWeatherInfo to fetch the weather info.
        getWeatherInfo(cityName);

        searchIV.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View v) {
              // Whatever user types on the city edit search bar (@id/idEditCity)
              // Turn the input into a string
              String city = cityEdt.getText().toString();

              // If no input, alert the user to enter something.
              if (city.isEmpty()){
                  Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
              } else {
                  // if any input,
                  cityNameTV.setText(cityName);  // Update the city name display bar with the inputted text (city name)
                  getWeatherInfo(city);          // and fetch weather info.
              }
            }
        });
    }

    // Once the permission is granted by the user,
    // You can call this built-in method automatically.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please provide permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // Create a method that gets the city name from the API latitude and longitude data.
    private String getCityName(double longitude, double latitude){
        // initialise with "Not found" in case the method fails to get longitude & latitude values.
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 10);

            for (Address adr: addresses){
                if (adr!=null){                          // if the locality data is there,
                    String city = adr.getLocality();     // get that data.
                    if (city!=null && !city.equals("")){ // check if the locality data is correct.
                        cityName = city;                 // update cityName with that screened data.
                    } else {
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "User City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        return cityName;

    }

    // Create a method for weather info,
    // and another for user location.
    private  void getWeatherInfo(String cityName){

        // Parse the data from API.
        // so first, create a url.
        String url = "http://api.weatherapi.com/v1/forecast.json?key=34a7fe8762854dd7b8765811212509&q=" + cityName + "&days=1&aqi=yes&alerts=yes";

        cityNameTV.setText(cityName);

        // Create a variable RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        // The weather API is a json object, so we have to make a json object request.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModalArrayList.clear();

                // Take a close look at the syntax of accessing the data in Json format.
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature + "°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    // Switch background colour
                    if (isDay==1) {
                        // Morning
                        Picasso.get().load("@assets/day light.jpg").into(backIV);
                    } else {
                        // Night
                        Picasso.get().load("@assets/night sky.jpg").into(backIV);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastArr = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastArr.getJSONArray("hour");

                    for(int i=0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);

                        // Create variables to store the fetched data.
                        String time = hourObj.getString("time");
                        String temp_c = hourObj.getString("temp_c");
                        String icon = hourObj.getJSONObject("condition").getString("icon");
                        String wind_kph = hourObj.getString("wind_kph");
                        weatherRVModalArrayList.add(new WeatherRVModal(time, temp_c, icon, wind_kph));
                    }

                    // Notify adaptor about these variables.
                    weatherRVAdapter.notifyDataSetChanged();



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });


    }


}