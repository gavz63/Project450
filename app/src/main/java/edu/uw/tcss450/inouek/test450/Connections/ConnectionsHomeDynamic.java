package edu.uw.tcss450.inouek.test450.Connections;


import android.app.ActionBar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListFragmentDirections;
import edu.uw.tcss450.inouek.test450.ConnectionsNavDynamicDirections;
import edu.uw.tcss450.inouek.test450.MobileNavigationDirections;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsHomeDynamic extends Fragment {

    private Credentials mCredentials;

    public ConnectionsHomeDynamic() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCredentials = ConnectionsHomeDynamicArgs.fromBundle(getArguments()).getCredentials();
        Log.e("ConnectionsHomeDynamic", "Received Email: " + mCredentials.getEmail());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connections_home_dynamic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigation = view.findViewById(R.id.connections_nav_bar_base);
        bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationSelected);
    }

    private boolean onNavigationSelected(final MenuItem menuItem) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
        switch (menuItem.getItemId())
        {
            case R.id.connections_nav_bar_connections:
                ConnectionsNavDynamicDirections.ActionGlobalNavLanding connectionsHome =
                        ConnectionsNavDynamicDirections.actionGlobalNavLanding(mCredentials);
                navController.navigate(connectionsHome);
                return true;
            case R.id.connections_nav_bar_sent:
                ConnectionsNavDynamicDirections.ActionGlobalNavSent sent =
                        ConnectionsNavDynamicDirections.actionGlobalNavSent(mCredentials);
                navController.navigate(sent);
                return true;
            case R.id.connections_nav_bar_received:
                ConnectionsNavDynamicDirections.ActionGlobalNavReceived received =
                        ConnectionsNavDynamicDirections.actionGlobalNavReceived(mCredentials);
                navController.navigate(received);
                return true;
        }
        return false;
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

}
