package edu.uw.tcss450.inouek.test450.weather;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.ui.home.HomeFragmentDirections;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class Weather10FragmentInMain extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private ArrayList<TenDaysWeatherPost> weathersArray;
    private OnListFragmentInteractionListener mListener;
    private MyWeatherRecyclerViewAdapter recyclerViewAdapter;

    public static Credentials mCredentials;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public Weather10FragmentInMain() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static Weather10FragmentInMain newInstance(int columnCount) {
        Weather10FragmentInMain fragment = new Weather10FragmentInMain();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weathersArray = new ArrayList<>();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_10_days_weather_list, container, false);

        weathersArray.add(new TenDaysWeatherPost.Builder( "01d",
                "0",
                "0").build());

        // Set the adapter
        // TODO: ここで処理を実行する
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(recyclerViewAdapter = new MyWeatherRecyclerViewAdapter(weathersArray, Weather10FragmentInMain.this::onClick));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState){
        super.onViewCreated(view,saveInstanceState);
        CurrentFiveDaysWeatherModel viewModel = CurrentFiveDaysWeatherModel.getFactory().create(CurrentFiveDaysWeatherModel.class);

        viewModel.getCurrentWeather().observe(this, weathers->{
            if(this.isVisible()){
                Log.e("home is visible?", "Yes!");
                recyclerViewAdapter.swap(weathers);
                recyclerViewAdapter.notifyDataSetChanged();
            } else {
                Log.e("home is visible?", "No....");

            }
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
        HomeFragmentDirections.ActionNavHomeToForecast24Fragment nav =
                HomeFragmentDirections.actionNavHomeToForecast24Fragment(
                        mCredentials
                );
        Navigation.findNavController(getView()).navigate(nav);

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

}
