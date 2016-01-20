package com.yumashish.kakunin.External;

import android.util.Log;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.StaticCluster;
import com.yumashish.kakunin.KakuninHomeActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by lightning on 1/18/16.
 */
public class DPDistanceClusterer implements Algorithm<ClusterItem> {
    public static String TAG = "GOGA_DISTANCE_CLUSTERER";
    int pixels;
    List<ClusterItem> items;
    int screenWidth;

    public DPDistanceClusterer(int pixels, int screenWidth) {
        this.pixels = pixels;
        items = new ArrayList<>();
        this.screenWidth = screenWidth;
    }

    @Override
    public void addItem(ClusterItem clusterItem) {
        items.add(clusterItem);
    }

    @Override
    public void addItems(Collection<ClusterItem> collection) {
        items.addAll(collection);
    }

    @Override
    public void clearItems() {
        items.clear();
    }

    @Override
    public void removeItem(ClusterItem clusterItem) {
        items.remove(clusterItem);
    }

    @Override
    public Set<? extends Cluster<ClusterItem>> getClusters(double v) {
        double mpp = metersPerPixelAt(screenWidth, v);
        Log.i(TAG, "Meters per pixel " + mpp + " @ zoom " + v);



        return null;
    }

    public double metersPerPixelAt(int screenWidth, double zoom) {
        double equatorLength = 40075004;
        double widthInPixels = screenWidth;
        return equatorLength/256d * Math.pow(2, zoom - 1);
    }

    @Override
    public Collection<ClusterItem> getItems() {
        return items;
    }
}
