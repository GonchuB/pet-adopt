package com.fiuba.tdp.petadopt.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fiuba.tdp.petadopt.R;
import com.fiuba.tdp.petadopt.fragments.HomeFragment;
import com.fiuba.tdp.petadopt.fragments.MyPetsFragment;
import com.fiuba.tdp.petadopt.fragments.MyRequestedPetsFragment;
import com.fiuba.tdp.petadopt.fragments.PetResultFragment;
import com.fiuba.tdp.petadopt.fragments.ProfileFragment;
import com.fiuba.tdp.petadopt.fragments.detail.PetDetailFragment;
import com.fiuba.tdp.petadopt.fragments.search.AdvanceSearchResultsDelegate;
import com.fiuba.tdp.petadopt.fragments.search.AdvancedSearchFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.AddPetFragment;
import com.fiuba.tdp.petadopt.fragments.addPet.map.ChooseLocationMapFragment;
import com.fiuba.tdp.petadopt.fragments.search.AdvanceSearchResultsDelegate;
import com.fiuba.tdp.petadopt.fragments.search.AdvancedSearchFragment;
import com.fiuba.tdp.petadopt.model.Pet;
import com.fiuba.tdp.petadopt.model.User;
import com.fiuba.tdp.petadopt.service.HttpClient;
import com.fiuba.tdp.petadopt.service.PetsClient;
import com.fiuba.tdp.petadopt.service.RegistrationIntentService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
    private Boolean exit = false;

    private Boolean shouldShowReportButton = false;
    private Fragment mapFragment;
    private HomeFragment homeFragment;
    private SearchView mSearchView;
    private Boolean atHome = true;
    private ProgressDialog progress;
    private Integer currentPosition;
    private String currentTitle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        debugGetSavedEndpoint();
        FacebookSdk.sdkInitialize(getApplicationContext());
        printFacebookKeyHash();

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        HttpClient.ActivityContext = getBaseContext();
        User.currentContext = getApplicationContext();
        if (User.user().isLoggedIn()) {
            auth_token = User.user().getAuthToken();
            setupActivity();
        } else {
            promptLogin();
        }

    }


    public void setShouldShowReportButton(Boolean shouldShowReportButton) {
        this.shouldShowReportButton = shouldShowReportButton;
    }


    private void printFacebookKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.fiuba.tdp.petadopt",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (User.user().isLoggedIn()) {
            auth_token = User.user().getAuthToken();
            setupActivity();
        } else {
            finish();
        }

    }

    private void setupActivity() {
        if (!created) {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle(R.string.loading);
            mapFragment = new ChooseLocationMapFragment();
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

            Intent i = getIntent();
            String type = i.getStringExtra("type");
            if (type == null) {
                fetchPets();
            } else {
                String petId = i.getStringExtra("pet_id");
                String userId = i.getStringExtra("user_id");
                startFromNotification(type, petId, userId);
            }
            created = true;
        }
    }

    private void startFromNotification(String type, String petId, String userId) {
        client = PetsClient.instance();
        client.setAuth_token(auth_token);
        client.getPet(petId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Pet pet = new Pet();
                try {
                    pet.loadFromJSON(response);
                    PetDetailFragment petDetail = new PetDetailFragment();
                    petDetail.setPet(pet);
                    displayFragment(petDetail, 0);
                    setTitle(pet.getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
        MenuItem reportItem = menu.findItem(R.id.report_action);
        if (shouldShowReportButton) {
            reportItem.setVisible(true);
        } else {
            reportItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (item.getItemId() == R.id.advance_search_action) {
            goToAdvancedSearch();
            return true;
        }
        if (item.getItemId() == R.id.report_action) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sure_report)
                    .setMessage(R.string.sure_report_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
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

    public void goBackToHome() {
        fetchPets();
        displayFragment(homeFragment, 0);
        setTitle(optionTitles[0]);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            // String text= "menu click... should be implemented";
            // Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            displayView(position);
        }

        private void displayView(int position) {
            // update the main content by replacing fragments
            Fragment fragment = null;
            currentPosition = position;
            switch (position) {
                case 0:
                    fetchPets();
                    fragment = homeFragment;
                    break;
                case 1:
                    fragment = new AddPetFragment();
                    break;
                case 2:
                    MyPetsFragment myPetsFragment = new MyPetsFragment();
                    goToMyPetsView(myPetsFragment, position);
                    fragment = myPetsFragment;
                    break;
                case 3:
                    MyRequestedPetsFragment myRequestedPetsFragment = new MyRequestedPetsFragment();
                    goToMyRequestedPetsView(myRequestedPetsFragment, position);
                    fragment = myRequestedPetsFragment;
                    break;
                case 4:
                    fragment = new ProfileFragment();
                    break;
                case 5:
                    goBackToLogin();
                    break;
                default:
                    break;
            }

            if (fragment != null && !fragment.isAdded()) {
                displayFragment(fragment, position);
            } else {
                // error in creating fragment
                Log.e("MainActivity", "Error in creating fragment or fragment was already added");
            }
        }
    }

    private void displayFragment(Fragment fragment, Integer position) {
        atHome = fragment instanceof HomeFragment;
        updateReportButtonVisibility(fragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
        this.currentFragment = fragment;
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        currentTitle = optionTitles[position];
        setTitle(currentTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void updateReportButtonVisibility(Fragment fragment) {
        shouldShowReportButton = fragment instanceof PetDetailFragment;
        invalidateOptionsMenu();
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


    private void goToMyPetsView(final MyPetsFragment fragment, int position) {
        client = PetsClient.instance();
        client.setAuth_token(auth_token);
        progress.show();
        client.getMyPets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONArray body) {
                progress.dismiss();
                fragment.setResults(body);
                fragment.onStart();
            }
        });
    }


    private void goToMyRequestedPetsView(final MyRequestedPetsFragment fragment, int position) {
        client = PetsClient.instance();
        client.setAuth_token(auth_token);
        progress.show();
        client.getMyRequestedPets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONArray body) {
                progress.dismiss();
                fragment.setResults(body);
                fragment.onStart();
            }
        });
    }

    private void performSearch(final String query) {
        client = PetsClient.instance();
        client.setAuth_token(auth_token);
        progress.show();
        client.simpleQueryPets(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int code, Header[] headers, JSONArray body) {
                progress.dismiss();
                showResults(body);
            }
        });
    }

    private void goToAdvancedSearch() {
        setTitle(R.string.advance_search_title);
        AdvancedSearchFragment fragment = new AdvancedSearchFragment();
        fragment.setAdvancedSearchResultsDelegate(new AdvanceSearchResultsDelegate() {
            @Override
            public void resultsAvailable(JSONArray body) {
                showResults(body);
            }
        });
        displayFragment(fragment, currentPosition);
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

    public void showResults(JSONArray body) {
        PetResultFragment fragment = new PetResultFragment();
        fragment.setResults(body);
        displayFragment(fragment, currentPosition);
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
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() != 0) {
                if (fragmentManager.getFragments().size() > 1) {
                    updateReportButtonVisibility(fragmentManager.getFragments().get(fragmentManager.getFragments().size() - 2));
                }
                fragmentManager.popBackStackImmediate();
                if (fragmentManager.getBackStackEntryCount() == 0 || fragmentManager.getBackStackEntryCount() == 1) {
                    setTitle(currentTitle);
                }
            } else {
                if (!atHome) {
                    goBackToHome();
                } else {
                    super.onBackPressed();
                    setTitle(currentTitle);
                }
            }
        }
    }

    // DEBUG PURPOSES
    private void debugGetSavedEndpoint() {
        SharedPreferences endpointData = getSharedPreferences("endpoint", Context.MODE_PRIVATE);
        String url = endpointData.getString("url", "");
        if (!url.isEmpty()) {
            HttpClient.base_url = url;
        } else {
            HttpClient.base_url = null;
        }

    }

}