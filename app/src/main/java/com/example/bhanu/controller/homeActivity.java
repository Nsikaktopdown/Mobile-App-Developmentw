package com.example.bhanu.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.client.APIClient;
import com.client.APIInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.model.PlaceResponse;
import com.model.RestaurantDatabase;
import com.model.UserDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.places.Place.TYPE_FOOD;
import static com.google.android.gms.location.places.Place.TYPE_NIGHT_CLUB;
import static com.google.android.gms.location.places.Place.TYPE_RESTAURANT;

/**
 * Created by Bhanugoban Nadar on 1/19/2018.
 */

public class homeActivity extends AppCompatActivity  implements
        NavigationView.OnNavigationItemSelectedListener {

    private  APIInterface apiInterface;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    PlaceAutocompleteFragment autocompleteFragment;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    RecyclerView nearbyRecycler;
    NearbyAdapter mAdapter;
    ProgressDialog pd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.acc_navigation_view);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        Log.w("Home Activity", "main");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("NG")
                .build();

        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                int flag=0;
                final Place place2=place;
                for(int i=0;i<place.getPlaceTypes().size();i++)
                {
                    if(place.getPlaceTypes().get(i)==TYPE_FOOD||place.getPlaceTypes().get(i)==TYPE_RESTAURANT||place.getPlaceTypes().get(i)==TYPE_NIGHT_CLUB) {
                      final  RestaurantDatabase restaurantDatabase = new RestaurantDatabase();

                        flag=1;
                        restaurantDatabase.AddtoDataBase(place);
                        Intent p = new Intent(homeActivity.this, restaurantActivity.class);

                        p.putExtra("placeId",place.getId());
                        startActivity(p);
                        break;
                    }
                }
                if(flag==0)
                    Toast.makeText(homeActivity.this,"not a restaurant",Toast.LENGTH_SHORT).show();

                Log.w("inside auto", "Place: " + place.getName());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("hello nrp", "An error occurred: " + status);
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Loading Nearby Restaurants");
        pd.setCancelable(false);

        nearbyRecycler = findViewById(R.id.nearby_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        nearbyRecycler.setLayoutManager(linearLayoutManager);
        nearbyRecycler.setHasFixedSize(true);
        nearbyRecycler.setNestedScrollingEnabled(false);

        fetchNearByRestaurants();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return false;
        }
        Log.w("Home Activity", "outside");
        return super.onOptionsItemSelected(item);

    }

    private void fetchNearByRestaurants(){
        pd.show();
        Call<PlaceResponse> call = apiInterface.getNearbyRestaurant();
        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                if(response.body() != null){
                    mAdapter = new NearbyAdapter(response.body().getData(), getApplicationContext());
                    nearbyRecycler.setAdapter(mAdapter);
                    pd.hide();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't find any Restaurants around you", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                pd.hide();
                Toast.makeText(getApplicationContext(), t.getMessage().toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

            if (id == R.id.logout)//DO your stuff }
            {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(homeActivity.this, LoginActivity.class);
                startActivity(i);
                return true;
            }
            else if(id == R.id.menu_account)
            {
                Intent i = new Intent(homeActivity.this, profileActivity.class);
                startActivity(i);
                return true;
            }
            return true;

    }
    @Override
    protected void onStart() {
    super.onStart();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(homeActivity.this);
        UserDatabase userDatabase=new UserDatabase();
        if (acct != null) {
            userDatabase.addUserToDatabase(acct);
        }
    }


}

