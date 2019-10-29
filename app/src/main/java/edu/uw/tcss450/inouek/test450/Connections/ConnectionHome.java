package edu.uw.tcss450.inouek.test450.Connections;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.inouek.test450.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionHome extends Fragment {


    public ConnectionHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_connection_home, container, false);

        //Navigation.findNavController(v).navigate(R.id.action_connectionHome_to_connectionsHomeDynamic);

        return v;
    }

}
