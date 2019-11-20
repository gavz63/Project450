package edu.uw.tcss450.inouek.test450;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.uw.tcss450.inouek.test450.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class testFragmentFromWeatherFragment extends Fragment {


    Button button;

    public testFragmentFromWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_test_fragment_from_weather, container, false);

        button = (Button)v.findViewById(R.id.predict);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //??????
            }
        });

        return v;
    }

}
