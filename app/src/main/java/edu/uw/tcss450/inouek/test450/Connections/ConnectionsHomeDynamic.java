package edu.uw.tcss450.inouek.test450.Connections;


import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.inouek.test450.ConnectionsNavDynamicDirections;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsHomeDynamic extends Fragment {

    private Credentials mCredentials;
    private AutoCompleteTextView mAutoCompleteTextView;
    private MaterialButton mSendFriendRequestButton;

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

        FloatingActionButton fab = view.findViewById(R.id.connections_floatingActionButton);
        fab.setOnClickListener(this::fabOnClick);
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

    private void fabOnClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_connections_auto_complete, null);
        mAutoCompleteTextView = dialogView.findViewById(R.id.search_for_friends_autocomplete);
        mSendFriendRequestButton = dialogView.findViewById(R.id.button_send_friend_request);
        mSendFriendRequestButton.setOnClickListener(this::sendFriendRequestOnClick);

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_contacts_find_new))
                .build();

        JSONObject message = mCredentials.asJSONObject();

        new SendPostAsyncTask.Builder(uri.toString(), message)
                .onPreExecute(() -> mSendFriendRequestButton.setEnabled(false))
                .onPostExecute(this::handleGetSuggestionsOnPost)
                .build().execute();
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendFriendRequestOnClick(View v) {
        String autoCompleteText = mAutoCompleteTextView.getText().toString();
        boolean error = false;
        if (autoCompleteText.equals("")) {
            mAutoCompleteTextView.setError("Please search for a user");
            error = true;
        } else {
            mAutoCompleteTextView.setError(null);
            error = false;
        }
        if (!error) {
            String[] split = autoCompleteText.split(" : ");
            String username = split[split.length - 1];

            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_contacts))
                    .appendPath(getString(R.string.ep_contacts_send))
                    .build();

            JSONObject message = new JSONObject();

            try {
                message.put("sender", mCredentials.getUsername());
                message.put("receiver", username);
            } catch (JSONException e) {
                mAutoCompleteTextView.setError("Cannot build JSON Object oopsie");
            }

            new SendPostAsyncTask.Builder(uri.toString(), message)
                    .onPostExecute(this::handleSendFriendRequestOnPost)
                    .onPreExecute(() -> mSendFriendRequestButton.setEnabled(false))
                    .build().execute();
        }
    }

    private void handleGetSuggestionsOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                JSONArray newContacts = resultsJSON.getJSONArray("newContacts");

                String[] searchSuggestions = new String[newContacts.length()];

                for (int i = 0; i < newContacts.length(); i++) {
                    JSONObject userJSON = newContacts.getJSONObject(i);

                    String username = userJSON.getString("username");
                    String firstname = userJSON.getString("firstname");
                    String lastname = userJSON.getString("lastname");

                    searchSuggestions[i] = lastname
                            + ", " + firstname
                            + " : " + username;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line,
                        searchSuggestions);
                mAutoCompleteTextView.setAdapter(adapter);
                mAutoCompleteTextView.setError(null);
                mSendFriendRequestButton.setEnabled(true);
            } else {
                mAutoCompleteTextView.setError("Cannot get new friends at this time");
            }
        } catch (JSONException e) {
            //It appears that the web service did not return a JSON formatted
            //String or it did not have what we expected in it.
            mAutoCompleteTextView.setError("JSONException getting users");
        }
    }

    private void handleSendFriendRequestOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                mAutoCompleteTextView.setError(null);
                mAutoCompleteTextView.setText("");
                mAutoCompleteTextView.setHint("Friend request sent!");
            } else {
                if (resultsJSON.getString("error").startsWith("Receiver")) {
                    mAutoCompleteTextView.setError("Please try again, the given user does not exist");
                } else {
                    mAutoCompleteTextView.setError("Cannot send friend request");
                }
            }
            mSendFriendRequestButton.setEnabled(true);
        } catch (JSONException e) {
            mAutoCompleteTextView.setError("JSONException Sending request");
            mSendFriendRequestButton.setEnabled(true);
        }
    }
}
