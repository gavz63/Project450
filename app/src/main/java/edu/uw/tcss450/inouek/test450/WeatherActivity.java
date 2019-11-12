package edu.uw.tcss450.inouek.test450;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
//import android.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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



public class WeatherActivity extends AppCompatActivity {

    EditText cityField;
    TextView weatherText;
    TextView tempText;
    String zipCode;
    Button button;

    Button predictToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // change the activity layout and want it to show
        setContentView(R.layout.activity_weather);


        weatherText = findViewById(R.id.result);
        cityField = findViewById(R.id.city);
        tempText = findViewById(R.id.tempText);

        button = (Button) findViewById(R.id.button);
        predictToday = (Button) findViewById(R.id.button_today);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zipCode = cityField.getText().toString();
                FindWeather(v);

            }
        });

        //when click predict today button, will open a new fragment ,showing 24 hours forecasting
        predictToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump to a totally new fragment from activity
                Fragment fragment = new WeatherForecast24();
                ((LinearLayout)findViewById(R.id.rootView)).removeAllViews();
                replaceFragment(fragment);

            }
        });
    }

    // show the somefrgment result into the weather activty page
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // replace weather activity by somefragment
        transaction.replace(R.id.fragmentContainer, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // this method will excute the link and find the weather data and info
    public void FindWeather (View v){
        try{
            // want to make asynctask to get the data in background
            ExecuteTask tasky = new ExecuteTask();
            //tasky.execute("Here in the URL of the website"+cityToFind+"API key");
            tasky.execute("http://api.openweathermap.org/data/2.5/weather?zip="+zipCode+",us&appid=4e6149bb3debe832f3d55ff70ec9b2f4");
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
                String messageWeather = "";
                String messageTemp = "";

                // s in there should be result
                JSONObject jsonObject = new JSONObject(result);

                String infoWeatherToday = jsonObject.getString("weather");

                JSONArray weatherArray = new JSONArray(infoWeatherToday);

                // Now we want to get the texts as they are in JSON ..
                // it is case sensitive in JSON, get element in the weather

                for (int i = 0; i < weatherArray.length(); i++) {

                    JSONObject jsonSecondary = weatherArray.getJSONObject(i);
                    String main = "";
                    String description = "";

                    main = jsonSecondary.getString("main");
                    description = jsonSecondary.getString("description");

                    if (main != "" && description != "") {
                        messageWeather += main + ": " + description + "\r\n";
                    }
                }

                //infoTempToday : {"temp":280.33,"pressure":1028,"humidity":100,"temp_min":278.71,"temp_max":282.04}
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String temp_min = jsonObject.getJSONObject("main").getString("temp_min");
                String temp_max = jsonObject.getJSONObject("main").getString("temp_max");
                String city = jsonObject.getString("name");

                if (temp != "" && temp_min != "" && temp_max != "") {
                    messageTemp = "City : " + city + "\r\n"
                                    + "Temperature : " + temp + "\r\n"
                                    + "Min Temperature : " + temp_min + "\r\n"
                                    + "Max Temperature : " + temp_max + "\r\n";

                }

                if (temp_min == "" || temp_max == "") {
                    messageTemp += "Temperature : " + temp + "\r\n";
                }


                if (messageTemp != "") {
                    weatherText.setText(messageWeather);
                    tempText.setText(messageTemp);
                } else {
                    //Toast.makeText(WeatherActivity.this, "An Error Occurred", Toast.LENGTH_LONG);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
