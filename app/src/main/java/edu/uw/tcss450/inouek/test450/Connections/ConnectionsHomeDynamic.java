package edu.uw.tcss450.inouek.test450.Connections;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import edu.uw.tcss450.inouek.test450.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsHomeDynamic extends Fragment {


    public ConnectionsHomeDynamic() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button requests = (Button) view.findViewById(R.id.connections_button_connections);
        if(requests != null) {
            Log.e("Not NULL", "NOT NULL");
            requests.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(view).navigate(R.id.action_connectionsHomeDynamic_to_connectionsRequestFragment);
                }
            });
        }
    }

    String[] getAutoCompleteFields()
    {
        String[] names = getResources().getStringArray(R.array.connections_auto_complete_names);
        String[] usernames = getResources().getStringArray(R.array.connections_auto_complete_usernames);
        String[] emails = getResources().getStringArray(R.array.connections_auto_complete_emails);

        int length = names.length + usernames.length + emails.length;
        String[] array = new String[length];
        for(int i = 0; i < length/3; i++)
        {
            int index = i * 3;
            array[index] = names[i] + ", " + emails[i];
            array[index+1] = usernames[i];
            array[index+2] = emails[i];
        }
        return array;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_connections_home_dynamic, container, false);
        final String[] PROFILES = getAutoCompleteFields();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        PROFILES);
        AutoCompleteTextView text = (AutoCompleteTextView) v.findViewById(R.id.connections_text_search);
        text.setAdapter(adapter);

        return v;
    }

}
