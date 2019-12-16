package com.example.weatherapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class TodayFragment extends Fragment {

    private String TAG="TodayFragmentTag";
    private JSONObject weatherInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.today_tab, container, false);
        Bundle b= this.getArguments();
        if(b!= null) {
            try {
                weatherInfo= new JSONObject(b.getString("weather"));
                Log.d(TAG, weatherInfo.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d(TAG, "no bundle");
        }

        fillInfo(rootView);
        return rootView;
    }


    public void colorImage(ImageView imageView, int resource, int color)
    {
        Drawable unwrappedDrawable = ContextCompat.getDrawable(getContext(), resource);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        imageView.setImageDrawable(wrappedDrawable);
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

            String icon, summary;
            double temperature;
            double humidity, pressure, visibility, windSpeed;
            double ozone, cloudCover, precipitation;

            try {
                JSONObject obj = weatherInfo.getJSONObject("currently");
                icon= obj.getString("icon");
                temperature= obj.getDouble("temperature");
                int temp= (int) temperature;

                summary= obj.getString("summary");

                precipitation = obj.getDouble("precipIntensity");
                precipitation= roundOff(precipitation);

                humidity= obj.getDouble("humidity");
                humidity= humidity*100;
                humidity= roundOff(humidity);

                pressure= obj.getDouble("pressure");
                pressure= roundOff(pressure);

                visibility= obj.getDouble("visibility");
                visibility= roundOff(visibility);

                windSpeed= obj.getDouble("windSpeed");
                windSpeed= roundOff(windSpeed);

                ozone= obj.getDouble("ozone");
                ozone= roundOff(ozone);

                cloudCover= obj.getDouble("cloudCover");
                cloudCover= cloudCover*100;
                cloudCover= roundOff(cloudCover);

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

                TextView tempTextView= rootView.findViewById(R.id.temperature_value);
                tempTextView.setText(temp + "°F");

                TextView summ= rootView.findViewById(R.id.summary);
                summ.setText(summary);

                TextView t= rootView.findViewById(R.id.precipitation_value);
                t.setText(precipitation + " mmph");

                TextView t2= rootView.findViewById(R.id.pressure_value);
                t2.setText(pressure + " mb");

                TextView t3= rootView.findViewById(R.id.wind_speed_value);
                t3.setText(windSpeed + " mph");

                TextView t4= rootView.findViewById(R.id.visibility_value);
                t4.setText(visibility+ " km");

                TextView t5= rootView.findViewById(R.id.ozone_value);
                t5.setText(ozone + " DU");

                TextView t6= rootView.findViewById(R.id.cloudcover_value);
                t6.setText(cloudCover+ "%");

                TextView t7= rootView.findViewById(R.id.humidity_value);
                t7.setText(humidity+ "%");

                ImageView i1= rootView.findViewById(R.id.wind_image);
                ImageView i2= rootView.findViewById(R.id.pressure_image);
                ImageView i3= rootView.findViewById(R.id.precipitation_image);
                ImageView i4= rootView.findViewById(R.id.temperature_image);
                ImageView i5= rootView.findViewById(R.id.humidity_image);
                ImageView i6= rootView.findViewById(R.id.visibility_image);
                ImageView i7= rootView.findViewById(R.id.cloudcover_image);
                ImageView i8= rootView.findViewById(R.id.ozone_image);

                int color= Color.rgb(172, 55, 219);
                colorImage(i1, R.drawable.weather_windy, color);
                colorImage(i2, R.drawable.gauge, color);
                colorImage(i3, R.drawable.weather_pouring, color);

                colorImage(i4, R.drawable.thermometer, color);
                colorImage(i5, R.drawable.water_percent, color);

                colorImage(i6, R.drawable.eye_outline, color);
                colorImage(i7, R.drawable.weather_fog, color);
                colorImage(i8, R.drawable.earth, color);

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
