package com.example.weathertesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    public String CITY_NAME = "";
    public String API_URL = "&appid=d98a770b1eb43daa53a864e19c9e19ed&units=metric";

    Button btnSearch;
    TextView tvDate, tvTemp, tvDescription, tvOutput;
    EditText etCity;
    private RequestQueue queue;

    //for getting the current location
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDate = findViewById(R.id.tvDate);
        tvTemp = findViewById(R.id.tvTemp);
        tvDescription = findViewById(R.id.tvDescription);
        tvOutput = findViewById(R.id.tvOutput);
        etCity = findViewById(R.id.etCity);
        btnSearch = findViewById(R.id.btnSearch);


//        Toast toast = Toast.makeText(getApplicationContext(), "hola!", Toast.LENGTH_SHORT);
//        toast.show();

        //for getting the current location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            btnSearch.setText("search");
            getLocation();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }



        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CITY_NAME = etCity.getText().toString();
                if (!CITY_NAME.isEmpty()){
                    findWeather();
                }else {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        btnSearch.setText("search");
                        getLocation();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                    }
//                    getLocation();
                }
            }
        }
        );


        queue = Volley.newRequestQueue(this);

//        findWeather();


    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null){
                    try {

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

//                        tvOutput.setText(addresses.get(0).getLocality());
//                        etCity.setText(addresses.get(0).getLocality());
                        CITY_NAME = addresses.get(0).getLocality();
                        etCity.setText(CITY_NAME);
                        findWeather();
                        CITY_NAME = "";

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    public void findWeather(){
////        String url = "http://api.myjson.com/bins/kp9wz";
////        String url = "http://dataservice.accuweather.com/currentconditions/v1/28143?apikey=GlUa6ScxTiGnOvDRxbezJI15z1XJFzdC";
//        String url = "http://api.openweathermap.org/data/2.5/weather?q=dhaka&appid=d98a770b1eb43daa53a864e19c9e19ed";
//
//        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.e("apiResponse", response.toString());
//                Toast toast = Toast.makeText(getApplicationContext(), "done!", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("apiResponseError", error.getMessage());
//                Toast toast = Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_SHORT);
//                toast.show();
//            }
//        }
//        );
//        queue.add(jor);
//    }





//--------------------------------------------------------------------------------------------------
    /*
    try {
        code = response.getString("code");
        if (code.equals("200")) {
            hospitalArray = response.getJSONArray("hospitals");
            Log.e("ArrayNotice", String.valueOf(hospitalArray));
            for (int i = 0; i < hospitalArray.length(); i++) {
                hospitalObject = hospitalArray.getJSONObject(i);
                String name = hospitalObject.getString("name");
                String address = hospitalObject.getString("address");
                String district = hospitalObject.getString("district");
                String division = hospitalObject.getString("division");
                String mobile_bn = hospitalObject.getString("mobile_bn");
                String mobile_en = hospitalObject.getString("mobile_en");

                hospitals = new Hospitals(name,address,district,division,mobile_bn,mobile_en);
                hospitalList.add(hospitals);
                hospitalAdapter = new HospitalAdapter(HospitalListActivity.this, hospitalList);
                hospitalListView.setAdapter(hospitalAdapter);
            }
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }
    */
//--------------------------------------------------------------------------------------------------














    public void findWeather(){
        String url = BASE_URL+CITY_NAME+API_URL;
//        String url = "http://api.openweathermap.org/data/2.5/weather?q=dhaka&appid=d98a770b1eb43daa53a864e19c9e19ed&units=metric";
//        String url = "http://dataservice.accuweather.com/currentconditions/v1/28143?apikey=GlUa6ScxTiGnOvDRxbezJI15z1XJFzdC";
//        String url = "http://dataservice.accuweather.com/currentconditions/v1/"+cityID+"?apikey=GlUa6ScxTiGnOvDRxbezJI15z1XJFzdC";
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("apiResponse", response.toString());
                Toast toast = Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_SHORT);
                toast.show();
                try {
                    int cod = response.getInt("cod");

                    if (cod == 200){
                        toast = Toast.makeText(getApplicationContext(), "city found!", Toast.LENGTH_SHORT);
                        toast.show();


                        // getting and setting the temperature
                        JSONObject main_object = response.getJSONObject("main");
                        Log.e("mainObject", main_object.toString());
                        String tempObj = main_object.getString("temp");
                        Log.e("tempObj", tempObj);
                        String temp = String.valueOf(main_object.getDouble("temp"));

                        // setting the condition
                        JSONArray array = response.getJSONArray("weather");
                        JSONObject object = array.getJSONObject(0);
                        String description = object.getString("description");

                        // City name
                        String city = response.getString("name");
//                        etCity.setText(CITY_NAME);

                        // Setting temperature
                        tvTemp.setText("Temperature: " + temp+ "\u2103");

                        // setting weather condition
                        tvDescription.setText("Condition: "+description);

                        //setting and formatting date
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM-dd-yyyy");
                        String formatted_date = sdf.format(calendar.getTime());
//                    String formatted_date = calendar.getTime().toString();

                        tvDate.setText(formatted_date);

                        //co-ordinate
                        JSONObject coord = response.getJSONObject("coord");
                        String tempLon = coord.getString("lon");
                        String tempLat = coord.getString("lat");

                        // main details
                        String mainFeelsLike = main_object.getString("feels_like");
                        String mainMin = main_object.getString("temp_min");
                        String mainMax = main_object.getString("temp_max");
                        String mainPressure = main_object.getString("pressure");
                        String mainHumidity = main_object.getString("humidity");

                        //visibility
                        int visibility = response.getInt("visibility");
//                    String visibility = response.getString("visibility");

                        // for wind
                        JSONObject wind = response.getJSONObject("wind");
                        double windSpeed = wind.getDouble("speed");
                        String windDegree = wind.getString("deg");

                        // clouds
                        JSONObject clouds = response.getJSONObject("clouds");
                        String cloudsAll = clouds.getString("all");

                        // dt (Time of data calculation, unix, UTC)
                        String dt = response.getString("dt");

                        //sys
                        JSONObject sys = response.getJSONObject("sys");
                        String sysType = sys.getString("type");
                        String sysId = sys.getString("id");
                        String sysCountry = sys.getString("country");
                        String sysSunrise = sys.getString("sunrise");
                        String sysSunset = sys.getString("sunset");

                        //extra
                        String extraTimeZone = response.getString("timezone");
                        String extraId = response.getString("id");
                        String extraName = response.getString("name");
                        String extraCod = response.getString("cod");




                        tvOutput.setText("Co-ordinate: "+tempLat+" by "+tempLon+"\n"+
                                "Feels like: "+mainFeelsLike+"\u2103\n"+
                                "Min temp: "+mainMin+"\u2103\n"+
                                "Max temp: "+mainMax+"\u2103\n"+
                                "Pressure: "+mainPressure+"\n"+
                                "Humidity: "+mainHumidity+"\n"+
                                "Visibility: "+visibility+"\n"+
                                "Wind Speed: "+windSpeed+"\n"+
                                "Wind Degree: "+windDegree+"\n"+
                                "DT: "+dt+"\n"+
                                "Type: "+sysType+"\n"+
                                "Id: "+sysId+"\n"+
                                "Country: "+sysCountry+"\n"+
                                "Sunrise: "+sysSunrise+"\n"+
                                "Sunset: "+sysSunset+"\n"+
                                "TimeZone: "+extraTimeZone+"\n"+
                                "Id: "+extraId+"\n"+
                                "Name: "+extraName+"\n"+
                                "Cod: "+extraCod+"\n"+
                                "");


                    }
                    else {
                        toast = Toast.makeText(getApplicationContext(), "Could not found!", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }catch (JSONException e){
                    Log.e("apiResponseError", response.toString());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast toast = Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_SHORT);
                toast.show();

                try {
                    String responseBody = new String( error.networkResponse.data, "utf-8" );
                    JSONObject jsonObject = new JSONObject( responseBody );
                    Log.e("apiResponseError", jsonObject.toString());

//                    toast = Toast.makeText(getApplicationContext(), "error 1!", Toast.LENGTH_SHORT);
//                    toast.show();

                    tvOutput.setText("");
                    tvTemp.setText("City not Found!!!");
                    tvDescription.setText("");
                } catch ( JSONException e ) {
                    toast = Toast.makeText(getApplicationContext(), "error 2!", Toast.LENGTH_SHORT);
                    toast.show();
                    //Handle a malformed json response
                } catch (UnsupportedEncodingException errorAgain){
                    toast = Toast.makeText(getApplicationContext(), "error 3!", Toast.LENGTH_SHORT);
                    toast.show();

                }





            }
        }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jor);


    }

}