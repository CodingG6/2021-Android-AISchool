package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Extend this class to the RecyclerView.
public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModal> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModalArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);   // return this 'view' inside our ViewHolder.
                                       // view: the variable created with 'LayoutInflater' in line 35
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {

        // Add our data (TextView and ImageView) here.
        WeatherRVModal modal = weatherRVModalArrayList.get(position);
        holder.temperaturTV.setText(modal.getTemperature()+"°c");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionTV);
        holder.windTV.setText(modal.getWindSpeed()+"Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm"); // Specify the format in reference to the response.
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{    // change the date format here.
            Date t = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRVModalArrayList.size();


    }

    // Create an inner class ViewHolder here.
    // Make this inner class public because we have  to create a constructor.
    public class ViewHolder extends RecyclerView.ViewHolder{

        // Create 3 TextView and 1 ImageView variables here.
        private TextView windTV, temperaturTV, timeTV;
        private ImageView conditionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialise them here.
            windTV = itemView.findViewById(R.id.idTVWindspeed);
            temperaturTV = itemView.findViewById(R.id.idTVTemperature);
            timeTV = itemView.findViewById(R.id.idTVTime);
            conditionTV = itemView.findViewById(R.id.idTVCondition);
        }
    }
}
























