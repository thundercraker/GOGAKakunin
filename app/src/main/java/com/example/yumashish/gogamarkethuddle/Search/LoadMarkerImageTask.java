package com.example.yumashish.gogamarkethuddle.Search;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.yumashish.gogamarkethuddle.Connector.MapClusterItem;
import com.example.yumashish.gogamarkethuddle.Kakunin.GOGAMapActivity;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yumashish on 11/4/15.
 */
public class LoadMarkerImageTask extends LoadImageTask {
    public static final String TAG = "GOGA_LOAD_MARKER_PROF";
    GOGAMapActivity mContext;
    LatLng mMarkerRef;

    public LoadMarkerImageTask(GOGAMapActivity context, LatLng markerRef, String url, int width, int height) {
        super(url, width, height, 5);
        mMarkerRef = markerRef;
        mContext = context;
    }

    @Override
    protected void onPostExecute(Bitmap b) {
        MapClusterItem mapClusterItem = mContext.getClusterItemFromDictionary(mMarkerRef);
        Log.i(TAG, "Loaded bitmap for " + mMarkerRef.toString() + " redrawing marker");
        mContext.redrawMarker(mMarkerRef, b);
    }
}
