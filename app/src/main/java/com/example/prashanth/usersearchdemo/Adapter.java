package com.example.prashanth.usersearchdemo;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

    public static String TAG = "CityAdapter";
    private Context context;

    private ArrayList<Pojo> cityModels = new ArrayList<Pojo>();
    private Adapter.AdapterCallback callback;

    public Adapter(ArrayList<Pojo> buildings, Context context, Adapter.AdapterCallback callback){
        this.context = context;
        this.callback = callback;
        cityModels = buildings;
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.listitem_layout, parent, false);
        Adapter.ViewHolder viewHolder = new Adapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final Adapter.ViewHolder holder, final int position) {

        Pojo pojo =  cityModels.get(position);

        holder.textViewName.setText(pojo.getName());
    }

    @Override
    public int getItemCount() {
        return cityModels.size();
    }

    interface AdapterCallback {
        void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textViewName;
        public ImageView iconLoc;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewName = (TextView) itemView.findViewById(R.id.nameText);
        }

        @Override
        public void onClick(View v) {
            if(NO_POSITION != getAdapterPosition()){
                callback.onClick(getAdapterPosition());
            }
        }
    }
}
