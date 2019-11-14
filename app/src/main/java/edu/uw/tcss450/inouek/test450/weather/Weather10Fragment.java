package edu.uw.tcss450.inouek.test450.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.uw.tcss450.inouek.test450.R;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class Weather10Fragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ArrayList<TenDaysWeatherPost> weathersArray;
    private OnListFragmentInteractionListener mListener;
    private MyWeatherRecyclerViewAdapter recyclerViewAdapter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Weather10Fragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Weather10Fragment newInstance(int columnCount) {
        Weather10Fragment fragment = new Weather10Fragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weathersArray = new ArrayList<TenDaysWeatherPost>();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_10_days_weather_list, container, false);
        //container.removeAllViews();

        weathersArray.add(new TenDaysWeatherPost.Builder( null,
                "0",
                "0").build());
//        TenDaysWeatherModel viewModel = TenDaysWeatherModel.getFactory().create(TenDaysWeatherModel.class);
//        weathers = viewModel.getCurrentLocation().getValue();

        //FindWeather();

        // Set the adapter
        // TODO: ここで処理を実行する
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(recyclerViewAdapter = new MyWeatherRecyclerViewAdapter(weathersArray, Weather10Fragment.this::onClick));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState){
        super.onViewCreated(view,saveInstanceState);
        TenDaysWeatherModel viewModel = TenDaysWeatherModel.getFactory().create(TenDaysWeatherModel.class);
        viewModel.getCurrentWeather().observe(this, weathers->{
            recyclerViewAdapter.swap(weathers);
            recyclerViewAdapter.notifyDataSetChanged();
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onClick(TenDaysWeatherPost weather){
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(TenDaysWeatherPost item);
    }



    // this method will excute the link and find the weather data and info
//    public void FindWeather (){
//        try{
//            // want to make asynctask to get the data in background
//            Weather10Fragment.ExecuteTask tasky = new Weather10Fragment.ExecuteTask();
//            LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
//            Location location = viewModel.getCurrentLocation().getValue();
//            //tasky.execute("Here in the URL of the website"+cityToFind+"API key");
//            tasky.execute("https://samples.openweathermap.org/data/2.5/forecast/daily?lat=" + location.getLatitude()
//                    + "&lon=" + location.getLongitude() + "&cnt=10,us&appid=4e6149bb3debe832f3d55ff70ec9b2f4");
//        } catch(Exception e) {
//            e.printStackTrace();
//
//        }
//    }


//    public static float KelvinToFahrenheit(float degree)
//    {
//        return degree * 9/5 - 459.67f;
//    }



    // this task will get all from website in background
//    public class ExecuteTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//            StringBuilder sb = new StringBuilder();
//            HttpURLConnection urlConnection = null;
//            String url = strings[0];
//
//            try {
//                // strings[0] is the urls
//                URL urlObject = new URL(url);
//                urlConnection = (HttpURLConnection) urlObject.openConnection();
//                InputStream content = urlConnection.getInputStream();
//                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
//                String line;
//
//                while (null != (line = buffer.readLine())) {
//
//                    sb.append(line);
//
//                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//            return sb.toString();
//        }
//
//        @Override
//        // used to update the UI
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            try {
//
//                // s in there should be result
//                JSONObject jsonObject = new JSONObject(result);
//
//                String tenDaysWeather = jsonObject.getString("list");
//
//                JSONArray weatherArray = new JSONArray(tenDaysWeather);
//
//                TenDaysWeatherPost[] weather = new TenDaysWeatherPost[weatherArray.length()];
//                //get 10 days weather info
//                //weathers.clear();
//                for (int i = 0; i < weatherArray.length(); i++) {
//
//
//                    JSONObject day = weatherArray.getJSONObject(i);
//
//                    long time = Integer.valueOf(day.getString("dt")).intValue();
//                    Calendar currCal = Calendar.getInstance();
//                    currCal.setTimeInMillis(time);
//                    Date currCalDate = new Date(time);
//                    String iconID = "http://openweathermap.org/img/w/" +day.getJSONArray("weather").getJSONObject(0).getString("icon");
//
//                    URL url = new URL(iconID);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setDoInput(true);
//                    conn.connect();
//                    BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
//                    Bitmap icon = BitmapFactory.decodeStream(in);
//                    in.close();
//                    conn.disconnect();
//
//
//                    String temp = day.getJSONObject("temp").getString("day");
//                    temp = String.format("%.2f",KelvinToFahrenheit(Float.parseFloat(temp)));
//                    String temp_min = day.getJSONObject("temp").getString("min");
//                    temp_min = String.format("%.2f",KelvinToFahrenheit(Float.parseFloat(temp_min)));
//                    String temp_max = day.getJSONObject("temp").getString("max");
//                    temp_max = String.format("%.2f",KelvinToFahrenheit(Float.parseFloat(temp_max)));
//                    weathers.add(new TenDaysWeatherPost.Builder(icon,
//                            temp_min+"/"+temp_max,
//                            (currCalDate.getDay() + "/" + currCalDate.getMonth() +"\n" + currCalDate.getDate())).build());
//
//
//                }
//
//                //weathers = new ArrayList(Arrays.asList(weather));
//                //TenDaysWeatherModel viewModel = TenDaysWeatherModel.getFactory().create(TenDaysWeatherModel.class);
//                //weathers = viewModel.getCurrentLocation().getValue();
//                recyclerViewAdapter.swap(weathers);
//                //recyclerViewAdapter.notifyDataSetChanged();
//
//                //System.out.println("set array");
//
//
//                //getFragmentManager().beginTransaction().replace(R.id.fragment_weathers, weatherFragment.this).commit();
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
