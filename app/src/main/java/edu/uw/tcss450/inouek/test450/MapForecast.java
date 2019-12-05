package edu.uw.tcss450.inouek.test450;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapForecast extends Fragment {

    Bundle bundle = this.getArguments();

    String lantitude = bundle.getString("geoLatitude");
    String longtitude = bundle.getString("geoLongtitude");

    String API_KEY = "328ab211749548638aae28278dfd7a9c";

    TextView text;

    View myView;

    public MapForecast() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_map_forecase,
                container, false);

        return myView;
    }

    // this method will excute the link and find the weather data and info
    public void FindWeather (View v){
        try{
            // want to make asynctask to get the data in background
            MapForecast.ExecuteTask tasky = new MapForecast.ExecuteTask();
            //https://api.weatherbit.io/v2.0/current?&key=328ab211749548638aae28278dfd7a9c&lat=38.123&lon=-78.543
            String url = "https://api.weatherbit.io/v2.0/current?" + "&key="+API_KEY + "&lat="+lantitude+"&lon="+longtitude;
            //tasky.execute("Here in the URL of the website"+cityToFind+"API key");
            tasky.execute(url);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    // this task will get all from website in background
    public class ExecuteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String response = "";
            HttpURLConnection urlConnection = null;
            String url = strings[0];

            try {
                // strings[0] is the urls
                URL urlObject = new URL(url);
                urlConnection = (HttpURLConnection) urlObject.openConnection();
                InputStream content = urlConnection.getInputStream();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        // used to update the UI
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                Log.d("myTag", "Getting some result");

                String messageWeather = "";
                String messageTemp = "";

                // s in there should be result
                JSONObject jsonObject = new JSONObject(result);

                // data with 48 {} information
                JSONArray weatherArray = jsonObject.getJSONArray("data");
                // get temperature
                String temp = jsonObject.getString("temp");

                String city_name = "";
                String description = "";

                // Now we want to get the texts as they are in JSON ..
                // it is case sensitive in JSON, get element in the weather
                for (int i = 0; i < 24; i++) {
                    JSONObject jsonSecondary = weatherArray.getJSONObject(i);
                    city_name  = jsonSecondary.getString("city_name");
                    description = jsonSecondary.getJSONObject("weather").getString("description");

                    if (temp != "" ) {
                        messageWeather = "Temperature: " + city_name + "\r\n" + "Description: "+ description + "\r\n";
                    }

                }

                if (temp != "") {
                    messageTemp = "Temperature : " + temp + "\r\n";
                }

                if (messageTemp != "") {
                    // send message back to text
                    text.setText(messageWeather);
                    //tempText.setText(messageTemp);
                } else {
                    //Toast.makeText(WeatherActivity.this, "An Error Occurred", Toast.LENGTH_LONG);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
