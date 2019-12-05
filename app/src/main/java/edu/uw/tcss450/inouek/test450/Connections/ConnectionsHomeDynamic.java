package edu.uw.tcss450.inouek.test450.Connections;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.ContactsContract;
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

import java.util.ArrayList;

import edu.uw.tcss450.inouek.test450.Connections.Profile.ProfileContent;
import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListFragmentDirections;
import edu.uw.tcss450.inouek.test450.ConnectionsNavDynamicDirections;
import edu.uw.tcss450.inouek.test450.MobileNavigationDirections;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.model.Credentials;
import edu.uw.tcss450.inouek.test450.utils.PushReceiver;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;
import edu.uw.tcss450.inouek.test450.weather.JwTokenModel;

import static edu.uw.tcss450.inouek.test450.Connections.Profile.ProfileContent.PROFILES;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsHomeDynamic extends Fragment {

    private Credentials mCredentials;
    private AutoCompleteTextView mAutoCompleteTextView;
    private MaterialButton mSendFriendRequestButton;
    private String mJwToken;
    BottomNavigationView mBottomNav;
    private PushRequestReceiver mPushRequestReceiver;

    ArrayList<SendPostAsyncTask> list = new ArrayList<SendPostAsyncTask>();

    int currentOption = 0;

    String[] ContactsIds;
    String[] ContactsUsernames;
    int count = 0;
    int target = 100;

    public ConnectionsHomeDynamic() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCredentials = ConnectionsHomeDynamicArgs.fromBundle(getArguments()).getCredentials();
        mJwToken = ConnectionsHomeDynamicArgs.fromBundle(getArguments()).getJwt();

        Log.e("ConnectionsHomeDynamic", "Received Email: " + mCredentials.getEmail());

        ProfileContent.myUsername = mCredentials.getUsername();
        ProfileContent.DeleteEndpointUri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_contacts_delete))
                .build();
        ProfileContent.AcceptEndpointUri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_contacts_accept))
                .build();

        ProfileContent.object = this;
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

        mBottomNav = view.findViewById(R.id.connections_nav_bar_base);
        mBottomNav.setOnNavigationItemSelectedListener(this::onNavigationSelected);

        FloatingActionButton fab = view.findViewById(R.id.connections_floatingActionButton);
        fab.setOnClickListener(this::fabOnClick);

        //manually navigate to connectionsHome

        NavController nc = Navigation.findNavController(getActivity(), R.id.hostFragment);
        nc.setGraph(R.navigation.connections_nav_dynamic, getArguments());

        LoadBaseConnections();
    }

    private boolean onNavigationSelected(final MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.connections_nav_bar_connections:
                        currentOption = 0;
                        LoadBaseConnections();
                        return true;
                case R.id.connections_nav_bar_sent:
                        currentOption = 1;
                        LoadSentConnectionRequests();
                        return true;
                case R.id.connections_nav_bar_received:
                        currentOption = 2;
                        LoadReceivedConnectionRequests();
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
                    Log.e("Send", result);
                }
            }
            mSendFriendRequestButton.setEnabled(true);
            mBottomNav.setSelectedItemId(R.id.connections_nav_bar_sent);
            //LoadSentConnectionRequests();
        } catch (JSONException e) {
            mAutoCompleteTextView.setError("JSONException Sending request");
            mSendFriendRequestButton.setEnabled(true);
        }
    }

    public void LoadBaseConnections()
    {
            Lock();
            count = 0;

            PROFILES.clear();
            NavController navController = Navigation.findNavController(getActivity(), R.id.hostFragment);
            ConnectionsNavDynamicDirections.ActionGlobalNavLanding connectionsHome =
                    ConnectionsNavDynamicDirections.actionGlobalNavLanding(mCredentials);
            navController.navigate(connectionsHome);

            Log.e("LoadConnections", "Start");
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_contacts))
                    .appendPath(getString(R.string.ep_contacts_base))
                    .build();

            JSONObject message = new JSONObject();

            try {
                message.put("username", mCredentials.getUsername());
            } catch (JSONException e) {
                Log.e("LoadConnection", "Error");
            }

            SendPostAsyncTask t = new SendPostAsyncTask.Builder(uri.toString(), message)
                    .onPostExecute(this::handleBaseOnPost)
                    .build();
            t.execute();
            list.add(t);
            Log.e("LoadConnections", "Stop");

    }

    private void handleBaseOnPost(String result)
    {
        try {
            Log.e("Base", result);
            JSONObject resultsJSON = new JSONObject(result);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                Log.e("ContactsID" , "Loaded");
                JSONArray Contacts = resultsJSON.getJSONArray("contacts");

                ContactsIds = new String[Contacts.length()];
                target = Contacts.length();

                for (int i = 0; i < Contacts.length(); i++) {
                    JSONObject userJSON = Contacts.getJSONObject(i);

                    String id = userJSON.getString("sender");
                    ContactsIds[i] = id;
                }
                LoadBaseUsernames();
            } else {
            }

        } catch (JSONException e) {
            Log.e("BaseError", e.toString());
        }
    }

    private void LoadBaseUsernames()
    {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_contacts_profile))
                .build();

        ContactsUsernames = new String[ContactsIds.length];
        target = ContactsIds.length;

        for (int i = 0; i < ContactsIds.length; i++) {
            Log.e("Test", "THIS SHOULD PRINT");
            JSONObject message = new JSONObject();

            try {
                message.put("id", ContactsIds[i]);
            } catch (JSONException e) {
                Log.e("LoadConnection", "Error");
            }
            SendPostAsyncTask t = new SendPostAsyncTask.Builder(uri.toString(), message)
                    .onPostExecute(this::GetBaseProfileFromIdPost)
                    .build();
            t.execute();
            list.add(t);
        }
    }

    private void GetBaseProfileFromIdPost(String result)
    {
        try {
            Log.e("Base", result);
            JSONObject resultsJSON = new JSONObject(result);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                JSONObject object = resultsJSON.getJSONObject("profile");
                String id = object.getString("memberid");
                String username = object.getString("username");
                String first = object.getString("firstname");
                String last =  object.getString("lastname");
                String name = first + " " + last;
                String email = object.getString("email");

                Log.e("User", username);
                ContactsUsernames[count++] = username;
                PROFILES.add(new ProfileContent.Profile(id, name, email, username));

                if(count == target-1)
                {

                }

            } else {

            }

        } catch (JSONException e) {
            Log.e("Profiles", e.toString());
        }

        NavController navController = Navigation.findNavController(getActivity(), R.id.hostFragment);
        ConnectionsNavDynamicDirections.ActionGlobalNavLanding connectionsHome =
                ConnectionsNavDynamicDirections.actionGlobalNavLanding(mCredentials);
        navController.navigate(connectionsHome);
    }

    public void LoadSentConnectionRequests()
    {
            Lock();

            count = 0;

            PROFILES.clear();
            NavController navController = Navigation.findNavController(getActivity(), R.id.hostFragment);
            ConnectionsNavDynamicDirections.ActionGlobalNavSent sent =
                    ConnectionsNavDynamicDirections.actionGlobalNavSent(mCredentials);
            navController.navigate(sent);

            Log.e("LoadSent", "Start");
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_contacts))
                    .appendPath(getString(R.string.ep_contacts_sent))
                    .build();

            JSONObject message = new JSONObject();

            try {
                message.put("username", mCredentials.getUsername());
            } catch (JSONException e) {
                Log.e("LoadSent", "Error");
            }

            SendPostAsyncTask t = new SendPostAsyncTask.Builder(uri.toString(), message)
                    .onPostExecute(this::handleSentOnPost)
                    .build();
            t.execute();
            list.add(t);
            Log.e("LoadSent", "Stop");
    }

    private void handleSentOnPost(String result)
    {
        try {
            Log.e("Sent", result);
            JSONObject resultsJSON = new JSONObject(result);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                Log.e("Sent" , "Loaded");
                JSONArray Contacts = resultsJSON.getJSONArray("sent");

                ContactsIds = new String[Contacts.length()];

                for (int i = 0; i < Contacts.length(); i++) {
                    JSONObject userJSON = Contacts.getJSONObject(i);

                    String id = userJSON.getString("receiver");
                    ContactsIds[i] = id;
                }
                LoadSentUsernames();
            } else {
            }

        } catch (JSONException e) {
            Log.e("Sent", e.toString());
        }
    }

    private void LoadSentUsernames()
    {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_contacts_profile))
                .build();

        ContactsUsernames = new String[ContactsIds.length];
        target = ContactsIds.length;

        for(int i = 0; i < ContactsIds.length; i++) {
            JSONObject message = new JSONObject();

            try {
                message.put("id", ContactsIds[i]);
            } catch (JSONException e) {
                Log.e("Sent", "Error");
            }

            SendPostAsyncTask t = new SendPostAsyncTask.Builder(uri.toString(), message)
                    .onPostExecute(this::GetSentProfileFromIdPost)
                    .build();
            t.execute();
            list.add(t);
        }
    }

    private void GetSentProfileFromIdPost(String result)
    {
        try {
            Log.e("SentResult", result);
            JSONObject resultsJSON = new JSONObject(result);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                JSONObject object = resultsJSON.getJSONObject("profile");
                String id = object.getString("memberid");
                String username = object.getString("username");
                String first = object.getString("firstname");
                String last =  object.getString("lastname");
                String name = first + " " + last;
                String email = object.getString("email");

                Log.e("User", username);
                ContactsUsernames[count++] = username;
                PROFILES.add(new ProfileContent.Profile(id, name, email, username));

                if(count == target-1)
                {
                }
            } else {
            }

        } catch (JSONException e) {
            Log.e("SentProfiles", e.toString());
        }

        NavController navController = Navigation.findNavController(getActivity(), R.id.hostFragment);
        ConnectionsNavDynamicDirections.ActionGlobalNavSent sent =
                ConnectionsNavDynamicDirections.actionGlobalNavSent(mCredentials);
        navController.navigate(sent);
    }

    public void LoadReceivedConnectionRequests()
    {
            Lock();

            count = 0;

            PROFILES.clear();
            NavController navController = Navigation.findNavController(getActivity(), R.id.hostFragment);
            ConnectionsNavDynamicDirections.ActionGlobalNavReceived received =
                    ConnectionsNavDynamicDirections.actionGlobalNavReceived(mCredentials);
            navController.navigate(received);

            Log.e("LoadReceived", "Start");
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_contacts))
                    .appendPath(getString(R.string.ep_contacts_received))
                    .build();

            JSONObject message = new JSONObject();

            try {
                message.put("username", mCredentials.getUsername());
            } catch (JSONException e) {
                Log.e("Receieved", "Error");
            }

            SendPostAsyncTask t = new SendPostAsyncTask.Builder(uri.toString(), message)
                    .onPostExecute(this::handleReceivedOnPost).build();
            t.execute();
            list.add(t);
            Log.e("LoadReceived", "Stop");

    }

    private void handleReceivedOnPost(String result)
    {
        try {
            Log.e("Received", result);
            JSONObject resultsJSON = new JSONObject(result);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                Log.e("ReceivedLoaded" , "Loaded");
                JSONArray Contacts = resultsJSON.getJSONArray("received");

                ContactsIds = new String[Contacts.length()];

                for (int i = 0; i < Contacts.length(); i++) {
                    JSONObject userJSON = Contacts.getJSONObject(i);

                    String id = userJSON.getString("sender");
                    ContactsIds[i] = id;
                }
                LoadReceivedUsernames();
            } else {

            }

        } catch (JSONException e) {
            Log.e("ReceivedError", e.toString());
        }
    }

    private void LoadReceivedUsernames()
    {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .appendPath(getString(R.string.ep_contacts_profile))
                .build();

        ContactsUsernames = new String[ContactsIds.length];
        target = ContactsIds.length;

        for(int i = 0; i < ContactsIds.length; i++) {
            JSONObject message = new JSONObject();

            try {
                message.put("id", ContactsIds[i]);
            } catch (JSONException e) {
                Log.e("LoadUsername error", "Error");
            }

            SendPostAsyncTask t = new SendPostAsyncTask.Builder(uri.toString(), message)
                    .onPostExecute(this::GetReceivedProfileFromIdPost)
                    .build();
                    t.execute();
                    list.add(t);
        }
    }

    private void GetReceivedProfileFromIdPost(String result)
    {
        try {
            Log.e("Received Result", result);
            JSONObject resultsJSON = new JSONObject(result);

            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                JSONObject object = resultsJSON.getJSONObject("profile");
                String id = object.getString("memberid");
                String username = object.getString("username");
                String first = object.getString("firstname");
                String last =  object.getString("lastname");
                String name = first + " " + last;
                String email = object.getString("email");

                Log.e("User", username);
                ContactsUsernames[count++] = username;
                PROFILES.add(new ProfileContent.Profile(id, name, email, username));
            } else {
            }

        } catch (JSONException e) {
            Log.e("Received", e.toString());
        }

        NavController navController = Navigation.findNavController(getActivity(), R.id.hostFragment);
        ConnectionsNavDynamicDirections.ActionGlobalNavReceived received =
                ConnectionsNavDynamicDirections.actionGlobalNavReceived(mCredentials);
        navController.navigate(received);
    }

    public void SendMessageNavigation(String u)
    {
        NavController navController =
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        MobileNavigationDirections.ActionGlobalNavChatlist chatPage =
                ChatListFragmentDirections.actionGlobalNavChatlist(mCredentials, mJwToken);
        navController.navigate(chatPage);
    }

    private class PushRequestReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("PUSHYFROMCONNECTIONS", "1");
            if(intent.hasExtra("TYPE")) {
                Log.e("PUSHYFROMCONNECTIONS", "2");
                if(intent.getStringExtra("TYPE").compareTo("request") == 0)
                {
                    Log.e("PUSHYFROMCONNECTIONS", "3");
                    Load();
                }
            }
        }
    }

    public void Load()
    {
        if(currentOption == 0)
        {
            LoadBaseConnections();
        }
        else if(currentOption == 1)
        {
            LoadSentConnectionRequests();
        }
        else if(currentOption == 2)
        {
            LoadReceivedConnectionRequests();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e("RESUME", "SET PUSH RECEIVER");

        if (mPushRequestReceiver == null) {
            mPushRequestReceiver = new PushRequestReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mPushRequestReceiver, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.e("PAUSE", "PAUSE PUSH RECEIVER");

        if (mPushRequestReceiver != null){
            getActivity().unregisterReceiver(mPushRequestReceiver);
        }
    }

    public void Lock()
    {
        for(SendPostAsyncTask t : list)
        {
            t.cancel(true);
        }

        list.clear();
    }

}
