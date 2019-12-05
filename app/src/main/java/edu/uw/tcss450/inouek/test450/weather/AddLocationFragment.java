package edu.uw.tcss450.inouek.test450.weather;


import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import org.json.JSONObject;

import edu.uw.tcss450.inouek.test450.MapFragment;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddLocationFragment extends Fragment implements GoogleMap.OnMapClickListener{

    MaterialButton mButton;
    EditText mZipField;
    EditText mCityNameField;
    MapFragment mMapFragment;

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

        mButton = view.findViewById(R.id.button_save_location);
        mZipField = view.findViewById(R.id.field_search_zipcode);
        mCityNameField = view.findViewById(R.id.field_cityname);
        FragmentManager manager = getFragmentManager();
        mMapFragment = (MapFragment) manager.findFragmentById(R.id.fragment_map);
        //mMapFragment.onMapClick(this::onMapClick);

        mButton.setOnClickListener(this::saveLocation);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d("LAT/LONG", latLng.toString());
    }

    private void saveLocation(View v) {

        if (!anyErrors()) {

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_weather_locations))
                    .appendPath(getString(R.string.ep_weather_locations_add))
                    .build();

            JSONObject message = new JSONObject();

//            new SendPostAsyncTask.Builder(uri.toString(), message)
//                    .onPreExecute()
//                    .onPostExecute()
//                    .build().execute();
        }
    }

    private boolean anyErrors() {
        boolean anyErrors = false;

        try {
            Integer.parseInt(mZipField.getText().toString());
        } catch (NumberFormatException e) {
            anyErrors = true;
        }

        if (mZipField.getText().toString().length() != 5) {
            anyErrors = true;
            if (mZipField.getText().toString().equals("")) {
                mZipField.setError("No zipcode entered");
            }
        }

        if (anyErrors) {
            mZipField.setError("Zipcode is invalid");
        } else {
            mZipField.setError(null);
        }

        if (mCityNameField.getText().toString().equals("")) {
            anyErrors = true;
            mCityNameField.setError("City name cannot be empty");
        } else {
            mCityNameField.setError(null);
        }

        return anyErrors;
    }


}
