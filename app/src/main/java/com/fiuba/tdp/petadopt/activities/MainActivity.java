package com.fiuba.tdp.petadopt.activities;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
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
import android.support.v7.widget.SearchView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.HomeFragment;
import com.fiuba.tdp.petadopt.fragments.search.AdvanceSearchResultsDelegate;
import com.fiuba.tdp.petadopt.fragments.search.AdvancedSearchFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.AddPetFragment;
import com.fiuba.tdp.petadopt.fragments.MyPetsFragment;
import com.fiuba.tdp.petadopt.fragments.ResultFragment;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, SearchView.OnQueryTextListener {
    private String[] optionTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private PetsClient client;
    private int initialFragmentIndex = 0;
    private Fragment currentFragment;
    private String auth_token;
    private Boolean created = false;
    private HomeFragment homeFragment;
    private SearchView mSearchView;
    private Boolean atHome = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
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
            homeFragment = new HomeFragment();

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
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        if (mSearchView != null) {
        mSearchView.setQueryHint(getResources().getString(R.string.search_hint));
        mSearchView.setOnQueryTextListener(this);
//        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (item.getItemId() == R.id.advance_search_action){
            setTitle(R.string.advance_search_title);
            AdvancedSearchFragment fragment = new AdvancedSearchFragment();
            fragment.setAdvancedSearchResultsDelegate(new AdvanceSearchResultsDelegate() {
                @Override
                public void resultsAvailable(JSONArray body) {
                    showResults(body);
                }
            });
            return displayFragment(fragment);
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
                    goBackToHome();
                    fragment = homeFragment;
                    break;
                case 1:
                    fragment = new AddPetFragment();
                    break;
                case 2:
                    fragment = new MyPetsFragment();
                    break;
                case 3:
                    fragment = new SettingsFragment();
                    break;
                case 4:
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
        if (fragment instanceof HomeFragment) {
            atHome = true;
        } else {
            atHome = false;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();

        this.currentFragment = fragment;

        return true;
    }

    private void fetchPets() {
        client = PetsClient.instance();
        client.setAuth_token(auth_token);
        client.getPetsForHome(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONArray body) {
                homeFragment.setResults(body);
                homeFragment.onStart();
            }
        });
    }

    private void performSearch(final String query) {
        client = PetsClient.instance();
        client.setAuth_token(auth_token);
        client.simpleQueryPets(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONArray body) {
                showResults(body);
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

    public void goBackToHome() {
        fetchPets();
        displayFragment(homeFragment);
        mDrawerList.setItemChecked(0, true);
        mDrawerList.setSelection(0);
        setTitle(optionTitles[0]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void showResults(JSONArray body) {
        ResultFragment fragment = new ResultFragment();
        fragment.setResults(body);
        displayFragment(fragment);
        setTitle(R.string.results_title);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String text) {
        performSearch(text);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
        } else if (!atHome) {
            goBackToHome();
        } else {
            super.onBackPressed();
        }
    }


}