package edu.uw.tcss450.inouek.test450.Connections;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uw.tcss450.inouek.test450.Connections.Profile.ProfileContent;
import edu.uw.tcss450.inouek.test450.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewProfileFragment extends Fragment {


    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(getArguments() != null) {
            ProfileContent.Profile s = (ProfileContent.Profile) getArguments().getSerializable(getString(R.string.profile_key));
            TextView name = getActivity().findViewById(R.id.profile_text_name);
            TextView id = getActivity().findViewById(R.id.profile_text_id);
            TextView email = getActivity().findViewById(R.id.profile_text_email);
            TextView userName = getActivity().findViewById(R.id.profile_text_username);

            name.setText(s.name);
            id.setText(s.id);
            email.setText(s.email);
            userName.setText(s.username);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_profile, container, false);
    }



}
