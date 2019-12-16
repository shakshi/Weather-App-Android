package com.example.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

public class MainActivityFragment extends Fragment {

    public int position;
    private JSONObject weatherInfo;
    private String place="";
    private String TAG="MainActivityFragment";

    String MyPREFERENCES = "WeatherApp2";
    String favKey = "fPlaces";
    SharedPreferences sharedPreferences;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        MainActivityFragment mainActivityFragment = new MainActivityFragment();
        mainActivityFragment.setArguments(bundle);
        return mainActivityFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.main_activity_fragment, container, false);

        Bundle b= this.getArguments();
        if(b!= null) {

            try {
                weatherInfo= new JSONObject(b.getString("weather"));
                place = b.getString("place");
                position= b.getInt("position");

                Log.d(TAG, weatherInfo.toString());

                Log.d(TAG, place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("Fragment", "no bundle");
        }

        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        FloatingActionButton mapButton = rootView.findViewById(R.id.map_button);
        if(position==0){
            mapButton.hide();
        }

        final Intent intent = new Intent(getContext(), DetailsActivity.class);
        fillInfo(rootView);

        CardView card1= rootView.findViewById(R.id.card1);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call Details Activity and send all weather info in intent
                Intent intent = new Intent( getContext(), DetailsActivity.class);
                intent.putExtra("place", place);
                intent.putExtra("weather", weatherInfo.toString());

                startActivity(intent);
            }
        });

        final MainActivityFragment frag= this;
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //remove that fragment

                //also remove the place from shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = sharedPreferences.getString(favKey, "");

                ArrayList<String> places = new ArrayList<>();
                if(!json.isEmpty()){
                    Type type= new TypeToken<ArrayList<String>>() {}.getType();
                    places= gson.fromJson(json, type);
                }
                places.remove(place);

                String jsonnew= gson.toJson(places);
                editor.putString(favKey, jsonnew);
                editor.commit();

                Log.d("places", places.toString());
                Toast.makeText(getContext(),place + " was removed from favorites", Toast.LENGTH_SHORT).show();

                ((MainActivity)getActivity()).removeFragment2(position);
            }
        });

        return rootView;
    }

    private int getIconDrawable(String summary)
    {
        int icon= R.drawable.weather_sunny;
        Log.d(TAG, summary);

        if(summary.equals("clear-day")) {
            icon= R.drawable.weather_sunny;
        }
        else if(summary.equals("clear-night")){
            icon= R.drawable.weather_night;
        }
        else if(summary.equals("rain")){
            icon= R.drawable.weather_rainy;
        }
        else if(summary.equals("sleet")){
            icon= R.drawable.weather_snowy_rainy;
        }else if(summary.equals("snow")){
            icon= R.drawable.weather_snowy;
        }else if(summary.equals("wind")){
            icon= R.drawable.weather_windy_variant;
        }else if(summary.equals("fog")){
            icon= R.drawable.weather_fog;
        }
        else if(summary.equals("cloudy")){
            icon= R.drawable.weather_cloudy;
        }
        else if(summary.equals("partly-cloudy-night")){
            icon= R.drawable.weather_night_partly_cloudy;
        }
        else if(summary.equals("partly-cloudy-day")){
            icon= R.drawable.weather_partly_cloudy;
        }
        return icon;
    }

    public String getDate(long timestamp)
    {
        Calendar cal= Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp * 1000L);
        String date= DateFormat.format("MM/dd/yyyy", cal).toString();
        return date;
    }

    public void colorImage(ImageView imageView, int resource, int color)
    {
        Drawable unwrappedDrawable = ContextCompat.getDrawable(getContext(), resource);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        imageView.setImageDrawable(wrappedDrawable);
    }

    public double roundOff(double x)
    {
        //round off to 2 decimals
        double value= Math.round(x* 100.0) /100.0;
        return value;
    }

    private void fillInfo(View rootView){

        //create 8 layouts and add to the scroll view for min and max temp and so on

        if(weatherInfo !=null)
        {
            Log.d(TAG, "in fill info");
            TextView locationTextView= rootView.findViewById(R.id.location);
            locationTextView.setText(place);

            String icon, summary;
            double temperature;
            double humidity, pressure, visibility, windSpeed;

            try {
                JSONObject obj = weatherInfo.getJSONObject("currently");
                icon= obj.getString("icon");


                temperature= obj.getDouble("temperature");
                int temp= (int) temperature;

                summary= obj.getString("summary");

                if(summary.length() > 40){
                    summary= summary.substring(0,40) + "...";
                }

                humidity= obj.getDouble("humidity");
                humidity= humidity*100;
                humidity= roundOff(humidity);

                pressure= obj.getDouble("pressure");
                pressure= roundOff(pressure);

                visibility= obj.getDouble("visibility");
                visibility= roundOff(visibility);

                windSpeed= obj.getDouble("windSpeed");
                windSpeed= roundOff(windSpeed);

                //card1
                ImageView iconImage= rootView.findViewById(R.id.icon);
                int iconResource = getIconDrawable(icon);
                //iconImage.setImageResource(iconResource);

                if (icon.equals("clear-day")) {
                    //color icon yellow
                    colorImage(iconImage, iconResource, Color.rgb(255, 167, 39));
                } else {
                    //color image white;
                    colorImage(iconImage, iconResource, Color.WHITE);
                }

                TextView tempTextView= rootView.findViewById(R.id.temperature);
                tempTextView.setText(temp + "°F");

                TextView summ= rootView.findViewById(R.id.summary);
                summ.setText(summary);

                //card 2
                TextView t= rootView.findViewById(R.id.rain_value);
                t.setText(humidity + "%");

                TextView t2= rootView.findViewById(R.id.pressure_value);
                t2.setText(pressure + " mb");

                TextView t3= rootView.findViewById(R.id.wind_speed_value);
                t3.setText(windSpeed + " mph");

                TextView t4= rootView.findViewById(R.id.visibility_value);
                t4.setText(visibility+ " km");

                ImageView i1= rootView.findViewById(R.id.humidity_image);
                ImageView i2= rootView.findViewById(R.id.pressure_image);
                ImageView i3= rootView.findViewById(R.id.wind_image);
                ImageView i4= rootView.findViewById(R.id.visibility_image);

                int color= Color.rgb(172, 55, 219);
                colorImage(i1, R.drawable.water_percent, color);
                colorImage(i2, R.drawable.gauge, color);
                colorImage(i3, R.drawable.weather_windy, color);
                colorImage(i4, R.drawable.eye_outline, color);

                LinearLayout scrollViewLL= rootView.findViewById(R.id.scroll_view_linear_layout);
                obj = weatherInfo.getJSONObject("daily");
                JSONArray data= obj.getJSONArray("data");

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );

                int n= data.length();
                for(int i=0;i<n && i<8;i++)
                {
                    JSONObject d= data.getJSONObject(i);
                    Log.d(TAG, d.toString());

                    long timestamp= d.getInt("time");
                    String date= getDate(timestamp);
                    double th= d.getDouble("temperatureHigh");
                    double tl= d.getDouble("temperatureLow");
                    String table_icon= d.getString("icon");

                    String s1= date;
                    String s2= String.valueOf((int)tl);
                    String s3= String.valueOf((int)th);

                    LayoutInflater inflater= LayoutInflater.from(getContext());
                    View view= inflater.inflate(R.layout.scroll_item, null);
                    LinearLayout linearLayout1= (LinearLayout) view;

                    TextView tv1= linearLayout1.findViewById(R.id.date);
                    tv1.setText(s1);

                    TextView tv2= linearLayout1.findViewById(R.id.maxt);
                    tv2.setText(s2);
                    TextView tv3= linearLayout1.findViewById(R.id.mint);
                    tv3.setText(s3);

                    ImageView iv= linearLayout1.findViewById(R.id.table_icon);
                    int tableiconresource= getIconDrawable(table_icon);
                    if (table_icon.equals("clear-day")) {
                        //color icon yellow
                        colorImage(iv, tableiconresource, Color.rgb(255, 167, 39));
                    } else {
                        //color image white;
                        colorImage(iv, tableiconresource, Color.WHITE);
                    }
                    scrollViewLL.addView(linearLayout1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
