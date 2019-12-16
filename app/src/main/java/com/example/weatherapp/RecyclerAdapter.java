package com.example.weatherapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHolder> {

    private ArrayList<String> imagesUrls;
    private Context context;


    public RecyclerAdapter(Context context, ArrayList<String> imagesUrls)
    {
        this.context= context;
        this.imagesUrls= imagesUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_layout, viewGroup, false);
        ImageViewHolder imageViewHolder= new ImageViewHolder(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder viewHolder, int i) {
        //int image_id= images[i];
        //viewHolder.album.setImageResource(image_id);
        Picasso.with(context).load(imagesUrls.get(i)).fit().into(viewHolder.album);
    }

    @Override
    public int getItemCount() {
        return imagesUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        ImageView album;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            album= itemView.findViewById(R.id.album);
        }
    }
}
