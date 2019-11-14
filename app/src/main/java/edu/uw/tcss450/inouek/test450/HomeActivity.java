package edu.uw.tcss450.inouek.test450;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.uw.tcss450.inouek.test450.Connections.ConnectionsHomeDynamicDirections;
import edu.uw.tcss450.inouek.test450.Connections.chat.ChatListFragmentDirections;
import edu.uw.tcss450.inouek.test450.model.Credentials;

public class HomeActivity extends AppCompatActivity {

    public static final int MONKEY_YELLOW = 1;
    public static final int MONKEY_GREEN = 2;
    public static final int MONKEY_RED = 3;
    public static final int MONKEY_PINK = 4;
    public static final int MONKEY_BLUE = 5;



    private SwitchMaterial mNightModeSwitch;
    private AppBarConfiguration mAppBarConfiguration;
    private Credentials mCredentials;
    private String mJwToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_night_mode_switch);
        View actionView = menuItem.getActionView();
        mNightModeSwitch = actionView.findViewById(R.id.night_mode_switch);
        mNightModeSwitch.setOnCheckedChangeListener((mNightModeSwitch, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
            }

        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_connections, R.id.nav_chat, R.id.nav_weather, R.id.nav_account)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.setGraph(R.navigation.mobile_navigation, getIntent().getExtras());
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this::onNavigationSelected);

        HomeActivityArgs args = HomeActivityArgs.fromBundle(getIntent().getExtras());
        mJwToken = args.getJwt();
        mCredentials = args.getCredentials();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private boolean onNavigationSelected(final MenuItem menuItem) {
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment);
        switch (menuItem.getItemId()) {
            case R.id.nav_account:
                MobileNavigationDirections.ActionGlobalNavAccount userPage =
                        UserFragmentDirections.actionGlobalNavAccount(mCredentials);
                navController.navigate(userPage);
                break;
            case R.id.nav_chatlist:
                MobileNavigationDirections.ActionGlobalNavChatlist chatPage =
                        ChatListFragmentDirections.actionGlobalNavChatlist(mCredentials);
                navController.navigate(chatPage);
                break;
            //TODO MAKE WEATHER AND CONNECTION ACTIVITIES INTO FRAGMENTS AND Navigate to them here
                //TODO PRobably pss the credentials (for friends and saved lcoations)
            case R.id.nav_weather:
                navController.navigate(R.id.action_nav_home_to_nav_weather);
                break;
            case R.id.nav_connections:
                MobileNavigationDirections.ActionGlobalNavConnections connectionsPage =
                        ConnectionsHomeDynamicDirections.actionGlobalNavConnections(mCredentials);
                navController.navigate(connectionsPage);
                break;
        }
        //Close the drawer
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
        return true;
    }

    void setCredentials(Credentials c) {
        mCredentials = c;
    }
}
