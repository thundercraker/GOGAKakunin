package com.example.yumashish.gogamarkethuddle.Kakunin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.yumashish.gogamarkethuddle.Connector.JSON.LocationObject;
import com.example.yumashish.gogamarkethuddle.Connector.JSON.PlaceJSON;
import com.example.yumashish.gogamarkethuddle.Connector.JSON.PlaceWrapper;
import com.example.yumashish.gogamarkethuddle.Connector.MapClusterItem;
import com.example.yumashish.gogamarkethuddle.Connector.XClusterItem;
import com.example.yumashish.gogamarkethuddle.CustomMapFragment;
import com.example.yumashish.gogamarkethuddle.GUIAdapters.MarkerCustomRenderer;
import com.example.yumashish.gogamarkethuddle.GUIAdapters.MarkerInfoWindowAdapter;
import com.example.yumashish.gogamarkethuddle.GUIAdapters.PlaceAutocompleteAdapter;
import com.example.yumashish.gogamarkethuddle.GUIAdapters.PlaceDetailLayout;
import com.example.yumashish.gogamarkethuddle.R;
import com.example.yumashish.gogamarkethuddle.Search.LoadPlaceDetailsTask;
//import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Created by yumashish on 11/4/15.
 */
public abstract class GOGAMapActivity extends ToolbarDrawerActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    public static final String TAG = "GOGA_KKN_MAPATV";
    public static final String INTENT_CURRENT_USER_TAG = "INTENT_CURRENT_USER_TAG";
    public static final String INTENT_MESSAGE_USER_TAG = "INTENT_MESSAGE_USER_TAG";
    protected GoogleApiClient mGoogleApiClient;
    protected GoogleMap mGoogleMap;
    protected Location mLastKnownLocation;
    protected ClusterManager<MapClusterItem> mClusterManager;
    private Hashtable<LatLng, LocationObject> mLocationObjectJSONDictionary;
    private Hashtable<LatLng, MapClusterItem> mClusterItemDictionary;
    protected Handler GOGATaskHandler;
    private MarkerCustomRenderer mMarkerCustomRenderer;

    private ViewSwitcher viewSwitcher;
    private LinearLayout hiddenFrame;
    private boolean hiddenShown = false;
    private CustomMapFragment mMapFragment;
    protected int mChatUserId;

    public static final String MESSAGE_TYPE_KEY = "types";
    public enum MessageType { BEGIN_LOAD_PLACE_DETAIL, PLACE_IMAGE_LOADED, PLACE_IMAGE_LOAD_FINISHED, NO_RATING_FOUND,
        STORE_LOGO_LOADED, USER_PROFILE_LOADED, EXPAND_CIRCLE }

    public enum MenuType { DEFAULT, USER_DETAIL }
    private MenuType mMenuType = MenuType.DEFAULT;

    private long pressStart = 0l;
    boolean extendMapCircle = false;

    @Override
    protected int getContentLayoutId() {
        return R.layout.home_map_content;
    }

    @OverridingMethodsMustInvokeSuper
    protected int getToolbarId() {
        return R.id.home_action_bar;
    }

    public GOGAMapActivity getGOGAMapActivity() {
        return this;
    }

    public enum MapMode { NONE, SHOUT_CIRCLE, SET_MARKER };
    protected boolean mBlockMapTouch = false;
    protected MapMode mCurrentMapMode = MapMode.NONE;

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    protected RelativeLayout mAutoCompleteLayer;
    AutoCompleteTextView mAutoCompleteTextView;
    PlaceAutocompleteAdapter mAutoCompleteAdapter;


    Bitmap search_icon;

    @Override
    protected void SetContent(FrameLayout contentFrame) {
        super.SetContent(contentFrame);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewFlipper);
        hiddenFrame = (LinearLayout) findViewById(R.id.customer_home_hidden_layer);
    }

    public LinearLayout getHiddenFrame() {
        return hiddenFrame;
    }

    public void ShowMain() {
        if(hiddenShown) {
            viewSwitcher.showNext();
            hiddenShown = false;
        }
    }

    public void ShowHidden() {
        if(!hiddenShown) {
            viewSwitcher.showNext();
            hiddenShown = true;
        }
    }

    long mPressStart;

    Circle mMutableCircle, mFinalCircle;
    int mDeltaMeters;
    long mPrevTime;
    double mDeltaTime;

    class RepeatingPing extends AsyncTask<Void, Void, Void> {
        long prevTime;
        int pingEvery;

        public RepeatingPing(int pingEvery) {
            this.pingEvery = pingEvery;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            while(extendMapCircle) {
                if(pingEvery < (System.currentTimeMillis() - prevTime)) {
                    Message m = new Message();
                    Bundle b = new Bundle();
                    b.putSerializable(MESSAGE_TYPE_KEY, MessageType.EXPAND_CIRCLE);
                    m.setData(b);
                    getMessageHandler().sendMessage(m);
                    prevTime = System.currentTimeMillis();
                }
            }

            return null;
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        mLocationObjectJSONDictionary = new Hashtable<>();
        mClusterItemDictionary = new Hashtable<>();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mMapFragment = (CustomMapFragment) ( getSupportFragmentManager().findFragmentById(R.id.customer_home_map_fragment));
        mMapFragment.getMapAsync(this);

        mMapFragment.setOnTouchListsner(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mBlockMapTouch)
                    return true;

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i(TAG, "Pressed " + System.currentTimeMillis());
                    pressStart = System.currentTimeMillis();
                    if (mCurrentMapMode == MapMode.SHOUT_CIRCLE) {
                        extendMapCircle = true;
                        mPrevTime = System.currentTimeMillis();
                        mDeltaMeters = 100;
                        //LatLng loc = new LatLng(getCurrentUserLastLocation().getLatitude(), getCurrentUserLastLocation().getLongitude());
                        LatLng loc = mGoogleMap.getCameraPosition().target;

                        if (mMutableCircle != null)
                            mMutableCircle.remove();
                        mMutableCircle = mGoogleMap.addCircle(new CircleOptions().center(loc).radius(100).strokeColor(Color.argb(255, 134, 158, 179)).strokeWidth(3f));
                    } else if(mCurrentMapMode == MapMode.SET_MARKER) {
                        mPressStart = System.currentTimeMillis();
                    }
                    new RepeatingPing(50).execute();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    final boolean long_press = Math.abs(System.currentTimeMillis() - mPressStart) > 666;

                    extendMapCircle = false;
                    if (mCurrentMapMode == MapMode.SHOUT_CIRCLE) {
                        mMutableCircle.setFillColor(Color.argb(128, 134, 158, 179));
                        mCurrentMapMode = MapMode.NONE;
                        DoShout(mMutableCircle);
                    } else if(mCurrentMapMode == MapMode.SET_MARKER) {
                        if(long_press) {
                            final double x = event.getX();
                            final double y = event.getY();
                            Log.i(TAG, "Long tap while on marker set mode @ x " + x + " y " + y);
                            LatLng position = mGoogleMap.getProjection().fromScreenLocation(new Point((int) x, (int) y));
                            AddMarker(position);
                        }
                    }
                    Log.i(TAG, "Lifted " + System.currentTimeMillis() + " long press " + long_press + " press length " + Math.abs(System.currentTimeMillis() - mPressStart));
                }
                return false;
            }
        });

        GOGATaskHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                MessageType type = (MessageType) msg.getData().getSerializable(MESSAGE_TYPE_KEY);
                Log.i(TAG, "Got handler message of type " + type.toString());
                switch (type) {
                    case BEGIN_LOAD_PLACE_DETAIL:
                        PlaceJSON place = (PlaceJSON) msg.obj;
                        //new LoadPlaceDetailsTask(getGOGAMapActivity(), place);
                        break;
                    case PLACE_IMAGE_LOADED:
                        Log.i(TAG, "Image Loaded putting into view flipper");
                        LoadPlaceDetailsTask.ImagesBundle imagesBundle = (LoadPlaceDetailsTask.ImagesBundle) msg.obj;
                        if(imagesBundle.object.getClass().equals(PlaceDetailLayout.class)) {
                            ((PlaceDetailLayout)imagesBundle.object).AddBitmap(imagesBundle.image);
                        } else {
                            Log.e(TAG, "Unrecognized layout of type " + msg.obj.getClass());
                        }
                        break;
                    case PLACE_IMAGE_LOAD_FINISHED:
                        Log.i(TAG, "Images load finished");

                        LoadPlaceDetailsTask.ImagesBundle imagesBundle1 = (LoadPlaceDetailsTask.ImagesBundle) msg.obj;
                        if(imagesBundle1.object.getClass().equals(PlaceDetailLayout.class)) {
                            ((PlaceDetailLayout)imagesBundle1.object).FinishedImageLoading();
                        } else {
                            Log.e(TAG, "Unrecognized layout of type " + msg.obj.getClass());
                        }
                        break;
                    case NO_RATING_FOUND:
                        Log.i(TAG, "No rating found");
                        if(msg.obj.getClass().equals(PlaceDetailLayout.class)) {
                            final PlaceDetailLayout layout = (PlaceDetailLayout) msg.obj;
                            layout.RemoveRating();
                        } else {
                            Log.e(TAG, "Unrecognized layout of type " + msg.obj.getClass());
                        }
                        break;
                    case EXPAND_CIRCLE:
                        //mutableCircle.setRadius();
                        mDeltaTime = ((double)System.currentTimeMillis() - (double)mPrevTime)/100d;
                        Log.i(TAG, "Set radius to: " + mMutableCircle.getRadius() + (mDeltaMeters * mDeltaTime) + " DeltaTime " + mDeltaTime + " Prev Time " + mPrevTime);
                        mMutableCircle.setRadius(mMutableCircle.getRadius() + (mDeltaMeters * mDeltaTime));

                        mPrevTime = System.currentTimeMillis();
                        break;
                }
            }
        };

        Log.i(TAG, "Database connected");

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.search_icon);
        search_icon = Bitmap.createScaledBitmap(b, 70, 70, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //AppEventsLogger.deactivateApp(this);
    }

    public class NameValuePair {
        public String Name;
        public String Value;

        public NameValuePair(String name, Object value) {
            Name = name;
            Value = value.toString();
        }

        public String getName() {
            return Name;
        }

        public String getValue() {
            return Value;
        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    @Override
    @OverridingMethodsMustInvokeSuper
    public void onBackPressed() {
        if(hiddenShown) {
            if(mMenuType == MenuType.USER_DETAIL) {
                mMenuType = MenuType.DEFAULT;
                invalidateOptionsMenu();
            }
            ShowMain();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        if(mMenuType == MenuType.DEFAULT) {
            menuInflater.inflate(R.menu.customer_home_menu, menu);
        } else if(mMenuType == MenuType.USER_DETAIL) {
            menuInflater.inflate(R.menu.user_detail_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //TODO
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastKnownLocation != null && mGoogleMap != null) {
            LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            CameraUpdate startUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 14);
            mGoogleMap.moveCamera(startUpdate);
        }
    }

    AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAutoCompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete selected: " + primaryText);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mAddPlaceMarkerCallback);

        }
    };

    ResultCallback<PlaceBuffer> mAddPlaceMarkerCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            //Add Marker
            for (Place place : places) {
                Log.i(TAG, "Place detail loaded: " + place.getName());
                //load a marker and close everything
                if (mClusterManager == null) setUpClusterer();
                XClusterItem<Place> clusterItem = new XClusterItem<>(place.getLatLng().latitude, place.getLatLng().longitude, place, search_icon);
                addToClusterManager(clusterItem);
                putToLocationObjectDictionary(place.getLatLng(), new PlaceWrapper(place));
                CameraUpdate startUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14);
                mGoogleMap.moveCamera(startUpdate);
            }
            cluster();
            HideAutoCompleteLayer();
            View view = getGOGAMapActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    };

    public void UpdateLastLocation() {
        mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    public Location getCurrentUserLastLocation() {
        if(mLastKnownLocation == null) UpdateLastLocation();
        return mLastKnownLocation;
    }

    public LatLng getCurrentUserLastLatLng() {
        return new LatLng(getCurrentUserLastLocation().getLatitude(), getCurrentUserLastLocation().getLongitude());
    }

    public double getCurrentCameraBoundsRadius() {
        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        float[] distarr = new float[1];
        Location.distanceBetween(bounds.northeast.latitude,
                bounds.northeast.longitude,
                bounds.southwest.latitude,
                bounds.southwest.longitude,
                distarr);
        float radius = distarr[0] / 2f;
        return radius;
    }

    public void MoveCamera(LatLng location, int zoom) {
        CameraUpdate startUpdate = CameraUpdateFactory.newLatLngZoom(location, zoom);
        mGoogleMap.moveCamera(startUpdate);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //TODO
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setBuildingsEnabled(true);
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.setMyLocationEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i(TAG, "On map click");

            }
        });

        //load autocomplete stuff
        Log.i(TAG, "Loading autocomplete");
        mAutoCompleteLayer = (RelativeLayout) findViewById(R.id.home_foreground_1);
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.home_autocomplete);
        mAutoCompleteTextView.setOnItemClickListener(mAutocompleteClickListener);

        mAutoCompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, mGoogleMap.getProjection().getVisibleRegion().latLngBounds, null);
        mAutoCompleteTextView.setAdapter(mAutoCompleteAdapter);
    }

    public static class MarkerItems {
        MapClusterItem mapClusterItem;
        Bitmap icon;
        LocationObject locationObject;

        public MarkerItems(MapClusterItem mapClusterItem, Bitmap icon, LocationObject locationObject) {
            this.mapClusterItem = mapClusterItem;
            this.icon = icon;
            this.locationObject = locationObject;
        }
    }
    List<MarkerItems> mQueuedMapClusterItems;

    public void consumeQueuedMapClusterItems() {
        setUpClusterer();
        if(mQueuedMapClusterItems != null) {
            for (MarkerItems item:mQueuedMapClusterItems) {
                addToClusterManager(item.mapClusterItem);
                putToLocationObjectDictionary(item.mapClusterItem.getPosition(), item.locationObject);
                redrawMarker(item.mapClusterItem.getPosition(), item.icon);
            }
        }
        mQueuedMapClusterItems.clear();
    }

    public void setUpClusterer() {
        Log.d(TAG, "Cluster Manager being setup");
        mClusterManager = new ClusterManager<MapClusterItem>(this, mGoogleMap);
        mGoogleMap.setOnCameraChangeListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mMarkerCustomRenderer = new MarkerCustomRenderer(this, mGoogleMap, mClusterManager);
        mClusterManager.setRenderer(mMarkerCustomRenderer);

        //Reset everything
        mLocationObjectJSONDictionary = new Hashtable<>();
        mClusterItemDictionary = new Hashtable<>();

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LocationObject locationObject = getLocationObjectFromDictionary(marker.getPosition());
                if (locationObject == null) {
                } else if (locationObject.getClass().equals(PlaceWrapper.class)) {
                    PlaceJSON place = ((PlaceWrapper) locationObject).convertToPlaceJSON();
                    PlaceDetailLayout detailLayout = new PlaceDetailLayout(getGOGAMapActivity(), place, getHiddenFrame());
                    new LoadPlaceDetailsTask(getGOGAMapActivity(), detailLayout, place).execute();
                    ShowHidden();
                } else if (locationObject.getClass().equals(PlaceJSON.class)) {
                    PlaceJSON place = (PlaceJSON) locationObject;
                    //clear the hidden frame
                    getHiddenFrame().removeAllViews();
                    PlaceDetailLayout detailLayout = new PlaceDetailLayout(getGOGAMapActivity(), place, getHiddenFrame());
                    new LoadPlaceDetailsTask(getGOGAMapActivity(), detailLayout, place).execute();
                    ShowHidden();
                } else {
                    Log.i(TAG, "Unknown marker location object type " + locationObject.getClass());
                }
            }
        });
    }

    public void addToClusterManager(MapClusterItem mci) {
        mClusterManager.addItem(mci);
    }

    public void addToClusterManagerWhenReady(MapClusterItem mci, LocationObject locationObject, Bitmap icon) {
        if(mClusterManager != null) {
            addToClusterManager(mci);
            putToLocationObjectDictionary(mci.getPosition(), locationObject);
            redrawMarker(mci.getPosition(), icon);
            return;
        }
        if(mQueuedMapClusterItems == null) {
            mQueuedMapClusterItems = new ArrayList<>();
        }
        mQueuedMapClusterItems.add(new MarkerItems(mci, icon, locationObject));
    }

    public boolean putToLocationObjectDictionary(LatLng position, LocationObject object) {
        if(!mLocationObjectJSONDictionary.containsKey(position)) {
            mLocationObjectJSONDictionary.put(position, object);
            return true;
        }
        return false;
    }

    public void putToClusterItemDictionary(LatLng position, MapClusterItem item) {
        mClusterItemDictionary.put(position, item);
    }

    public LocationObject getLocationObjectFromDictionary(LatLng key) {
        return mLocationObjectJSONDictionary.get(key);
    }

    public MapClusterItem getClusterItemFromDictionary(LatLng key) {
        return mClusterItemDictionary.get(key);
    }

    public void cluster() {
        mClusterManager.cluster();
    }

    public MarkerInfoWindowAdapter getMarkerInfoWindowAdapter() {
        return new MarkerInfoWindowAdapter(this, mLocationObjectJSONDictionary);
    }

    public void setInfoWindowAdapter(MarkerInfoWindowAdapter markerInfoWindowAdapter) {
        mGoogleMap.setInfoWindowAdapter(markerInfoWindowAdapter);
    }

    //@Override
    public void redrawMarker(LatLng position, Bitmap icon) {
        MarkerManager.Collection markerCollection = mClusterManager.getMarkerCollection();
        Collection<Marker> markers = markerCollection.getMarkers();
        Log.i(TAG, "Attempting to redraw marker at " + position.toString() + " " + markers.size() + " markers found on current map");
        for(Marker marker : markers) {
            if(position.equals(marker.getPosition())) {
                Log.i(TAG, "Marker for " + position + " found, replacing icon");
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            } else {
                Log.i(TAG, "Marker " + marker.getPosition() + " did not match");
            }
        }
    }

    public void redrawMarkerLater(LatLng position, Bitmap icon) {

    }

    public void redrawInfoWindow(LatLng position) {
        MarkerManager.Collection markerCollection = mClusterManager.getMarkerCollection();
        Collection<Marker> markers = markerCollection.getMarkers();
        Log.i(TAG, "Attempting to redraw marker at " + position.toString() + " " + markers.size() + " markers found on current map");
        for(Marker marker : markers) {
            if(position.equals(marker.getPosition())) {
                Log.i(TAG, "Marker for " + position + " found, replacing icon");
                if(marker.isInfoWindowShown())
                    marker.showInfoWindow();
            } else {
                Log.i(TAG, "Marker " + marker.getPosition() + " did not match");
            }
        }
    }
    public ToolBarMenu getUserDetailMenu() {
        ToolBarMenu menu = new ToolBarMenu(R.layout.actionbar_menu_item);
        menu.AddItem(getResources().getDrawable(R.drawable.chaticon), "Go to Chat Window", new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Go to chat window pressed");
            }
        });
        return menu;
    }

    /*@Override
    public void redrawMarker(MapClusterItem mapClusterItem, Bitmap icon) {
        mMarkerCustomRenderer.getMarker(mapClusterItem).setIcon(BitmapDescriptorFactory.fromBitmap(icon));
    }*/

    public void ShowAutoCompleteLayer() {
        mAutoCompleteLayer.setVisibility(View.VISIBLE);
    }

    public void HideAutoCompleteLayer() {
        mAutoCompleteLayer.setVisibility(View.GONE);
    }

    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    /*protected void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mGoogleMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }*/

    protected void changeMapType(int map_type) {
        if(mGoogleMap != null)
            mGoogleMap.setMapType(map_type);
    }


    HeatmapTileProvider mHeatmapProvider;
    TileOverlay mHeatmapTileOverlay;
    protected boolean heatMapOverlaid = false;

    protected void convertToHeatMap() {
        List<LatLng> list = new ArrayList<>();

        MarkerManager.Collection markerCollection = mClusterManager.getMarkerCollection();
        Collection<Marker> markers = markerCollection.getMarkers();

        for (Marker marker:markers) {
            list.add(marker.getPosition());
        }

        Collection<Marker> clusteredMarkerCollection = mClusterManager.getClusterMarkerCollection().getMarkers();
        for (Marker marker:clusteredMarkerCollection) {
            list.add(marker.getPosition());
        }


        if(list.size() == 0) {
            makeToast("There are no markers for a heatmap", Toast.LENGTH_LONG);
            return;
        }

        mHeatmapProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();

        mHeatmapTileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mHeatmapProvider));
    }

    public void ToggleHeatMap() {
        if(mHeatmapTileOverlay == null) {
            convertToHeatMap();
        } else {
            mHeatmapTileOverlay.remove();
            mHeatmapTileOverlay = null;
        }
    }


    public void makeToast(String msg, int length) {
        Toast.makeText(this, msg, length).show();
    }

    //Message Handlers

    public Handler getMessageHandler() {
        return GOGATaskHandler;
    }

    //HTTP based Maps access
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public String getWAK() {
        return getResources().getString(R.string.WAK);
        //return getResources().getString(R.string.WAK);
    }

    public static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    public static final String DIRECTIONS_SEARCH_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    public static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

    //helper functions

    public static Bitmap LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Bitmap b = BitmapFactory.decodeStream(is);
            //Drawable d = Drawable.createFromStream(is, "");
            return b;
        } catch (Exception ex) {
            Log.e(TAG, "Problem downloading the profile image at " + url.toString());
            ex.printStackTrace();
            return null;
        }
    }

    Dialog mShoutDialog;
    //Shoutcast
    protected void DoShout(Circle mapCircle) {
        Log.i(TAG, "Circle is being shown, dims: " + mapCircle.getCenter() + " , " + mapCircle.getRadius());
    }

    protected Marker AddMarker(LatLng position) {
        return mGoogleMap.addMarker(new MarkerOptions().position(position));
    }
}
