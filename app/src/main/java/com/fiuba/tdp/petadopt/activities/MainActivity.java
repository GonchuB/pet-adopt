package com.fiuba.tdp.petadopt.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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

import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.MatchesFragment;
import com.fiuba.tdp.petadopt.fragments.MyPetsFragment;
import com.fiuba.tdp.petadopt.fragments.SearchFragment;
import com.fiuba.tdp.petadopt.fragments.SettingsFragment;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import com.fiuba.tdp.petadopt.service.PetsClient;

public class MainActivity extends AppCompatActivity {
    private String[] optionTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private PetsClient client;
    private int initialFragmentIndex = 0;
    private String auth_token;

    private void fetchPets() {
        client = new PetsClient(auth_token);
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                auth_token= null;
            } else {
                auth_token= extras.getString(LoginActivity.AUTH_TOKEN);
            }
        } else {
            auth_token = (String) savedInstanceState.getSerializable(LoginActivity.AUTH_TOKEN);
        }

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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
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
                    fragment = new MatchesFragment();
                    break;
                case 1:
                    fragment = new SearchFragment();
                    break;
                case 2:
                    fragment = new MyPetsFragment();
                    break;
                case 3:
                    fragment = new SettingsFragment();
                    break;
                default:
                    break;
            }

            if (fragment != null) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment).commit();

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
}