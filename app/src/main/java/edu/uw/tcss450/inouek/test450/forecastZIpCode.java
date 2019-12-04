package edu.uw.tcss450.inouek.test450;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class forecastZIpCode extends Fragment {

    View myView;

    EditText zipcode;
    TextView textView;
    // open weather api
    String API_KEY = "4e6149bb3debe832f3d55ff70ec9b2f4";

    public forecastZIpCode() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_forecast_zip_code,
                container, false);
        return myView;

    }

    // this method will excute the link and find the weather data and info
    public void FindWeather (View v){
        try{
            // want to make asynctask to get the data in background
            forecastZIpCode.ExecuteTask tasky = new forecastZIpCode.ExecuteTask();
            //https://samples.openweathermap.org/data/2.5/forecast?q=München,DE&appid=b6907d289e10d714a6e88b30761fae22
            //api.openweathermap.org/data/2.5/weather?zip=94040,us
            String url = "https://api.openweathermap.org/data/2.5/weather?zip=" + zipcode + ",us&appid="+API_KEY;
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

                //
                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                // now get the first element:
                JSONObject firstSport = weatherArray.getJSONObject(0);
                // temperature
                String description = firstSport.getString("description");


                String city_name = "";

                // Now we want to get the texts as they are in JSON ..
                // it is case sensitive in JSON, get element in the weather


                if (description != "") {
                    messageTemp = "Description : " + description + "\r\n";
                }

                if (messageTemp != "") {
                    // send message back to text
                    textView.setText(messageWeather);
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
