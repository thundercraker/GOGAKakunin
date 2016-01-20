package com.yumashish.kakunin.Tasks;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.yumashish.kakunin.JSON.PlaceJSON;
import com.yumashish.kakunin.JSON.ResponseJSON;
import com.yumashish.kakunin.GUI.Maps.XClusterItem;
import com.yumashish.kakunin.Legacy.EnableLogging;
import com.yumashish.kakunin.GOGAMapActivity;
import com.yumashish.kakunin.GUI.Maps.MarkerInfoWindowAdapter;
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
import java.util.List;

/**
 * Created by yumashish on 11/4/15.
 */
public class SearchForPlacesTask extends QuickAsyncTask<Void, Void, List<PlaceJSON>> {
    public static final String TAG = "GOGA_KKN_SRH_PL";
    GOGAMapActivity mContext;
    LatLng mLocation;
    double mRadius;
    String mTypes, mName, mKeyword;
    Handler mHandler;
    boolean mNoResult;//, mConsume;

    List<PlaceJSON> mPlaceList;

    public SearchForPlacesTask(GOGAMapActivity context, String name, String keyword, LatLng location, double radius, String types, QuickTask<Void, List<PlaceJSON>> quickTask) {
        super(quickTask);
        mContext = context;
        mLocation = location;
        mRadius = radius;
        mTypes = types;
        mName = name;
        mKeyword = keyword;
        mHandler = mContext.getMessageHandler();
        //mConsume = true;
        mNoResult = false;
    }

    /*public void setConsume(boolean consume) {
        mConsume = consume;
    }*/

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //create new cluster manager
        mContext.setUpClusterer();
    }

    @Override
    protected List<PlaceJSON> doInBackground(Void...params) {
        super.doInBackground(params);
        mPlaceList = SearchForPlaces();
        if(mPlaceList != null && mPlaceList.size() > 0) {
            Log.i(TAG, "Found " + mPlaceList.size() + " items");
            for (PlaceJSON place : mPlaceList) {
                Log.i(TAG, "Retreived new place: " + place.name);
            }
        } else {
            mNoResult = true;
        }
        return mPlaceList;
    }

    @Override
    protected void onPostExecute(List<PlaceJSON> places) {
        super.onPostExecute(places);
        if(mNoResult) {
            mContext.makeToast("No results found for search criteria", Toast.LENGTH_LONG);
            Log.i(TAG, "No results found for search");
        }/* else if(mConsume) {
            for (PlaceJSON place:mPlaceList) {
                XClusterItem<PlaceJSON> MCI = new XClusterItem<>(place.geometry.location.lat, place.geometry.location.lng, place, null);
                mContext.addToClusterManager(MCI);
                mContext.putToLocationObjectDictionary(MCI.getPosition(), MCI.getLoad());
            }

            mContext.cluster();
            MarkerInfoWindowAdapter markerInfoWindowAdapter = mContext.getMarkerInfoWindowAdapter();
            mContext.setInfoWindowAdapter(markerInfoWindowAdapter);
        }*/
    }

    private List<PlaceJSON> SearchForPlaces() {
        try {
            HttpRequestFactory httpRequestFactory = GOGAMapActivity.HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                    request.setParser(new JsonObjectParser(GOGAMapActivity.JSON_FACTORY));
                }
            });
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(GOGAMapActivity.PLACES_SEARCH_URL));
            request.getUrl().put("key", mContext.getWAK());
            if(mLocation != null)
                request.getUrl().put("location", mLocation.latitude + "," + mLocation.longitude);
            if(mRadius != Double.POSITIVE_INFINITY)
                request.getUrl().put("radius", mRadius);
            request.getUrl().put("sensor", "false");
            request.getUrl().put("language", "en");
            if(mName != null) {
                request.getUrl().put("name", mName);
            }
            if(mTypes != null) {
                request.getUrl().put("types", mTypes);
            }
            if(mKeyword != null) {
                request.getUrl().put("keyword", mKeyword);
            }
            Log.i(TAG, "Query " + request.getUrl().toString());
            EnableLogging.enableLogging();
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

    public List<PlaceJSON> ParseResponse(HttpResponse response) throws IOException {
        ResponseJSON responseJSON = response.parseAs(ResponseJSON.class);
        Log.i(TAG, "Response Code: " + response.getStatusCode() +
                " Message: " + response.getStatusMessage() +
                " Content-Type: " + response.getContentType());
        return responseJSON.getResults();
    }
}