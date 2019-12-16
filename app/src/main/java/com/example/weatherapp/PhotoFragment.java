package com.example.weatherapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotoFragment extends Fragment {

    private String TAG="PhotoFragment";
    private JSONObject weatherInfo=null;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private ArrayList<String> imageURls;

    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.photos_tab, container, false);

        Bundle b= this.getArguments();
        if(b!= null) {
            imageURls = b.getStringArrayList("images");
            Log.d(TAG, imageURls.toString());
        }
        else{
            Log.d(TAG, "no bundle");
        }

        recyclerView= rootView.findViewById(R.id.recyclerView);
        layoutManager= new LinearLayoutManager(getContext());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerAdapter= new RecyclerAdapter(getContext(), imageURls);
        recyclerView.setAdapter(recyclerAdapter);

//        String imageUri = "https://i.imgur.com/tGbaZCY.jpg";
//        ImageView ivBasicImage = (ImageView) rootView.findViewById(R.id.image);
//        Picasso.with(getContext()).load(imageUri).into(ivBasicImage);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
