package com.example.yumashish.gogamarkethuddle.GUIAdapters;

import android.content.Context;
import android.util.Log;

import com.example.yumashish.gogamarkethuddle.Connector.MapClusterItem;
import com.example.yumashish.gogamarkethuddle.Connector.NamedObject;
import com.example.yumashish.gogamarkethuddle.Connector.XClusterItem;
import com.example.yumashish.gogamarkethuddle.Kakunin.GOGAMapActivity;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by yumashish on 11/4/15.
 */
public class MarkerCustomRenderer extends DefaultClusterRenderer<MapClusterItem> {
    public MarkerCustomRenderer(Context context, GoogleMap map, ClusterManager<MapClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapClusterItem item, MarkerOptions markerOptions) {
       if (item.getClass().equals(XClusterItem.class)) {
            XClusterItem xcitem = (XClusterItem) item;
            if(xcitem.getLoad() instanceof NamedObject)
                markerOptions.title(((NamedObject) xcitem.getLoad()).getName());
            else if(xcitem.getLoad() instanceof Place)
                markerOptions.title(((Place) xcitem.getLoad()).getName().toString());

            if(xcitem.icon != null)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(xcitem.icon));
        } else {
           Log.d(GOGAMapActivity.TAG, "Unknown map cluster item type " + item.getClass().getName());
       }
    }
}
