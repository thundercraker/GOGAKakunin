package com.example.yumashish.gogamarkethuddle.Connector.JSON;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by lightning on 12/16/15.
 */
public class RandomMarker implements LocationObject {
    Marker mMarker;

    public RandomMarker(Marker marker) {
        mMarker = marker;
    }

    @Override
    public double getLat() {
        return mMarker.getPosition().latitude;
    }

    @Override
    public double getLng() {
        return mMarker.getPosition().longitude;
    }
}
