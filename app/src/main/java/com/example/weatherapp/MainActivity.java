package com.example.weatherapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    RequestQueue requestQueue;
    String ip_url = "http://ip-api.com/json";
    String weather_url = "http://weather-forecast-shakshi.us-west-2.elasticbeanstalk.com/weather/?lat=34.0322&long=-118.2836";

    String MyPREFERENCES = "WeatherApp2";
    SharedPreferences mSharedPreferences;
    String TAG = "MainActivityTag";
    String favKey = "fPlaces";

    String mCity, mRegion, mCountry;
    ArrayList<String> mCityImagesURLs;

    private ArrayList<String> favPlacesList;

    private Toolbar toolbar;
    private TabLayout mTabLayout;
    private ViewPager viewPager;
    private int n;

    private List<String> data;
    private String currentLocation = "";

    private MainActivityViewPagerAdapter mAdapter;
    private List<MainActivityFragment> fragmentList;
    private LinearLayout mProgressView;

    AutoCompleteAdapter autoCompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mCityImagesURLs = new ArrayList<>();
        data = new ArrayList<>();

        mProgressView = findViewById(R.id.progressbar_view);
        mProgressView.setVisibility(View.VISIBLE);

        favPlacesList= new ArrayList<>();

        mSharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(favKey, "");

        if(!json.isEmpty()){
            Type type= new TypeToken<ArrayList<String>>() {}.getType();
            favPlacesList= gson.fromJson(json, type);
        }

        Log.d(TAG, "FAV places "+ favPlacesList);
        n = 1 + favPlacesList.size();

        Log.d(TAG, "total fragments " + n);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        viewPager.setOffscreenPageLimit(10);
        fragmentList = new ArrayList<>();

        mAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(viewPager);

        requestQueue = Volley.newRequestQueue(MainActivity.this);

        Log.d(TAG, "got request queue" + requestQueue.toString());
        getCurrentLocation();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        MenuItem searchMenu = menu.findItem(R.id.app_bar_menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenu);

        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchAutoComplete.setBackgroundColor(Color.rgb(22,21,21));

        autoCompleteAdapter = new AutoCompleteAdapter(getApplicationContext(), data, R.layout.dropdown_item);
        searchAutoComplete.setAdapter(autoCompleteAdapter);

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString = (String) adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d(TAG, "Searched keyword is " + query);
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra("query", query);
                startActivityForResult(intent, 1);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, newText);
                if (newText.length() > 0) {
                    getSuggestions(newText);
                }
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                finish();
                startActivity(getIntent());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_menu_search:
                super.onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void removeFragment(MainActivityFragment frag) {
//        this.getSupportFragmentManager()
//                .beginTransaction().remove(frag).commit();
        fragmentList.remove(frag);
        //mTabLayout.removeTabAt(frag.position);
        //getSupportActionBar().removeTab(frag);
        mAdapter.notifyDataSetChanged();
    }

    public void removeFragment2(int position) {
        Log.d(TAG, "remove fragment "+ position);
        if(!fragmentList.isEmpty() && position<fragmentList.size())
        {
            fragmentList.remove(position);
            mAdapter.notifyDataSetChanged();
        }

    }


    private void getCurrentLocation() {

        String wurl = "http://weather-forecast-shakshi.us-west-2.elasticbeanstalk.com/weather/?lat=34.0322&long=-118.2836";

        Log.d(TAG, "asking curr location");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, wurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        requestQueue.add(stringRequest);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, ip_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());
                        try {
                            Double lat = response.getDouble("lat");
                            Double lng = response.getDouble("lon");
                            mCity = response.getString("city");
                            mRegion = response.getString("region");
                            mCountry = response.getString("countryCode");

                            currentLocation = mCity + ", " + mRegion + ", " + mCountry;
                            Log.d(TAG, currentLocation);
                            getCurrentWeather(currentLocation, lat.toString(), lng.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(getRequest);
    }

    private void getCurrentWeather(final String currLocation, String lat, String lng) {
        String weather_url = "http://weather-forecast-shakshi.us-west-2.elasticbeanstalk.com/weather/?lat=" + lat + "&long=" + lng;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, weather_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d(TAG, "Response" + response.toString());

                        JSONObject weatherInfo = response;

                        Bundle bundle = new Bundle();
                        bundle.putString("place", currLocation);
                        bundle.putInt("position", 0);
                        bundle.putString("weather", weatherInfo.toString());
                        //bundle.putString("images", mCityImagesURLs.toString());

                        //curr location fragment added
                        MainActivityFragment fragment = new MainActivityFragment();
                        fragment.setArguments(bundle);

                        fragmentList.add(fragment);
                        mAdapter.notifyDataSetChanged();

                        if (favPlacesList.size()==0 ) {
                            Log.d(TAG, "no favs");
                            mProgressView.setVisibility(View.GONE);
                            mTabLayout.setVisibility(View.VISIBLE);
                        } else {
                            Log.d(TAG, "FAVS");

                            Log.d(TAG, "Fav place:" + favPlacesList.get(0));
                            getLatLong(favPlacesList.get(0), 0);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(getRequest);
    }

    //to get weather for other places
    private void getWeather(final String place, String lat, String lng, final int position) {

        //String weather_url= "http://weather-forecast-shakshi.us-west-2.elasticbeanstalk.com/weather/?lat=34.0322&long=-118.2836";
        String weather_url = "http://weather-forecast-shakshi.us-west-2.elasticbeanstalk.com/weather/?lat=" + lat + "&long=" + lng;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, weather_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d(TAG, "Response" + response.toString());

                        JSONObject weatherInfo = response;

                        Bundle bundle = new Bundle();
                        bundle.putString("place", place);
                        bundle.putInt("position", position + 1);

                        bundle.putString("weather", weatherInfo.toString());
                        //bundle.putString("images", mCityImagesURLs.toString());

                        //curr location fragment added
                        MainActivityFragment fragment = new MainActivityFragment();
                        fragment.setArguments(bundle);

                        fragmentList.add(fragment);
                        mAdapter.notifyDataSetChanged();

                        if(position < (favPlacesList.size()-1))
                        {
                            Log.d(TAG, "fav"+ favPlacesList.get(position+1));
                            getLatLong(favPlacesList.get(position+1), position+1);
                        }

                        if(position == (n-2)) {
                            mProgressView.setVisibility(View.GONE);
                            mTabLayout.setVisibility(View.VISIBLE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(getRequest);
    }

    private void getCityImages(String city) {

        String url = "http://weather-forecast-shakshi.us-west-2.elasticbeanstalk.com/city/?city=" + city;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        try {

                            JSONArray items = response.getJSONArray("items");
                            mCityImagesURLs = new ArrayList<>();

                            int n = items.length();
                            for (int i = 0; i < n; i++) {
                                JSONObject obj = items.getJSONObject(i);
                                mCityImagesURLs.add(obj.getString("link"));
                            }

                            Log.d(TAG, mCityImagesURLs.toString());


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(getRequest);

    }


    private void getLatLong(final String query, final int index) {
        String weather_url = "http://weather-forecast-shakshi.us-west-2.elasticbeanstalk.com/location/?address=" + query;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, weather_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d(TAG, "Response" + response.toString());

                        try {
                            JSONArray results = response.getJSONArray("results");
                            JSONObject obj = results.getJSONObject(0);
                            JSONObject geometry = obj.getJSONObject("geometry");

                            JSONObject location = geometry.getJSONObject("location");
                            Double lat = location.getDouble("lat");
                            Double lng = location.getDouble("lng");
                            Log.d(TAG, "lat" + lat + " long: " + lng);

                            getWeather(query, lat.toString(), lng.toString(), index);

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(getRequest);
    }

    private void getSuggestions(String input) {
        String url = "http://weather-forecast-shakshi.us-west-2.elasticbeanstalk.com/suggestions/?term=" + input;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.d("Response", response.toString());

                        try {
                            JSONArray pred = response.getJSONArray("predictions");
                            ArrayList<String> preddata = new ArrayList<>();

                            int n = pred.length();
                            for (int i = 0; i < 5 && i < n; i++) {
                                JSONObject obj = pred.getJSONObject(i);
                                String description = obj.getString("description");
                                preddata.add(description);

                            }
                            autoCompleteAdapter.setData(preddata);
                            autoCompleteAdapter.notifyDataSetChanged();
                            Log.d(TAG, data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(getRequest);
    }
}
