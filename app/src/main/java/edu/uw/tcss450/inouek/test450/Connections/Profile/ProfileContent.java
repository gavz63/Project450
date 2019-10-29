package edu.uw.tcss450.inouek.test450.Connections.Profile;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample name for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ProfileContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Profile> PROFILES = new ArrayList<Profile>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Profile> PROFILES_MAP = new HashMap<String, Profile>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createProfiles(i));
        }
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
