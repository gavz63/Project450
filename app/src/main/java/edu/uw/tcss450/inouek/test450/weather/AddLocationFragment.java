package edu.uw.tcss450.inouek.test450.weather;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import edu.uw.tcss450.inouek.test450.MapFragment;
import edu.uw.tcss450.inouek.test450.MobileNavigationDirections;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddLocationFragment extends Fragment {

    private MaterialButton mButton;
    private EditText mCityNameField;
    private double mLat;
    private double mLong;
    private Credentials mCredentials;
    private String mJwt;

    public AddLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCredentials = AddLocationFragmentArgs.fromBundle(getArguments()).getCredentials();
        mJwt = AddLocationFragmentArgs.fromBundle(getArguments()).getJwt();

        mButton = view.findViewById(R.id.button_save_location);
        mCityNameField = view.findViewById(R.id.field_cityname);

        LocationViewModel viewModel = LocationViewModel.getFactory().create(LocationViewModel.class);
        Location location = viewModel.getCurrentLocation().getValue();
        mLat = location.getLatitude();
        mLong = location.getLongitude();

        mButton.setOnClickListener(this::saveLocation);
    }

    private void saveLocation(View v) {

        if (!anyErrors()) {

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather))
                    .appendPath(getString(R.string.ep_weather_locations))
                    .appendPath(getString(R.string.ep_weather_locations_add))
                    .build();

            JSONObject message = new JSONObject();
            try {
                message.put("cityname", mCityNameField.getText().toString());
                message.put("lat", mLat);
                message.put("long", mLong);
                message.put("username", mCredentials.getUsername());

            } catch (JSONException e) {
                mCityNameField.setError("JSONError my b");
            }

            new SendPostAsyncTask.Builder(uri.toString(), message)
                    .onPreExecute(() -> {
                        getActivity().findViewById(R.id.add_location_progress).setVisibility(View.VISIBLE);
                        getActivity().findViewById(R.id.button_save_location).setEnabled(false);
                    }).onPostExecute(this::handleSaveLocationOnPost)
                    .build().execute();
        }
    }

    private boolean anyErrors() {
        boolean anyErrors = false;

        if (mCityNameField.getText().toString().equals("")) {
            anyErrors = true;
            mCityNameField.setError("City name cannot be empty");
        } else {
            mCityNameField.setError(null);
        }

        return anyErrors;
    }

    private void handleSaveLocationOnPost(String s) {
        try {
            JSONObject resultsJSON = new JSONObject(s);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                NavController controller = Navigation
                        .findNavController(getActivity().findViewById(R.id.nav_host_fragment));

                MobileNavigationDirections.ActionGlobalWeatherMainFragment action =
                        MobileNavigationDirections.actionGlobalWeatherMainFragment(mCredentials, mJwt);

                controller.navigate(action);

            } else {
                mCityNameField.setError(resultsJSON.get("error").toString());
            }
        } catch (JSONException e) {
            mCityNameField.setError(e.getMessage());
        }
        getActivity().findViewById(R.id.add_location_progress).setVisibility(View.GONE);
        getActivity().findViewById(R.id.button_save_location).setEnabled(true);
    }

    public void setLat(double lat) { mLat = lat; }

    public void setLong(double lng) { mLong = lng; }
}
