package edu.uw.tcss450.inouek.test450.weather;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.WeatherMainFragment;
import edu.uw.tcss450.inouek.test450.WeatherMainFragmentArgs;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.GetAsyncTask;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

import static edu.uw.tcss450.inouek.test450.HomeActivity.KelvinToFahrenheit;
import static edu.uw.tcss450.inouek.test450.WeatherMainFragmentArgs.fromBundle;


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
    public static Credentials mCredentials;

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
        LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
        Location location = viewModel.getCurrentLocation().getValue();
        cities.add(new CityPost.Builder("Current Location",
                String.valueOf(location.getLongitude()),
                String.valueOf(location.getLatitude())).build());
        cities.add(new CityPost.Builder("Tokyo",
                "35.652832",
                "139.839478").build());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_list, container, false);

        JwTokenModel jwTokenModel = JwTokenModel.getFactory().create(JwTokenModel.class);
        mJwToken = jwTokenModel.getJwToken().toString();

        try {
            getCityList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            Uri.Builder tenDayWeatherUri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_weather_10));
            MyCityRecyclerViewAdapter.uri = tenDayWeatherUri;

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


    private void getCityList() throws JSONException {
        Log.e("getCityList","Called");
        Uri locationUri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_weather_locations))
                .build();
        JSONObject json = new JSONObject();
        json.put("username", mCredentials.getUsername());
        new SendPostAsyncTask.Builder(locationUri.toString(), json)
                .onPostExecute(s->{
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
                        cities = new ArrayList();
                        LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
                        Location location = viewModel.getCurrentLocation().getValue();
                        BigDecimal lat = new BigDecimal(location.getLatitude());
                        BigDecimal lon = new BigDecimal(location.getLongitude());

                        cities.add(new CityPost.Builder("Current Location",
                                (lat.setScale(6, RoundingMode.HALF_UP).toString()),
                                 lon.setScale(6, RoundingMode.HALF_UP).toString()).build());

                        cities.add(new CityPost.Builder("New York",
                                "40.730610",
                                "-73.935242").build());

                        cities.add(new CityPost.Builder("Tokyo",
                                "35.652832",
                                "139.839478").build());


                        cities.add(new CityPost.Builder("Mexico",
                                "19.432608",
                                "-99.133209").build());
                        cities.addAll(Arrays.asList(cityPosts));
                        CityViewModel cityViewModel = CityViewModel.getFactory().create(CityViewModel.class);
                        cityViewModel.changeData(cities);


                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                })
                .build().execute();
    }


}
