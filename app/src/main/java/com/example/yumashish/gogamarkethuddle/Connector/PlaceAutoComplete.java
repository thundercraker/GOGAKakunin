package com.example.yumashish.gogamarkethuddle.Connector;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by yumashish on 10/31/15.
 */
public class PlaceAutoComplete  {
    public static final String TAG = "GOGAHuddleAutoComplete";
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;
    private ArrayList<AutocompletePrediction> mResultList;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;

    public PlaceAutoComplete(Context context, GoogleApiClient apiClient, GoogleMap googleMap, LatLngBounds bounds, AutocompleteFilter filter) {
        mContext = context;
        mGoogleApiClient = apiClient;
        mResultList = new ArrayList<AutocompletePrediction>();
        mBounds = bounds;
        mPlaceFilter = filter;
        mGoogleMap = googleMap;
    }

    public void SingleCommit() {
        Log.i(TAG, "Beginning Single Search for type");
        mResultList = GetAutoComplete("");
        if(mResultList != null && mResultList.size() > 0) {
            for (AutocompletePrediction prediction : mResultList) {
                Log.i(TAG, "Prediction: " + prediction.getFullText(null).toString());
            }
        } else {
            //Toast.makeText(mContext, "No results", Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<AutocompletePrediction> GetAutoComplete(CharSequence constraint) {
        if(mGoogleApiClient.isConnected()) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint);

            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        } else {
            Log.d(TAG, "Google API Client not connected");
            return null;
        }
    }
}
