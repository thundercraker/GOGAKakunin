package com.yumashish.kakunin.External;

import com.google.android.gms.maps.model.LatLng;
import com.yumashish.kakunin.Interfaces.LocationObject;

import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.Serializable;

/**
 * Created by lightning on 1/12/16.
 */
public abstract class LSObject implements LocationObject, Serializable {
    public String name;
    public double latitude;
    public double longitude;

    public abstract CellProcessor[] getCellProcessors();

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getLat() {
        return getLatitude();
    }

    @Override
    public double getLng() {
        return getLongitude();
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
