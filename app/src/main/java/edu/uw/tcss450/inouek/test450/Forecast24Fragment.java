package edu.uw.tcss450.inouek.test450;


import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import edu.uw.tcss450.inouek.test450.utils.GetAsyncTask;
import edu.uw.tcss450.inouek.test450.weather.CityPost;
import edu.uw.tcss450.inouek.test450.weather.JwTokenModel;
import edu.uw.tcss450.inouek.test450.weather.MyCityRecyclerViewAdapter;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.GetAsyncTask;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;
import edu.uw.tcss450.inouek.test450.weather.CityPost;
import edu.uw.tcss450.inouek.test450.weather.CityViewModel;
import edu.uw.tcss450.inouek.test450.weather.LocationViewModel;
import edu.uw.tcss450.inouek.test450.weather.Weather10Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class Forecast24Fragment extends Fragment {

    private static final String TAG = "Forecast24Fragment";

    TextView test;
    Adapter24Hour adapter;
    //    String lat = String.valueOf(38.123);
//    String lon = String.valueOf(-78.543);
//    String API_KEY = "328ab211749548638aae28278dfd7a9c";

    // need to pass these variables to my adaptor
    ArrayList<String[]> weatherInfo = new ArrayList<>();
    View myView;
    Credentials mCredentials;

    // weatherbit.io 
    //Using another web API: which having 48 hrs forecast
    // 185f29dc08694f95a92201318dea4b23
    //https://api.weatherbit.io/v2.0/forecast/hourly?city=Raleigh,NC&key=API_KEY&hours=48&lat=38.123&lon=-78.543

    public Forecast24Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().onBackPressed();
        mCredentials = Forecast24FragmentArgs.fromBundle(getArguments()).getCredentials();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_weather_forecast24,
                container, false);
        try {
            FindWeather();
            initRecylerView(myView);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myView;
    }

    public void initRecylerView(View view){
        Log.d(TAG, "init recycler view ");
        RecyclerView recyclerview = (RecyclerView)view.findViewById(R.id.weather_recyclerView_holder_uniqueweather_recyclerView_holder_unique);
        // create the object of recycler view adapter, getActivity() point to Context
        adapter = new Adapter24Hour(getContext(), weatherInfo);
        // set adapter to recycler view
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setAdapter(adapter);
    }

    // this method will excute the link and find the weather data and info
    public void FindWeather () throws JSONException {
        Log.e("FindWeather","Called");
        SelectLocationViewModel viewModel = SelectLocationViewModel.getFactory().create(SelectLocationViewModel.class);
        Location location = viewModel.getCurrentLocation().getValue();
        BigDecimal lat = new BigDecimal(location.getLatitude());
        BigDecimal lon = new BigDecimal(location.getLongitude());
        //https://api.darksky.net/forecast/31091e93a3b789ac4dbd6844169ebd19/37.421998,-122.084000?exclude=currently,minutely,daily,alerts,flags&extend=hourly
        Uri locationUri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_weather_24))
//              .appendQueryParameter("lat", lat.setScale(6, RoundingMode.HALF_UP))
//              .appendQueryParameter("long", lon.setScale(6, RoundingMode.HALF_UP))
//              .appendQueryParameter("days_from_today", "0")
                .build();
        Log.e("uri", locationUri.toString());
        new GetAsyncTask.Builder(locationUri.toString())
                .onPostExecute(s->{
                    Log.e("inside of the data", s);
                    Log.e("response data type", s.getClass().getName());
                    Log.e("get the position", String.valueOf(Weather10Fragment.position));

                    try {
                        //Log.e("inside of the data", s);
                        //Log.e("response data type", s.getClass().getName());
                        // s in there should be result

                        //JSONObject day = weatherArray.getJSONObject();
                        ArrayList<String[]> info = new ArrayList<>();

                        String[] data;

                        //get 10 days weather info
                        JSONArray weatherArray = new JSONArray(s);
                        for (int i = 0; i < weatherArray.length(); i++) {
                            data = new String[3];

                            JSONObject day = weatherArray.getJSONObject(i);
                            data[0]=  day.get("iconId").toString();
                            data[1]= day.get("temperature").toString();
                            data[2] = String.valueOf(i + 1);
                            info.add(data);
                        }

                        weatherInfo.addAll(info);
                        adapter.notifyDataSetChanged();

                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                } )
                .addHeaderField("lat", lat.setScale(6, RoundingMode.HALF_UP).toString())
                .addHeaderField("long", lon.setScale(6, RoundingMode.HALF_UP).toString())
                //.addHeaderField("days_from_today", String.valueOf(1))
                .addHeaderField("days_from_today", String.valueOf(Weather10Fragment.position))
                .build().execute();
    }

    // this method will excute the link and find the weather data and info
//    public void FindWeather (View v){
//        try{
//            // want to make asynctask to get the data in background
//            ExecuteTask tasky = new ExecuteTask();
//            String url = "https://api.weatherbit.io/v2.0/forecast/hourly?&key="+API_KEY+"&hours=48&lat="+lat+"&lon="+lon;
//            //tasky.execute("Here in the URL of the website"+cityToFind+"API key");
//            tasky.execute(url);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }


    // this task will get all from website in background
//    public class ExecuteTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//            String response = "";
//            HttpURLConnection urlConnection = null;
//            String url = strings[0];
//
//            try {
//                // strings[0] is the urls
//                URL urlObject = new URL(url);
//                urlConnection = (HttpURLConnection) urlObject.openConnection();
//                InputStream content = urlConnection.getInputStream();
//                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
//                String s = "";
//                while ((s = buffer.readLine()) != null) {
//                    response += s;
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return response;
//        }
//
//        @Override
//        // used to update the UI
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            try {
//
//                Log.d("myTag", "Getting some result");
//
//                String messageWeather = "";
//                String messageTemp = "";
//
//                // s in there should be result
//                JSONObject jsonObject = new JSONObject(result);
//
//                // data with 48 {} information
//                JSONArray weatherArray = jsonObject.getJSONArray("data");
//                // get the city name
//                String city = jsonObject.getString("city_name");
//
//                ArrayList<String> temp24hours = new ArrayList<>();
//
//                // Now we want to get the texts as they are in JSON ..
//                // it is case sensitive in JSON, get element in the weather
//                for (int i = 0; i < 24; i++) {
//                    JSONObject jsonSecondary = weatherArray.getJSONObject(i);
//                    String temp  = jsonSecondary.getString("temp");
//                    temp24hours.add(temp);
//
//                    String description = jsonSecondary.getJSONObject("weather").getString("description");
//
//                    if (temp != "" ) {
//                        messageWeather += "Temperature: " + temp + "\r\n" + "Description: "+ description + "\r\n";
//                    }
//
//                }
//
//                if (city != "") {
//                    messageTemp = "City : " + city + "\r\n";
//                }
//
//                if (messageTemp != "") {
//                    // send message back to text
//                    test.setText(messageWeather);
//                    weatherInfo.add(messageWeather);
//                    // add to recyler view
//                    initRecylerView(myView);
//                    //tempText.setText(messageTemp);
//                } else {
//                    //Toast.makeText(WeatherActivity.this, "An Error Occurred", Toast.LENGTH_LONG);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}



