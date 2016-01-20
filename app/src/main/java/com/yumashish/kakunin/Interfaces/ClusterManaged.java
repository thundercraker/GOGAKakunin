package com.yumashish.kakunin.Interfaces;

import com.yumashish.kakunin.GUI.Maps.MapClusterItem;
import com.yumashish.kakunin.GUI.Maps.MarkerInfoWindowAdapter;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yumashish on 11/4/15.
 */
public interface ClusterManaged {
    void setUpClusterer();
    void cluster();
    void addToClusterManager(MapClusterItem mci);
    void putToLocationObjectDictionary(LatLng position, LocationObject place);
    LocationObject getLocationObjectFromDictionary(LatLng key);
    void putToClusterItemDictionary(LatLng position, MapClusterItem mci);
    MapClusterItem getClusterItemFromDictionary(LatLng key);

    //void redrawMarker(LatLng pos, Bitmap icon);
    //void redrawMarker(MapClusterItem pos, Bitmap icon);
    MarkerInfoWindowAdapter getMarkerInfoWindowAdapter();
    void setInfoWindowAdapter(MarkerInfoWindowAdapter infoWindowAdapter);
}
