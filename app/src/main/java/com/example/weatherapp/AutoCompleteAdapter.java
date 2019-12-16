package com.example.weatherapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> {
    private List<String> mList;
    Context mContext;

    public AutoCompleteAdapter(Context context, List<String> list, int resource) {
        super(context, resource);
        mContext= context;
        mList = list;
    }

    public void setData(ArrayList<String> result) {
        mList.clear();
        if (result != null) {
            this.mList.addAll(result);
        }
    }

    @Override
    public String getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}