package com.yumashish.kakunin.Tasks;

import android.util.Log;

import com.yumashish.kakunin.JSON.DirectionsJSON;
import com.yumashish.kakunin.GOGAMapActivity;
import com.yumashish.kakunin.QuickAsyncTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.JsonObjectParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yumashish on 11/6/15.
 */
public class GetDirectionsBetweenLocations extends QuickAsyncTask<Void, Void, DirectionsJSON> {
    public static final String TAG = "GOGA_KKN_DIR_TASK";
    public enum TravelMode { DRIVING, WALKING, BICYCLING, TRANSIT, NONE };

    GOGAMapActivity mContext;
    LatLng mStart, mDest;
    List<String> mWaypoints;
    boolean mOptimize;
    TravelMode mMode;

    private DirectionsJSON directions;

    public DirectionsJSON getDirections() {
        return directions;
    }

    public static String ToModeString(TravelMode mode) {
        switch (mode) {
            case DRIVING:
                return "driving";
            case WALKING:
                return "walking";
            case BICYCLING:
                return "bicycling";
            case TRANSIT:
                return "transit";
            default:
                return "";
        }
    }

    public GetDirectionsBetweenLocations(GOGAMapActivity context, LatLng start, LatLng dest, TravelMode mode, boolean optimize) {
        super(new QuickTask<Void, DirectionsJSON>() {
            @Override
            public void Pre() {

            }

            @Override
            public DirectionsJSON Do(Void... params) {
                return null;
            }

            @Override
            public void Post(DirectionsJSON param) {

            }
        });
        mContext = context;
        mStart = start;
        mDest = dest;
        mOptimize = optimize;
        mMode = mode;
    }

    public void setWaypoints(List<String> waypoints) {
        mWaypoints = (waypoints != null) ? waypoints : new ArrayList<String>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected DirectionsJSON doInBackground(Void... params) {
        super.doInBackground();
        Log.i(TAG, "Begin directions download routine");
        directions = searchDirections();
        return directions;
    }

    @Override
    protected void onPostExecute(DirectionsJSON p) {
        super.onPostExecute(p);
        Log.i(TAG, "Finished load of " + directions.routes.size() + " routes");
    }

    private DirectionsJSON searchDirections() {
        try {
            HttpRequestFactory httpRequestFactory = GOGAMapActivity.HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                    request.setParser(new JsonObjectParser(GOGAMapActivity.JSON_FACTORY));
                }
            });
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(GOGAMapActivity.DIRECTIONS_SEARCH_URL));
            request.getUrl().put("origin", mStart.latitude + "," + mStart.longitude);
            request.getUrl().put("destination", mDest.latitude + "," + mDest.longitude);
            request.getUrl().put("key", mContext.getWAK());
            String waypointString = ToWaypointsString();
            if(waypointString != "")
                request.getUrl().put("waypoints", ((mOptimize) ? "optimize:true|" : "" ) + waypointString);
            /*
            request.getUrl().put("sensor", "false");
            request.getUrl().put("language", "en");
            request.getUrl().put("mode", "driving");
            request.getUrl().put("alternatives", "true");
            */
            String modeString = ToModeString(mMode);
            if(modeString.length() < 1)
                request.getUrl().put("mode", modeString);
            Log.i(TAG, "Query " + request.getUrl().toString());
            Log.i(TAG, request.execute().parseAsString());
            return ParseResponse(request.execute());
        } catch (HttpResponseException e) {
            Log.e(TAG, "HTTP Exception [Code " + e.getStatusCode() + "] " + e.getStatusMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IO Exception " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "Generic Exception " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public DirectionsJSON ParseResponse(HttpResponse response) throws IOException {
        DirectionsJSON directionsJSON = response.parseAs(DirectionsJSON.class);
        Log.i(TAG, "Response Code: " + response.getStatusCode() +
                " Message: " + response.getStatusMessage() +
                " Content-Type: " + response.getContentType());
        return directionsJSON;
    }

    public String ToWaypointsString() {
        if(mWaypoints == null || mWaypoints.size() < 1)
            return "";
        StringBuilder builder = new StringBuilder();
        for(String waypoint : mWaypoints) {
            builder.append(waypoint);
            builder.append("|");
        }
        if(builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
