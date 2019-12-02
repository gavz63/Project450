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
import edu.uw.tcss450.inouek.test450.weather.CityFragment;
import edu.uw.tcss450.inouek.test450.weather.CityPost;
import edu.uw.tcss450.inouek.test450.weather.LocationViewModel;
import edu.uw.tcss450.inouek.test450.weather.TenDaysWeatherModel;
import edu.uw.tcss450.inouek.test450.weather.TenDaysWeatherPost;
import edu.uw.tcss450.inouek.test450.weather.Weather10Fragment;

//Testing change on git
public class HomeActivity extends AppCompatActivity implements Weather10Fragment.OnListFragmentInteractionListener, CityFragment.OnListFragmentInteractionListener {

    public static final int MONKEY_YELLOW = 1;
    public static final int MONKEY_GREEN = 2;
    public static final int MONKEY_RED = 3;
    public static final int MONKEY_PINK = 4;
    public static final int MONKEY_BLUE = 5;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000000000;
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
            //requestLocation();
            createLocationRequest();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
                    viewModel.changeLocation(location);
                    Log.d("Location Update", location.toString());
                }

            }
        };
        createLocationRequest();

        //findWeather();

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

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        requestLocation();
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
                        WeatherMainFragmentDirections.actionGlobalWeatherMainFragment(mCredentials, mJwToken);
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
    public void onResume() {
        super.onResume();
        //findWeather();
        //Start location update
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    public void onPause() {
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
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // this method will execute the link and find the weather data and info
    public void findWeather() {
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
    }


    public static float KelvinToFahrenheit(float degree) {
        return degree * 9 / 5 - 459.67f;
    }

    private void getTenDayWeatherOnPost(String s) {

        try {
            System.out.println(s);

            JSONArray weatherArray = new JSONArray(s);

            TenDaysWeatherPost[] weather = new TenDaysWeatherPost[weatherArray.length()];
            //get 10 days weather info
            for (int i = 0; i < weatherArray.length(); i++) {


                JSONObject day = weatherArray.getJSONObject(i);

                long time = Integer.valueOf(day.getString("date")).intValue();
                Calendar currCal = Calendar.getInstance();
                Date dateObject = new Date(time * 1000);
                currCal.setTime(dateObject);
                //Date currCalDate = new Date(time);
                String iconID = day.getString("iconId");
                System.out.println(iconID);

                String[] week_name = {"Sun", "Mon", "Tue", "Wed",
                        "Thur", "Fri", "Sat"};
                String temp_min = day.getString("minTemp");
                temp_min = String.format("%.2f", KelvinToFahrenheit(Float.parseFloat(temp_min)));
                String temp_max = day.getString("maxTemp");
                temp_max = String.format("%.2f", KelvinToFahrenheit(Float.parseFloat(temp_max)));

                int date = currCal.get(Calendar.DAY_OF_MONTH);
                int month = currCal.get(Calendar.MONTH) + 1;
                weather[i] = (new TenDaysWeatherPost.Builder(iconID,
                        "" + month + " / " + date + " / "
                                + week_name[currCal.get(Calendar.DAY_OF_WEEK)],
                        temp_min + "/" + temp_max)
                        .build());
            }
            weathers = new ArrayList(Arrays.asList(weather));

            TenDaysWeatherModel viewModel = TenDaysWeatherModel.getFactory().create(TenDaysWeatherModel.class);
            viewModel.changeData(weathers);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onListFragmentInteraction(TenDaysWeatherPost item) {

    }

    @Override
    public void onListFragmentInteraction(CityPost item) {

    }
}
