package com.albertotorresgi.cityguide.adapters;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.albertotorresgi.cityguide.R;
import com.albertotorresgi.cityguide.models.Place;
import com.albertotorresgi.cityguide.models.PlaceResultSet;
import com.albertotorresgi.cityguide.utils.Constants;
import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to Render View objects that are either already on-screen
 */
public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private final Resources mResources;

    private PlaceResultSet mPlaceResultSet;

    public PlaceAdapter(Resources resources) {
        mResources = resources;
    }

    public void setPlaceResultSet(PlaceResultSet placeResultSet) {
        Preconditions.checkNotNull(placeResultSet);
        mPlaceResultSet = placeResultSet;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View placeView = inflater.inflate(R.layout.item_place, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(placeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Place place = mPlaceResultSet.placeList.get(i);
        viewHolder.item_name.setText(place.name);
        Picasso.with(viewHolder.item_icon.getContext()).load(place.getIcon())
                .placeholder(R.drawable.clear).into(viewHolder.item_icon);

        viewHolder.item_distance.setText(
                mResources.getString(R.string.distance, place.getDistance()));
        if (place.rating != null) {
            viewHolder.item_rating.setVisibility(View.VISIBLE);
            viewHolder.item_rating.setRating(place.rating);
        } else {
            viewHolder.item_rating.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
       return mPlaceResultSet != null ? mPlaceResultSet.placeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public @BindView(R.id.item_name) TextView item_name;
        public @BindView(R.id.item_distance) TextView item_distance;
        public @BindView(R.id.item_rating) RatingBar item_rating;
        public @BindView(R.id.item_icon) ImageView item_icon;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
