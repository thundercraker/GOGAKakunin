package com.yumashish.kakunin.Tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.yumashish.kakunin.JSON.PlaceJSON;
import com.yumashish.kakunin.GOGAMapActivity;
import com.yumashish.kakunin.GUI.PlaceDetailLayout;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

/**
 * Created by yumashish on 11/5/15.
 */
public class LoadPlaceDetailsTask extends AsyncTask<Void, Integer, Void> {
    public static final String TAG = "GOGA_LD_PLC_DTAILS_TSK";
    private GOGAMapActivity mContext;
    private PlaceDetailLayout mLayout;
    private PlaceJSON mPlace;
    private ImagesBundle<PlaceDetailLayout> mBundle;
    private int SyncCount;

    public static class ImagesBundle<T> {
        public T object;
        public Bitmap image;

        public ImagesBundle() {
        }

        public ImagesBundle(T object, Bitmap image) {
            this.object = object;
            this.image = image;
        }
    }

    public LoadPlaceDetailsTask(GOGAMapActivity context, PlaceDetailLayout layout, PlaceJSON place) {
        mContext = context;
        mLayout = layout;
        mPlace = place;
        mBundle = new ImagesBundle();
        mBundle.object = mLayout;
        SyncCount = 0;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //Load Place Details


        if(mPlace.id != null && mPlace.id.length() > 0) {
            //TODO Get the summary from wiki API
            Log.i(TAG, "Loading details for " + mPlace.name + " id: " + mPlace.place_id);
            Places.GeoDataApi.getPlaceById(mContext.getGoogleApiClient(), mPlace.place_id)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if(places.getStatus().isSuccess() && places.getCount() > 0) {
                                Log.i(TAG, "Loaded Place details for " + mPlace.name + " id: " + mPlace.place_id);
                                final Place place = places.get(0);
                                mPlace.rating = place.getPriceLevel();
                                //redrawInfoWindow();
                                mLayout.detail.setText(place.getAddress() + "\n" +place.getPhoneNumber());
                                if(place.getRating() >= 1f && place.getRating() <= 5f) {
                                    Log.i(TAG, "Valid rating of " + place.getRating());
                                    mLayout.rating.setRating(place.getRating());
                                } else {
                                    Message msg = mContext.getMessageHandler().obtainMessage();
                                    Bundle b = new Bundle();
                                    b.putSerializable(GOGAMapActivity.MESSAGE_TYPE_KEY, GOGAMapActivity.MessageType.NO_RATING_FOUND);
                                    msg.setData(b);
                                    msg.obj = mLayout;
                                    Log.i(TAG, "Invalid rating " + place.getRating() + " , sending delete rating UI message, type " + msg.getData().getSerializable(GOGAMapActivity.MESSAGE_TYPE_KEY));
                                    mContext.getMessageHandler().sendMessage(msg);
                                }
                            } else {
                                Log.e(TAG, "[Place Load] Query returned with " + places.getStatus().getStatusMessage() + " code " + places.getStatus().getStatusCode() + " (if SUCCESS maybe count is 0) Count: " + places.getCount());
                            }
                            places.release();
                        }
                    });
            Places.GeoDataApi.getPlacePhotos(mContext.getGoogleApiClient(), mPlace.place_id)
                    .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                        @Override
                        public void onResult(PlacePhotoMetadataResult placePhotoMetadataResult) {
                            if (placePhotoMetadataResult.getStatus().isSuccess()) {
                                final PlacePhotoMetadataBuffer placePhotoMetadataBuffer = placePhotoMetadataResult.getPhotoMetadata();

                                if(placePhotoMetadataBuffer.getCount() == 0) {
                                    Message msg1 = mContext.getMessageHandler().obtainMessage();//new Message();
                                    Bundle b1 = new Bundle();
                                    b1.putSerializable(GOGAMapActivity.MESSAGE_TYPE_KEY, GOGAMapActivity.MessageType.PLACE_IMAGE_LOAD_FINISHED);
                                    msg1.setData(b1);
                                    msg1.obj = mBundle;
                                    Log.i(TAG, "Sending finished message to handler");
                                    mContext.getMessageHandler().sendMessage(msg1);
                                } else {
                                    for (int i = 0; i < placePhotoMetadataBuffer.getCount(); i++) {
                                        mBundle.object.SetupProgress(placePhotoMetadataBuffer.getCount());
                                        final PlacePhotoMetadata placePhotoMetadata = placePhotoMetadataBuffer.get(i);
                                        placePhotoMetadata.getPhoto(mContext.getGoogleApiClient())
                                                .setResultCallback(new ResultCallback<PlacePhotoResult>() {
                                                    @Override
                                                    public void onResult(PlacePhotoResult placePhotoResult) {
                                                        Log.i(TAG, "Loaded Place Image for " + mPlace.name + " id: " + mPlace.place_id);
                                                        //mPlace.image = placePhotoResult.getBitmap();
                                                        mBundle.image = placePhotoResult.getBitmap();
                                                        Message msg = mContext.getMessageHandler().obtainMessage();//new Message();
                                                        Bundle b = new Bundle();
                                                        b.putSerializable(GOGAMapActivity.MESSAGE_TYPE_KEY, GOGAMapActivity.MessageType.PLACE_IMAGE_LOADED);
                                                        msg.setData(b);
                                                        msg.obj = mBundle;
                                                        Log.i(TAG, "Sending message to handler");
                                                        mContext.getMessageHandler().sendMessage(msg);
                                                        if (SyncCount++ == placePhotoMetadataBuffer.getCount() - 1) {
                                                            Message msg1 = mContext.getMessageHandler().obtainMessage();//new Message();
                                                            Bundle b1 = new Bundle();
                                                            b1.putSerializable(GOGAMapActivity.MESSAGE_TYPE_KEY, GOGAMapActivity.MessageType.PLACE_IMAGE_LOAD_FINISHED);
                                                            msg1.setData(b1);
                                                            msg1.obj = mBundle;
                                                            Log.i(TAG, "Sending finished message to handler");
                                                            mContext.getMessageHandler().sendMessage(msg1);
                                                        }
                                                    }
                                                });

                                    }
                                }
                                placePhotoMetadataBuffer.release();
                            } else {
                                Log.e(TAG, "[Image Load] Query returned with " + placePhotoMetadataResult.getStatus().getStatusMessage() + " code " + placePhotoMetadataResult.getStatus().getStatusCode());
                            }
                        }
                    });

        } else {
            Log.i(TAG, "This place " + mPlace.name + " does not have an associated Google Places ID");
        }

        return null;
    }

    private void redrawInfoWindow() {
        mContext.redrawInfoWindow(mPlace.getLatLng());
    }
}
