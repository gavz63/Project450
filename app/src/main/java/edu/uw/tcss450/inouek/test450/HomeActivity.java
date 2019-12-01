package edu.uw.tcss450.inouek.test450;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import edu.uw.tcss450.inouek.test450.Connections.ConnectionsHomeDynamicDirections;
import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListFragmentDirections;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.GetAsyncTask;
import edu.uw.tcss450.inouek.test450.weather.LocationViewModel;
import edu.uw.tcss450.inouek.test450.weather.TenDaysWeatherModel;
import edu.uw.tcss450.inouek.test450.weather.TenDaysWeatherPost;
import edu.uw.tcss450.inouek.test450.weather.Weather10Fragment;

//Testing change on git
public class HomeActivity extends AppCompatActivity implements Weather10Fragment.OnListFragmentInteractionListener, LocationListener {

    public static final int MONKEY_YELLOW = 1;
    public static final int MONKEY_GREEN = 2;
    public static final int MONKEY_RED = 3;
    public static final int MONKEY_PINK = 4;
    public static final int MONKEY_BLUE = 5;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    //LocationManager mLocationManager;
    private ArrayList<TenDaysWeatherPost> weathers;

    private SwitchMaterial mNightModeSwitch;
    private AppBarConfiguration mAppBarConfiguration;
    private Credentials mCredentials;
    private String mJwToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get location and continuously update it.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){

                if(locationResult == null){
                    return;
                }
                for(Location location : locationResult.getLocations()) {
                    LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
                    viewModel.changeLocation(location);
                    Log.d("Location Update", location.toString());
                }

            }
        };
        createLocationRequest();

        findWeather();

        //Set up menus and nav_host_fragment
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_night_mode_switch);
        View actionView = menuItem.getActionView();
        mNightModeSwitch = actionView.findViewById(R.id.night_mode_switch);
        mNightModeSwitch.setOnCheckedChangeListener((mNightModeSwitch, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
            }

        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_connections, R.id.nav_chat, R.id.nav_weather, R.id.nav_account)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.mobile_navigation, getIntent().getExtras());
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this::onNavigationSelected);

        HomeActivityArgs args = HomeActivityArgs.fromBundle(getIntent().getExtras());
        mJwToken = args.getJwt();
        mCredentials = args.getCredentials();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private boolean onNavigationSelected(final MenuItem menuItem) {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        switch (menuItem.getItemId()) {
            case R.id.nav_account:
                MobileNavigationDirections.ActionGlobalNavAccount userPage =
                        UserFragmentDirections.actionGlobalNavAccount(mCredentials);
                navController.navigate(userPage);
                break;
            case R.id.nav_chatlist:
                MobileNavigationDirections.ActionGlobalNavChatlist chatPage =
                        ChatListFragmentDirections.actionGlobalNavChatlist(mCredentials, mJwToken);
                navController.navigate(chatPage);
                break;
            //TODO WEATHER NAVIGATION
            case R.id.nav_weather:

                MobileNavigationDirections.ActionGlobalWeatherMainFragment weatherPage =
                        WeatherMainFragmentDirections.actionGlobalWeatherMainFragment(mCredentials);
                navController.navigate(weatherPage);

                // test should put weather fragment, just put test for testing purpose
                //navController.navigate(R.id.test_forecast24);

                break;
            case R.id.nav_connections:
                MobileNavigationDirections.ActionGlobalNavConnections connectionsPage =
                        ConnectionsHomeDynamicDirections.actionGlobalNavConnections(mCredentials);
                navController.navigate(connectionsPage);
                break;
            case R.id.nav_home:
                navController.navigate(MobileNavigationDirections.actionGlobalNavHome());
                break;
        }
        //Close the drawer
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        //Start location update
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ){

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //Stop location update
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    void setCredentials(Credentials c) {
        mCredentials = c;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    requestLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down...maybe ask for permission again?
                    finishAndRemoveTask();
                }
                return;
            }
            // other 'case' lines to check for otherpermissions this app might request
        }
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("LOCATION", location.toString());

                                LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
                                viewModel.changeLocation(location);
                                findWeather();

                            }
                        }
                    });
        }
    }

    private void createLocationRequest(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onListFragmentInteraction(TenDaysWeatherPost item) {

    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




    // this method will excute the link and find the weather data and info
    public void findWeather (){
        LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
        Location location = viewModel.getCurrentLocation().getValue();

        Uri tenDayWeatherUri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_weather_10))
                .appendQueryParameter("lat", Double.toString(location.getLatitude()))
                .appendQueryParameter("lon", Double.toString(location.getLongitude()))
                .build();
        new GetAsyncTask.Builder(tenDayWeatherUri.toString())
                .onPostExecute(this::getTenDayWeatherOnPost)
                .addHeaderField("authorization", mJwToken) //add the JWT as a header
                .build().execute();
//        try{
//            // want to make asynctask to get the data in background
//            HomeActivity.ExecuteTask tasky = new HomeActivity.ExecuteTask();
//            LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
//            Location location = viewModel.getCurrentLocation().getValue();
//            //tasky.execute("Here in the URL of the website"+cityToFind+"API key");
//            tasky.execute("https://samples.openweathermap.org/data/2.5/forecast/daily?lat=" + location.getLatitude()
//                    + "&lon=" + location.getLongitude() + "&cnt=10,us&appid=4e6149bb3debe832f3d55ff70ec9b2f4");
//        } catch(Exception e) {
//            e.printStackTrace();
//
//        }
    }


    public static float KelvinToFahrenheit(float degree)
    {
        return degree * 9/5 - 459.67f;
    }

    private void getTenDayWeatherOnPost(String s) {

        return;
    }



    // this task will get all from website in background
    public class ExecuteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            StringBuilder sb = new StringBuilder();
            HttpURLConnection urlConnection = null;
            String url = strings[0];

            try {
                // strings[0] is the urls
                URL urlObject = new URL(url);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String line;

                while (null != (line = buffer.readLine())) {

                    sb.append(line);

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return sb.toString();
        }

        @Override
        // used to update the UI
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                // s in there should be result
                JSONObject jsonObject = new JSONObject(result);

                String tenDaysWeather = jsonObject.getString("list");

                JSONArray weatherArray = new JSONArray(tenDaysWeather);

                TenDaysWeatherPost[] weather = new TenDaysWeatherPost[weatherArray.length()];
                //get 10 days weather info
                for (int i = 0; i < weatherArray.length(); i++) {


                    JSONObject day = weatherArray.getJSONObject(i);

                    long time = Integer.valueOf(day.getString("dt")).intValue();
                    Calendar currCal = Calendar.getInstance();
                    currCal.setTimeInMillis(time);
                    //Date currCalDate = new Date(time);
                    String iconID = "http://openweathermap.org/img/w/" + day.getJSONArray("weather").getJSONObject(0).getString("icon") + ".png";
                    System.out.println(iconID);

                    String[] week_name = {"Sun", "Mon", "Tue", "Wed",
                            "Thur", "Fri", "Sat"};
                    String temp = day.getJSONObject("temp").getString("day");
                    temp = String.format("%.2f", KelvinToFahrenheit(Float.parseFloat(temp)));
                    String temp_min = day.getJSONObject("temp").getString("min");
                    temp_min = String.format("%.2f", KelvinToFahrenheit(Float.parseFloat(temp_min)));
                    String temp_max = day.getJSONObject("temp").getString("max");
                    temp_max = String.format("%.2f", KelvinToFahrenheit(Float.parseFloat(temp_max)));
                    weather[i] = (new TenDaysWeatherPost.Builder(iconID,
//                            (currCal.get(Calendar.DATE) + "/" + (currCal.get(Calendar.MONTH) + 1)+ "/"
////                                    + week_name[currCal.get(Calendar.DAY_OF_WEEK)]),
                            "test",
                            temp_min + "/" + temp_max)
                            .build());
                }
                weathers = new ArrayList(Arrays.asList(weather));

                TenDaysWeatherModel viewModel = TenDaysWeatherModel.getFactory().create(TenDaysWeatherModel.class);
                viewModel.changeData(weathers);

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
}
