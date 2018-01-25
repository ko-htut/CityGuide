package com.albertotorresgi.cityguide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.ImmutableList;

/**
 * Enum places
 */
public enum PlaceType implements Parcelable {

    BAR(ImmutableList.of("bar", "night_club")),
    BISTRO(ImmutableList.of("restaurant")),
    CAFE(ImmutableList.of("cafe"));

    public ImmutableList<String> matchingPlaceTypes;

    PlaceType(ImmutableList<String> matchingPlaceTypes) {
        this.matchingPlaceTypes = matchingPlaceTypes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Parcelable.Creator<PlaceType> CREATOR =
            new Parcelable.Creator<PlaceType>() {

        public PlaceType createFromParcel(Parcel in) {
            PlaceType placeType = PlaceType.values()[in.readInt()];
            return placeType;
        }

        public PlaceType[] newArray(int size) {
            return new PlaceType[size];
        }

    };
}
