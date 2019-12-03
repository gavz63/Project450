package edu.uw.tcss450.inouek.test450.Connections.Profile;


import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.uw.tcss450.inouek.test450.Connections.ConnectionsHomeDynamic;
import edu.uw.tcss450.inouek.test450.R;
import edu.uw.tcss450.inouek.test450.utils.SendPostAsyncTask;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ProfileContent {

    public static String myUsername;
    public static Uri DeleteEndpointUri;
    public static Uri AcceptEndpointUri;

    public static ConnectionsHomeDynamic object;

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Profile> PROFILES = new ArrayList<Profile>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Profile> PROFILES_MAP = new HashMap<String, Profile>();

    private static final int COUNT = 25;
    private static String[] names = null;
    private static String[] emails = null;
    private static String[] usernames = null;

    private static int option = 0;

    private static void LoadStrings()
    {
        names = new String[11];
        emails = new String[11];
        usernames = new String[11];

        Scanner sc = null;
        try {
           sc  = new Scanner(new File("/Temp/profiles.txt"));
        }
        catch(java.io.FileNotFoundException error)
        {
            String f = null;
            try {
                f = new File("profiles.txt").getCanonicalPath();
            }
            catch(java.io.IOException e)
            {
                Log.e("DONT KNOW WHY THIS HAPPENED","PRETTY PISSED");
            }
            if(f != null) {
                Log.e("FOUND", f);
            }
            Log.e("ERROR", "FILE NOT FOUND");
        }
        if(sc != null)
        {
            int i = 0;
            while(sc.hasNextLine())
            {
                names[i] = sc.nextLine();
                usernames[i] = sc.nextLine();
                emails[i] = sc.nextLine();
                i++;
            }
        }
    }

//    static {
//
//        if(names == null) {
//            LoadStrings();
//        }
//
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createProfiles(i));
//        }
//    }

    public static void AcceptRequest(String username)
    {
        option = -1;
        DeleteRequest(myUsername, username);

        JSONObject message = new JSONObject();

        try {
            message.put("sender", username);
            message.put("receiver",myUsername);
        }
        catch (JSONException e)
        {

        }

        new SendPostAsyncTask.Builder(AcceptEndpointUri.toString(), message)
                .onPostExecute(result -> {
                    try {
                        Log.e("DeleteConnectionRequest", result);
                        JSONObject resultsJSON = new JSONObject(result);

                        boolean success = resultsJSON.getBoolean("success");

                        if (success) {

                        } else {
                            Log.e("DeleteConnectionRequest", "Failure");
                        }

                    } catch (JSONException e) {
                    }
                    object.LoadReceivedConnectionRequests();
                })
                .build().execute();
    }

    public static void DeleteConnections(String username)
    {
        option = 0;
        DeleteRequest(myUsername, username);

    }

    public static void CancelRequest(String username)
    {
        option = 1;
        DeleteRequest(myUsername, username);

    }

    public static void DenyRequest(String username)
    {
        option = 2;
        DeleteRequest(username, myUsername);

    }

    public static void DeleteRequest(String u1, String u2)
    {
        JSONObject message = new JSONObject();

        try {
            message.put("sender", u1);
            message.put("receiver", u2);
        } catch (JSONException e) {

        }

        new SendPostAsyncTask.Builder(DeleteEndpointUri.toString(), message)
                .onPostExecute(result -> {
                    try {
                        Log.e("DeleteConnectionRequest", result);
                        JSONObject resultsJSON = new JSONObject(result);

                        boolean success = resultsJSON.getBoolean("success");

                        if (success) {

                        } else {
                            Log.e("DeleteConnectionRequest", "Failure");
                        }

                    } catch (JSONException e) {
                    }
                    if(option == 0)
                    {
                        object.LoadBaseConnections();
                    }
                    else if(option == 1)
                    {
                        object.LoadSentConnectionRequests();
                    }
                    else if(option == 2) {
                        object.LoadReceivedConnectionRequests();
                    }
                    else
                    {

                    }
                })
                .build().execute();
    }

    public static void clear()
    {
        PROFILES.clear();
        PROFILES_MAP.clear();
    }

    private static void addItem(Profile item) {
        PROFILES.add(item);
        // a bunch of keys for the auto search.
        PROFILES_MAP.put(item.name, item);
        PROFILES_MAP.put(item.id, item);
        PROFILES_MAP.put(item.email, item);
        PROFILES_MAP.put(item.username, item);

    }

    private static Profile createProfiles(int position) {
        String id = String.valueOf(position);

        return new Profile(String.valueOf(position), "Profile Name " + position, "Email Name " + position, "Username " + position);
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class Profile implements Serializable {
        public final String id; // maybe id should function like discord. If username is not unique, id will be.
        public final String name;
        public final String email;
        public final String username;

        public Profile(String id, String name, String email, String username) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.username = username;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
