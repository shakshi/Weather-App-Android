package com.example.weatherapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private JSONObject mWeather=null;
    private String place="";
    private String TAG="DetailActivityTag";
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private LinearLayout mProgressView;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mProgressView = findViewById(R.id.progressbar_view);
        mProgressView.setVisibility(View.VISIBLE);

        TextView topView = findViewById(R.id.place_searched);
        requestQueue = Volley.newRequestQueue(DetailsActivity.this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Intent intent= getIntent();
        try {
            mWeather= new JSONObject(intent.getStringExtra("weather"));
            place= intent.getStringExtra("place");
            Log.d(TAG, mWeather.toString());
            Log.d(TAG, place);
            topView.setText(place);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        if(mWeather!=null) {
            bundle.putString("weather", mWeather.toString());
        }

        TodayFragment todayFragment= new TodayFragment();
        todayFragment.setArguments(bundle);
        adapter.addFragment(todayFragment, "Today");

        WeeklyFragment weeklyFragment= new WeeklyFragment();
        weeklyFragment.setArguments(bundle);
        adapter.addFragment(weeklyFragment, "Weekly");

        String[] array = place.split(",");
        String city= array[0];
        getCityImages(place);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.twitter:
                //call a tweet intent
                String url = "http://www.twitter.com/intent/tweet?text="+ "Check Out "+ place + "'s Weather!" +
                        "It is " + "59" + "°F!" + "&hashtags=CSCI571WeatherSearch";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;

            case android.R.id.home:
                this.finish();
                return true;

                default:
                return super.onOptionsItemSelected(item);
        }
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
                            ArrayList<String> cityImagesURLs = new ArrayList<>();

                            int n = items.length();
                            for (int i = 0; i < n; i++) {
                                JSONObject obj = items.getJSONObject(i);
                                cityImagesURLs.add(obj.getString("link"));
                            }

                            Log.d(TAG, cityImagesURLs.toString());

                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("images",cityImagesURLs);

                            PhotoFragment photoFragment= new PhotoFragment();
                            photoFragment.setArguments(bundle);
                            adapter.addFragment(photoFragment, "Photos");

                            viewPager.setAdapter(adapter);

                            tabLayout = (TabLayout) findViewById(R.id.tabs);
                            tabLayout.setupWithViewPager(viewPager);

                            tabLayout.getTabAt(0).setIcon(R.drawable.calendar_today);
                            tabLayout.getTabAt(1).setIcon(R.drawable.trending_up);
                            tabLayout.getTabAt(2).setIcon(R.drawable.google_photos);

                            ColorStateList colors;
                            if (Build.VERSION.SDK_INT >= 23) {
                                colors = getResources().getColorStateList(R.color.tab_icon, getTheme());
                            }
                            else {
                                colors = getResources().getColorStateList(R.color.tab_icon);
                            }

                            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                TabLayout.Tab tab = tabLayout.getTabAt(i);
                                Drawable icon = tab.getIcon();

                                if (icon != null) {
                                    icon = DrawableCompat.wrap(icon);
                                    DrawableCompat.setTintList(icon, colors);
                                }
                            }

                            mProgressView.setVisibility(View.GONE);
                            //hide Progress bar

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
