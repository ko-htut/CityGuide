package com.albertotorresgi.cityguide.models;

import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.Immutable;

/**
 * Places result
 */
@Immutable
public class PlaceResultSet {

    /**
     * Immutable list of Places
     */
    public @Nullable final ImmutableList<Place> placeList;


    /**
     * Place type
     */
    public final PlaceType placeType;

    public final boolean didRequestSucceed;

    public PlaceResultSet(
            ImmutableList<Place> placeList,
            PlaceType placeType,
            boolean didRequestSucceed) {
        this.placeList = placeList;
        this.placeType = placeType;
        this.didRequestSucceed = didRequestSucceed;
    }
}
