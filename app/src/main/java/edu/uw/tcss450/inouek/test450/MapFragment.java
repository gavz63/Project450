package edu.uw.tcss450.inouek.test450;


import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.uw.tcss450.inouek.test450.weather.LocationViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener{

    private GoogleMap mMap;
    private String geoInfo;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //add this fragment as the OnMapReadyCallback -> See onMapReady()
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //Go grab a reference to the ViewModel.
        LocationViewModel model =  LocationViewModel.getFactory().create(LocationViewModel.class);
        Location l = model.getCurrentLocation().getValue();

        // Add a marker in the current device location and move the camera
        LatLng current = new LatLng(l.getLatitude(), l.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));


        //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));

        //Add a observer to the ViewModel. MainActivity is listening to changes to the device
        //location. It reports those changes to the ViewModel. This is an observer on
        //the ViewModel and will act on those changes.
        model.getCurrentLocation().observe(this, location -> {
            final LatLng c = new LatLng(location.getLatitude(), location.getLongitude());
            //Zoom levels are from 2.0f (zoomed out) to 21.f (zoomed in)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 15.0f));
        });

        mMap.setOnMapClickListener(this);
    }


    @Override
    public void onMapClick(LatLng latLng) {
//        Log.d("LAT/LONG", latLng.toString());
//
//        geoInfo = latLng.toString();
//
//        Marker marker = mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("New Marker"));
//
//        Double l1=latLng.latitude;
//        Double l2=latLng.longitude;
//        String coordl1 = l1.toString();
//        String coordl2 = l2.toString();
//
//        // latlng will return
//        // bundle to store the longtitude and latitude information
//        Fragment fragment = new Fragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("geoInfoFromMap", geoInfo);
//        bundle.putString("geoLatitude", coordl1);
//        bundle.putString("geoLongtitude", coordl2);
//        fragment.setArguments(bundle);
//
//        // TODO set location to be saved

    }
}


