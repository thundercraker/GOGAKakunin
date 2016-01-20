package com.yumashish.kakunin.GUI.Maps;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yumashish.kakunin.External.LSObject;
import com.yumashish.kakunin.External.SimpleLocationObject;
import com.yumashish.kakunin.GUI.FBPhotoTapCard;
import com.yumashish.kakunin.GUI.SwipeCardsController;
import com.yumashish.kakunin.Interfaces.LocationObject;
import com.yumashish.kakunin.JSON.LoadLocationObject;
import com.yumashish.kakunin.JSON.PlaceJSON;
import com.yumashish.kakunin.JSON.PlaceWrapper;
import com.yumashish.kakunin.GOGAMapActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.yumashish.kakunin.KakuninHomeActivity;
import com.yumashish.kakunin.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * Created by yumashish on 11/2/15.
 */
public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    public static String TAG = "GOGA_MARKER_WINDOW_ADAPTER";
    GOGAMapActivity mContext;

    public MarkerInfoWindowAdapter(GOGAMapActivity context) {
        mContext = context;
    }

    public View getShopInfoView(String name, String address, String phone, String timeString) {
        ViewGroup window = (ViewGroup) mContext.getLayoutInflater().inflate(R.layout.info_window_goga_cafe, null);
        ((TextView) window.findViewById(R.id.iwgc_name)).setText(name);
        if(address != null && address.length() > 0) {
            ((TextView) window.findViewById(R.id.iwgc_address)).setText(address);
        } else {
            window.removeView(window.findViewById(R.id.igcw_address_container));
        }
        if(phone != null && phone.length() > 0) {
            ((TextView) window.findViewById(R.id.iwgc_phone_no)).setText(phone);
        } else {
            window.removeView(window.findViewById(R.id.igcw_phone_container));
        }
        if(timeString != null && timeString.length() > 0) {
            timeString = timeString.replaceAll("\\s{3,}", System.getProperty("line.separator"));
            ((TextView) window.findViewById(R.id.iwgc_time)).setText(timeString);
        } else {
            window.removeView(window.findViewById(R.id.igcw_time_container));
        }
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        Log.i(TAG, "Attempting to get item @ " + marker.getPosition());
        LocationObject locationObject = mContext.getLocationObjectFromDictionary(marker.getPosition());
        return processInfoWindowObject(locationObject);
    }

    View processInfoWindowObject(Object locationObject) {
        Log.i(TAG, "Attempting to resolve location object type " + ((locationObject != null) ? locationObject.getClass() : "null item"));
        if(locationObject == null) {
            Log.i(TAG, "Location object is null");
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
            return null;
        } else if(locationObject instanceof PlaceWrapper) {
            PlaceWrapper placeWrapper = (PlaceWrapper) locationObject;
            PlaceJSON place = placeWrapper.convertToPlaceJSON();
            if(place != null) {
                return getShopInfoView(place.name, place.formatted_address, place.formatted_phone_number, "");
            } else {
                Toast.makeText(mContext, "There was an error while retreiving the information of this marker", Toast.LENGTH_LONG).show();
                return null;
            }
        } else if(locationObject instanceof PlaceJSON) {
            PlaceJSON place = (PlaceJSON) locationObject;
            if (place != null) {
                return getShopInfoView(place.name, place.formatted_address, place.formatted_phone_number, "");
            } else {
                Toast.makeText(mContext, "There was an error while retreiving the information of this marker", Toast.LENGTH_LONG).show();
                return null;
            }
        } else if (locationObject instanceof SimpleLocationObject) {
            SimpleLocationObject lsObject = (SimpleLocationObject) locationObject;
            if (lsObject != null) {
                View infoWindow = mContext.getLayoutInflater().inflate(R.layout.info_window_marker, null);
                //ImageView imageView = (ImageView) infoWindow.findViewById(R.id.infoWindowPlacePicture);
                Log.i(TAG, "Setting place and address " + lsObject.name + " | " + lsObject);
                return getShopInfoView(lsObject.getName(), lsObject.getAddress(), lsObject.getTel(), lsObject.getOpentime());
            } else {
                Toast.makeText(mContext, "There was an error while retreiving the information of this marker", Toast.LENGTH_LONG).show();
                return null;
            }
        } else if(locationObject instanceof LoadLocationObject) {
            LoadLocationObject loadLocationObject = (LoadLocationObject) locationObject;
            return processInfoWindowObject(loadLocationObject.getLoad());
        } else if(locationObject instanceof KakuninHomeActivity.TypedJSONObject) {
            try {
                KakuninHomeActivity.TypedJSONObject typedObject = (KakuninHomeActivity.TypedJSONObject) locationObject;
                if (typedObject.type == KakuninHomeActivity.TypedJSONObject.Type.PHOTO) {
                    JSONObject photo = typedObject.object;
                    View infoWindow = mContext.getLayoutInflater().inflate(R.layout.info_window_marker, null);
                    TextView titleTV = (TextView) infoWindow.findViewById(R.id.infoWindowMarkerTitile);
                    TextView addrTV = (TextView) infoWindow.findViewById(R.id.infoWindowMarkerAddress);
                    titleTV.setText("From " + photo.getJSONObject("from").getString("name"));
                    String datetimeraw = photo.getString("created_time");
                    String[] dtSplit = datetimeraw.split("T");
                    String[] tTzSplit = dtSplit[1].split("\\+");
                    addrTV.setText("Created on " + dtSplit[0] + " " + tTzSplit[0]);
                    return infoWindow;
                }
            } catch (ClassCastException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        } else if(locationObject instanceof List) {
            List listObject = (List) locationObject;
            return processInfoWindowObjectList(listObject);
        } else {
            Log.i(TAG, "There is no defined behaviour for " + locationObject.getClass());
            return null;
        }
    }

    View processInfoWindowObjectList(List locationObjects) {
        if(locationObjects.size() > 0) {
            Object firstObject = locationObjects.get(0);
            Log.i(TAG, "Attempting to resolve location object from list of type " + firstObject.getClass());
            if (firstObject instanceof XClusterItem) {
                return getShopInfoView(locationObjects.size() + " photos", "", "", "");
            } else {
                Log.i(TAG, "There is no defined behaviour for a list of " + firstObject.getClass());
            }
        } else {
            Log.i(TAG, "The list is empty");
        }
        return null;
    }
}
