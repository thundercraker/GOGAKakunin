package com.example.yumashish.gogamarkethuddle.Connector.JSON;

import android.nfc.Tag;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumashish on 11/6/15.
 */
public class DirectionsJSON {

    @Key
    public List<GeocodedWaypoint> geocoded_waypoints;

    @Key
    public List<Route> routes;

    @Key
    public String status;

    public static class Route {
        @Key
        public Bound bounds;

        @Key
        public List<Leg> legs;

        @Key
        public OverviewPolyline overview_polyline;

        public static class OverviewPolyline {
            @Key
            public String points;
        }

        @Key
        public String summary;

        @Key
        public List<String> warnings;

        @Key
        public List<Integer> waypoint_order;

        public List<LatLng> getDecodedPoly() {
            String encoded = overview_polyline.points;
            Log.i("DirectionsJSON", "Attemptig to decode polyline points encoded as" + encoded);
            List<LatLng> poly = new ArrayList();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng( (((double) lat / 1E5)),
                        (((double) lng / 1E5) ));
                poly.add(p);
            }

            return poly;
        }

        public static class Bound {
            @Key
            public PlaceJSON.Location northeast;

            @Key
            public PlaceJSON.Location southwest;
        }

        public static class Leg {

            @Key
            public TextValue distance;

            @Key
            public TextValue duration;

            @Key
            public String end_address;

            @Key
            public PlaceJSON.Location end_location;

            @Key
            public String start_address;

            @Key
            public PlaceJSON.Location start_location;

            @Key
            public List<Step> steps;

            public static class Step {
                @Key
                public TextValue distance;

                @Key
                public TextValue duration;

                @Key
                public PlaceJSON.Location end_location;

                @Key
                public String html_instuctions;

                @Key
                public PlaceJSON.Location start_location;

                @Key
                public String travel_mode;
            }

            public static class TextValue {
                @Key
                public String text;

                @Key
                public int value;
            }
        }
    }

    public static class GeocodedWaypoint {
        @Key
        public String geocoder_status;

        @Key
        public String place_id;

        @Key
        public List<String> types;
    }
}
