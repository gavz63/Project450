package edu.uw.tcss450.inouek.test450;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.weather.CityFragment;
import edu.uw.tcss450.inouek.test450.weather.MyWeatherRecyclerViewAdapter;
import edu.uw.tcss450.inouek.test450.weather.TenDaysWeatherModel;
import edu.uw.tcss450.inouek.test450.weather.TenDaysWeatherPost;
import edu.uw.tcss450.inouek.test450.weather.Weather10Fragment;

/**
 * A simple {@link Fragment} subclass.
 * yeah
 * hi kota =)
 * hi thanks gavin
 * now its getting tough
 *
 */
public class WeatherMainFragment extends Fragment {
    Credentials mCredentials;
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ArrayList<TenDaysWeatherPost> weathersArray;
    private Weather10Fragment.OnListFragmentInteractionListener mListener;
    private MyWeatherRecyclerViewAdapter recyclerViewAdapter;


    public WeatherMainFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weathersArray = new ArrayList<TenDaysWeatherPost>();
        try {
            mCredentials = WeatherMainFragmentArgs.fromBundle(getArguments()).getCredentials();
            CityFragment.mCredentials = mCredentials;
            Weather10Fragment.mCredentials = mCredentials;
            Log.e("setting", "mCredentials");
        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wather_main, container, false);

        TenDaysWeatherModel viewModel = TenDaysWeatherModel.getFactory().create(TenDaysWeatherModel.class);
        weathersArray = viewModel.getCurrentWeather().getValue();

        // open map fragment
        FloatingActionButton fab = view.findViewById(R.id.add_city_button);
        fab.setOnClickListener(v ->
                Navigation.findNavController(getActivity().findViewById(R.id.nav_host_fragment))
                        .navigate(R.id.action_weatherMainFragment_to_forecast24Fragment));



        return view;
    }


}
