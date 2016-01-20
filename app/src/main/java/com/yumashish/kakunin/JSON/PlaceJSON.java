package com.yumashish.kakunin.JSON;

import android.graphics.Bitmap;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.Key;
import com.yumashish.kakunin.Interfaces.LocationObject;

import java.io.Serializable;

/**
 * Created by yumashish on 10/30/15.
 */
public class PlaceJSON implements LocationObject, Serializable {
    @Key
    public String id;

    @Key
    public String place_id;

    @Key
    public String name;

    @Key
    public String reference;

    @Key
    public String icon;

    @Key
    public String vicinity;

    @Key
    public Geometry geometry;

    @Key
    public String formatted_address;

    @Key
    public String formatted_phone_number;

    @Override
    public String toString() {
        return name + " - " + id + " - " + reference;
    }

    @Override
    public double getLat() {
        return geometry.location.lat;
    }

    @Override
    public double getLng() {
        return geometry.location.lng;
    }

    public LatLng getLatLng() {
        return new LatLng(getLat(), getLng());
    }

    public static class Geometry implements Serializable
    {
        @Key
        public Location location;
    }

    public static class Location implements Serializable
    {
        @Key
        public double lat;

        @Key
        public double lng;

        public LatLng getLatLng() {
            return new LatLng(lat,lng);
        }
    }

    //members for internal use
    public Place place;

    public Bitmap image;

    public String detailText;

    public int rating = -1;
}
