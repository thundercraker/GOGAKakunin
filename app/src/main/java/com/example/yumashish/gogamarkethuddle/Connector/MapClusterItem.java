package com.example.yumashish.gogamarkethuddle.Connector;

import com.example.yumashish.gogamarkethuddle.Connector.JSON.PlaceJSON;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by yumashish on 11/2/15.
 */
public class MapClusterItem implements ClusterItem {
    private final LatLng mPosition;

    public MapClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MapClusterItem(LatLng latLng) {
        mPosition = latLng;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
