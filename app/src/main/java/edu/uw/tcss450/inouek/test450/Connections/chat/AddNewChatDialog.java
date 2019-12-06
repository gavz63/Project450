package edu.uw.tcss450.inouek.test450.Connections.chat;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.inouek.test450.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewChatDialog extends Fragment {


    public AddNewChatDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_chat_dialog, container, false);
    }

}
