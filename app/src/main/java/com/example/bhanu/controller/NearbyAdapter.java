package com.example.bhanu.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.model.Restaurant;
import com.model.RestaurantDatabase;

import java.util.ArrayList;
import java.util.List;

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.MyViewHolder> {
        private List<Restaurant> mDataset = new ArrayList<>();
        private Context mContext;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView nearby_name, nearby_vicinity ;
            public ImageView nearby_image;
            public RatingBar nearby_rating;
            public MyViewHolder(View itemView) {
                super(itemView);
                nearby_name = itemView.findViewById(R.id.nearby_name);
                nearby_vicinity = itemView.findViewById(R.id.nearby_vicinity);
                nearby_image = itemView.findViewById(R.id.nearby_image);
                nearby_rating = itemView.findViewById(R.id.place_rating);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public NearbyAdapter(List<Restaurant> myDataset, Context context) {
            mDataset = myDataset;
            mContext = context;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public NearbyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            View v =  LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nearby_item, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Restaurant model = mDataset.get(position);
            holder.nearby_vicinity.setText(model.getVicinity());
            holder.nearby_name.setText(model.getName());
            Glide.with(holder.nearby_image.getContext()).load(model.getIcon()).into(holder.nearby_image);

            holder.nearby_rating.setRating(model.getRating());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RestaurantDatabase restaurantDatabase = new RestaurantDatabase();
                    restaurantDatabase.AddtoDataBase(model);
                    Intent p = new Intent(mContext, restaurantActivity.class);
                    p.putExtra("placeId",model.getId());
                    mContext.startActivity(p);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
    }


}
