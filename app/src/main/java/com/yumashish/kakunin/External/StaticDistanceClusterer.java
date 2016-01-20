package com.yumashish.kakunin.External;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.algo.Algorithm;
import com.yumashish.kakunin.GUI.Maps.XClusterItem;
import com.yumashish.kakunin.Interfaces.LocationObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by lightning on 1/18/16.
 */
public class StaticDistanceClusterer {
    double clusterDistance;
    List<Cluster> clusters;

    public static class Cluster {
        double maxDist;
        public LatLng center;
        public List<ClusterItem> objects;

        public Cluster(StaticDistanceClusterer clusterer, ClusterItem first) {
            center = first.getPosition();
            objects = new ArrayList();
            objects.add(first);
            maxDist = clusterer.clusterDistance;
        }

        public boolean addIfInRange(ClusterItem candidate) {
            if(Distance(candidate.getPosition(), center) < maxDist) {
                objects.add(candidate);
                return true;
            }
            return false;
        }
    }

    public StaticDistanceClusterer(double MaxClusterDistance, List<ClusterItem> items)
    {
        clusters = new ArrayList<>();
        clusterDistance = MaxClusterDistance;
        for (ClusterItem item : items) {
            boolean ploop = false;
            for (Cluster cluster : clusters) {
                if(cluster.addIfInRange(item)) {
                    ploop = true;
                    items.remove(item);
                }
            }
            if(!ploop) {
                //create new cluster
                clusters.add(new Cluster(this, item));
            }
        }
    }

    public List<Cluster> getClusters() { return clusters; }

    public static float Distance(LatLng a, LatLng b) {
        float[] dist = new float[1];
        Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, dist);
        return dist[0];
    }
}
