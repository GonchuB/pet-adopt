package com.fiuba.tdp.petadopt.activities;

import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.AdvancedSearchFragment;
import com.fiuba.tdp.petadopt.fragments.MatchesFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.AddPetFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.map.ChooseLocationMapFragment;
import com.fiuba.tdp.petadopt.fragments.MyPetsFragment;
import com.fiuba.tdp.petadopt.fragments.SearchFragment;
import com.fiuba.tdp.petadopt.fragments.SettingsFragment;
import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.HttpClient;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String[] optionTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private PetsClient client;
    private int initialFragmentIndex = 0;
    private Fragment currentFragment;
    private String auth_token;
    private Boolean created = false;
    private Boolean exit = false;
    private Fragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpClient.ActivityContext = getBaseContext();
        User.currentContext = getApplicationContext();
        if (User.user().isLoggedIn()) {
            auth_token = User.user().getAuthToken();
            setupActivity();
        } else{
            promptLogin();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (User.user().isLoggedIn()){
            auth_token = User.user().getAuthToken();
            setupActivity();
        } else{
            finish();
        }

    }

    private void setupActivity() {
        if (!created) {
            mapFragment = new ChooseLocationMapFragment();

            DrawerItemClickListener listener = new DrawerItemClickListener();

            setContentView(R.layout.activity_main);

            optionTitles = getResources().getStringArray(R.array.drawer_option_array);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);

            // Set the adapter for the list view
            mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, optionTitles));

            // Set the list's click listener
            mDrawerList.setOnItemClickListener(listener);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    invalidateOptionsMenu();
                }
            };
            listener.displayView(initialFragmentIndex);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

            this.fetchPets();
            created = true;
        }
    }

    private void promptLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (item.getItemId() == R.id.advance_search_action){
            return displayFragment(new AdvancedSearchFragment());
        }

        if (item.getItemId() == R.id.simple_search_action){
            return displayFragment(new SearchFragment());
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (created) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position,long id) {

            // String text= "menu click... should be implemented";
            // Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            displayView(position);
        }

        private void displayView(int position) {
            // update the main content by replacing fragments
            // TODO: find way to avoid switch
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = mapFragment;
                    break;
                case 1:
                    fragment = new SearchFragment();
                    break;
                case 2:
                    fragment = new AddPetFragment();
                    break;
                case 3:
                    fragment = new MyPetsFragment();
                    break;
                case 4:
                    fragment = new SettingsFragment();
                    break;
                case 5:
                    goBackToLogin();
                    break;
                default:
                    break;
            }

            if (fragment != null) {
                displayFragment(fragment);

                // Highlight the selected item, update the title, and close the drawer
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
                setTitle(optionTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                // error in creating fragment
                Log.e("MainActivity", "Error in creating fragment");
            }
        }
    }

    private boolean displayFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();

        this.currentFragment = fragment;

        return true;
    }

    private void fetchPets() {
        client = PetsClient.instance();
        client.setAuth_token(auth_token);
        client.getPets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONArray body) {
                String items = "";
                try {
                    items = body.toString();
                    Log.v("JSON", items);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void goBackToLogin() {
        User.user().logout();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void showAddPetFragment(View view) {
        Fragment fragment = new AddPetFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
        setTitle(R.string.new_pet_title);
        mDrawerLayout.closeDrawer(mDrawerList);
    }


}