package com.yumashish.kakunin.JSON;

import com.google.android.gms.maps.model.LatLng;
import com.yumashish.kakunin.Interfaces.LocationObject;

/**
 * Created by lightning on 1/14/16.
 */
public class LoadLocationObject<T> implements LocationObject {
    T load;
    LatLng latLng;

    public LoadLocationObject(T load, LatLng position) {
        this.load = load;
        this.latLng = position;
    }

    public T getLoad() { return load; }

    @Override
    public double getLat() {
        return 0;
    }

    @Override
    public double getLng() {
        return 0;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
