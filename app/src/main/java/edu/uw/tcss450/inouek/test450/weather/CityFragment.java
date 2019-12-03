package edu.uw.tcss450.inouek.test450.weather;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.WeatherMainFragment;
import edu.uw.tcss450.inouek.test450.WeatherMainFragmentArgs;
import edu.uw.tcss450.inouek.test450.utils.GetAsyncTask;

import static edu.uw.tcss450.inouek.test450.HomeActivity.KelvinToFahrenheit;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CityFragment extends Fragment {
    private String mJwToken;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private MyCityRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<CityPost> cities;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CityFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CityFragment newInstance(int columnCount) {
        CityFragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cities = new ArrayList<>();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);
        LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
        Location location = viewModel.getCurrentLocation().getValue();

        cities.add(new CityPost.Builder("Current Location",
                String.valueOf(location.getLongitude()),
                String.valueOf(location.getLatitude())).build());
        cities.add(new CityPost.Builder("Tokyo",
                                        "35.652832",
                                        "139.839478").build());
        JwTokenModel jwTokenModel = JwTokenModel.getFactory().create(JwTokenModel.class);
        mJwToken = jwTokenModel.getJwToken().toString();

        getCityList();
        
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(recyclerViewAdapter = new MyCityRecyclerViewAdapter(cities, this::onClick));
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle saveInstanceState){
        super.onViewCreated(view,saveInstanceState);
        CityViewModel viewModel = CityViewModel.getFactory().create(CityViewModel.class);
        viewModel.getCityList().observe(this, cities->{
            recyclerViewAdapter.swap(cities);
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
    private void onClick(CityPost city){
        String cityName = city.getCity();
        String lat = city.getLat();
        String lon = city.getLong();

        Uri tenDayWeatherUri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_weather_10))
                .appendQueryParameter("lat", lat)
                .appendQueryParameter("lon", lon)
                .build();
        new GetAsyncTask.Builder(tenDayWeatherUri.toString())
                .onPostExecute(this::getTenDayWeatherOnPost)
                .addHeaderField("authorization", mJwToken) //add the JWT as a header
                .build().execute();
        System.out.println("clicked");
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
        void onListFragmentInteraction(CityPost item);
    }


    private void getCityList(){
        Uri tenDayWeatherUri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_weather_locations))
                .build();
        new GetAsyncTask.Builder(tenDayWeatherUri.toString())
                .onPostExecute(this::getCityOnPost)
                .addHeaderField("authorization", mJwToken) //add the JWT as a header
                .build().execute();
    }

    private void getCityOnPost(String s) {

        try {

            // s in there should be result

            JSONArray cityArray = new JSONArray(s);

            CityPost[] cityPosts = new CityPost[cityArray.length()];
            //get 10 days weather info
            for (int i = 0; i < cityArray.length(); i++) {


                JSONObject day = cityArray.getJSONObject(i);
                String cityName = day.getString("nickname");
                String lat = day.getString("Lat");
                String lon = day.getString("Long");

                cityPosts[i] = (new CityPost.Builder(cityName, lon, lat)
                        .build());
            }
            cities = new ArrayList(Arrays.asList(cityPosts));

            CityViewModel viewModel = CityViewModel.getFactory().create(CityViewModel.class);
            viewModel.changeData(cities);

        }catch(JSONException e){
            e.printStackTrace();
        }

        return;
    }

    private void getTenDayWeatherOnPost(String s) {

        try {

            // s in there should be result
            //JSONObject jsonObject = new JSONObject(s);

            //String tenDaysWeather = jsonObject.getString("list");

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
               //System.out.println(iconID);

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
            ArrayList<TenDaysWeatherPost> weathers = new ArrayList(Arrays.asList(weather));

            TenDaysWeatherModel viewModel = TenDaysWeatherModel.getFactory().create(TenDaysWeatherModel.class);
            viewModel.changeData(weathers);

            System.out.println("end update");
        }catch(JSONException e){
            e.printStackTrace();
        }

    }
}
