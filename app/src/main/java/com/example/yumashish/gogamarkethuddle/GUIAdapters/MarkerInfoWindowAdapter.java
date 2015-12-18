package com.example.yumashish.gogamarkethuddle.GUIAdapters;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yumashish.gogamarkethuddle.Connector.JSON.LocationObject;
import com.example.yumashish.gogamarkethuddle.Connector.JSON.PlaceJSON;
import com.example.yumashish.gogamarkethuddle.Connector.JSON.PlaceWrapper;
import com.example.yumashish.gogamarkethuddle.Connector.JSON.RandomMarker;
import com.example.yumashish.gogamarkethuddle.Kakunin.GOGAMapActivity;
import com.example.yumashish.gogamarkethuddle.Kakunin.KakuninHomeActivity;
import com.example.yumashish.gogamarkethuddle.R;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Dictionary;

/**
 * Created by yumashish on 11/2/15.
 */
public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    public static String TAG = "GOGA_MARKER_WINDOW_ADAPTER";
    GOGAMapActivity mContext;
    Dictionary<LatLng, LocationObject> mLocationObjectJSONDictionary;

    public MarkerInfoWindowAdapter(GOGAMapActivity context, Dictionary<LatLng, LocationObject> markerPlaceJSONDictionary) {
        mContext = context;
        mLocationObjectJSONDictionary = markerPlaceJSONDictionary;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LocationObject locationObject = mLocationObjectJSONDictionary.get(marker.getPosition());
        if(locationObject == null) {
            Log.i(TAG, "Location object for " + marker.getPosition() + "is null");
//        } else if(locationObject.getClass().equals(RandomMarker.class)) {
//            View infowindow = mContext.getLayoutInflater().inflate(R.layout.random_marker_info_window, null);
//            TextView line1 = (TextView) infowindow.findViewById(R.id.rmiw_line1);
//            TextView line2 = (TextView) infowindow.findViewById(R.id.rmiw_line2);
//            ImageView image = (ImageView) infowindow.findViewById(R.id.rmiw_image);
//
//            image.setImageBitmap(KakuninHomeActivity.Resize(mContext.getResources().getDrawable(R.drawable.hot_deal), 250, 250));
//            line1.setText("Latitude: " + marker.getPosition().latitude);
//            line2.setText("Longitude: " + marker.getPosition().longitude);
//
//            return infowindow;
        } else if(locationObject instanceof PlaceWrapper) {
            PlaceWrapper placeWrapper = (PlaceWrapper) locationObject;
            PlaceJSON place = placeWrapper.convertToPlaceJSON();
            if(place != null) {
                View infoWindow = mContext.getLayoutInflater().inflate(R.layout.info_window_marker, null);
                //ImageView imageView = (ImageView) infoWindow.findViewById(R.id.infoWindowPlacePicture);
                TextView titleTV = (TextView) infoWindow.findViewById(R.id.infoWindowMarkerTitile);
                TextView addrTV = (TextView) infoWindow.findViewById(R.id.infoWindowMarkerAddress);
                Log.i(TAG, "Setting place and address " + place.name + " | " + place.formatted_address);
                titleTV.setText(place.name);
                addrTV.setText(place.formatted_address);
                addrTV.setText((addrTV.getText().length() < 1) ? place.formatted_phone_number : addrTV.getText().toString() + "\n" + place.formatted_phone_number);
                if (addrTV.getText().length() < 1)
                    ((ViewGroup) addrTV.getParent()).removeView(addrTV);
                return infoWindow;
            } else {
                Toast.makeText(mContext, "There was an error while retreiving the information of this marker", Toast.LENGTH_LONG).show();
                return null;
            }
        } else if(locationObject instanceof PlaceJSON) {
            PlaceJSON place = (PlaceJSON) locationObject;
            if (place != null) {
                View infoWindow = mContext.getLayoutInflater().inflate(R.layout.info_window_marker, null);
                //ImageView imageView = (ImageView) infoWindow.findViewById(R.id.infoWindowPlacePicture);
                TextView titleTV = (TextView) infoWindow.findViewById(R.id.infoWindowMarkerTitile);
                TextView addrTV = (TextView) infoWindow.findViewById(R.id.infoWindowMarkerAddress);
                Log.i(TAG, "Setting place and address " + place.name + " | " + place.formatted_address);
                titleTV.setText(place.name);
                addrTV.setText(place.formatted_address);
                addrTV.setText((addrTV.getText().length() < 1) ? place.formatted_phone_number : addrTV.getText().toString() + "\n" + place.formatted_phone_number);
                if (addrTV.getText().length() < 1)
                    ((ViewGroup) addrTV.getParent()).removeView(addrTV);
                return infoWindow;
            } else {
                Toast.makeText(mContext, "There was an error while retreiving the information of this marker", Toast.LENGTH_LONG).show();
                return null;
            }
        } else {
            Log.i(TAG, "There is no defined behaviour for " + locationObject.getClass());
        }
        return null;
    }
}
