package com.yumashish.kakunin.JSON;

import com.google.android.gms.location.places.Place;
import com.yumashish.kakunin.Interfaces.LocationObject;

/**
 * Created by lightning on 12/16/15.
 */
public class PlaceWrapper implements LocationObject {
    Place place;

    public PlaceWrapper(Place place) {
        this.place = place;
    }

    public Place getPlace() {
        return place;
    }

    public PlaceJSON convertToPlaceJSON() {
        PlaceJSON placeJSON = new PlaceJSON();
        placeJSON.name = place.getName().toString();
        placeJSON.place = place;
        placeJSON.place_id = place.getId();
        placeJSON.id = place.getId();
        placeJSON.geometry = new PlaceJSON.Geometry();
        placeJSON.geometry.location = new PlaceJSON.Location();
        placeJSON.geometry.location.lat = place.getLatLng().latitude;
        placeJSON.geometry.location.lng = place.getLatLng().longitude;
        placeJSON.formatted_address = place.getAddress().toString();
        placeJSON.formatted_phone_number = place.getPhoneNumber().toString();

        return placeJSON;
    }

    @Override
    public double getLat() {
        return place.getLatLng().latitude;
    }

    @Override
    public double getLng() {
        return place.getLatLng().longitude;
    }
}
