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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeeklyFragment extends Fragment {

    private String TAG="WeeklyFragment";
    private JSONObject weatherInfo=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.weekly_tab, container, false);

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

    private void fillInfo(View rootView){

        if(weatherInfo !=null)
        {
            Log.d(TAG, "in fill info");

            String icon, summary;

            try {
                JSONObject obj = weatherInfo.getJSONObject("daily");
                icon= obj.getString("icon");
                summary= obj.getString("summary");

                ImageView iconImage= rootView.findViewById(R.id.icon);
                int iconResource = getIconDrawable(icon);
                iconImage.setImageResource(iconResource);

                TextView summ= rootView.findViewById(R.id.summary);
                summ.setText(summary);

                JSONArray data= obj.getJSONArray("data");
                ArrayList<Entry> tempLow= new ArrayList<>();
                ArrayList<Entry> tempHigh= new ArrayList<>();

                int n= data.length();
                for(int i=0;i<n;i++)
                {
                    JSONObject d= data.getJSONObject(i);
                    double th= d.getDouble("temperatureHigh");
                    double tl= d.getDouble("temperatureLow");

                    tempHigh.add(new Entry(i, (int)th));
                    tempLow.add(new Entry(i, (int) tl));
                }

                LineChart mChart = rootView.findViewById(R.id.chart);

                mChart.getAxisLeft().setDrawGridLines(false);  // y-axis
                mChart.getAxisRight().setDrawGridLines(false);  // y-axis
                mChart.getXAxis().setDrawGridLines(false);

                mChart.getAxisLeft().setTextColor(Color.WHITE); // y-axis
                mChart.getXAxis().setTextColor(Color.WHITE);
                mChart.getAxisRight().setTextColor(Color.WHITE);

                int color= Color.rgb(172, 55, 219);
                LineDataSet lineDataSet1= new LineDataSet(tempLow, "Minimum Temperature");
                lineDataSet1.setDrawValues(false);
                lineDataSet1.setCircleSize(4f);
                lineDataSet1.setColor(color);

                LineDataSet lineDataSet2= new LineDataSet(tempHigh, "Maximum Temperature");
                lineDataSet2.setDrawValues(false);
                lineDataSet2.setCircleSize(4f);
                lineDataSet2.setColor(getResources().getColor(android.R.color.holo_orange_dark));

                ArrayList<ILineDataSet> dataSets= new ArrayList<>();
                dataSets.add(lineDataSet1);
                dataSets.add(lineDataSet2);

                LineData lineData= new LineData(dataSets);
                mChart.setData(lineData);
                mChart.invalidate();

                Legend legend= mChart.getLegend();
                legend.setTextColor(Color.WHITE);
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setTextSize(16);

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
