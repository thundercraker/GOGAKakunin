package com.example.yumashish.gogamarkethuddle.Kakunin;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yumashish.gogamarkethuddle.Connector.JSON.DirectionsJSON;
import com.example.yumashish.gogamarkethuddle.Connector.JSON.PlaceJSON;
import com.example.yumashish.gogamarkethuddle.Connector.JSON.RandomMarker;
import com.example.yumashish.gogamarkethuddle.Connector.XClusterItem;
import com.example.yumashish.gogamarkethuddle.Extensions.SwipeCardsController;
import com.example.yumashish.gogamarkethuddle.GUIAdapters.InformationTapcard;
import com.example.yumashish.gogamarkethuddle.R;
import com.example.yumashish.gogamarkethuddle.Search.GetDirectionsBetweenLocations;
import com.example.yumashish.gogamarkethuddle.Search.SearchForPlacesTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by lightning on 12/16/15.
 */
public class KakuninHomeActivity extends GOGAMapActivity {
    public static String TAG = "GOGA_KKN_MAIN";

    ProgressDialog mProgressDialog;
    RelativeLayout mForeground0, mForegroundInfo;
    SwipeCardsController mCardController;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mForegroundInfo     = (RelativeLayout) findViewById(R.id.home_foreground_info);
        mForeground0        = (RelativeLayout) findViewById(R.id.home_foreground_0);
    }

    @Override
    protected int getDrawerItemsArrayId() {
        return R.array.drawer_items;
    }

    @Override
    protected void groupSelected(View v, int groupPosition, long id) {
        switch (groupPosition) {
            case 0:
                displayCards(R.array.info_card_maps_api);
                CloseDrawer();
                break;
            case 3:
                if(mAutoCompleteLayer.getVisibility() == View.VISIBLE) HideAutoCompleteLayer();
                else ShowAutoCompleteLayer();
                CloseDrawer();
                break;
            case 8:
                displayCards(R.array.info_card_places_api);
                CloseDrawer();
                break;
            case 10:
                showKeywordSearchDialog();
                CloseDrawer();
                break;
            case 12:
                displayCards(R.array.info_card_directions_api);
                CloseDrawer();
                break;
            default:
                break;
        }
    }

    @Override
    protected void childSelected(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        switch (groupPosition) {
            case 1:
                //Map Types
                switch (childPosition) {
                    case 0:
                        //Road
                        changeMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        //Road
                        changeMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        //Road
                        changeMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                    case 3:
                        //Road
                        changeMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    default:
                        //Road
                        changeMapType(GoogleMap.MAP_TYPE_NONE);
                        break;
                }
                break;
            case 2:
                switch (childPosition) {
                    case 0:
                        //Jump to current location
                        UpdateLastLocation();
                        Location loc = getCurrentUserLastLocation();
                        MoveCamera(new LatLng(loc.getLatitude(), loc.getLongitude()), 4);
                        break;
                    case 1:
                        //Enable my location
                        if(mGoogleMap != null) {
                            mGoogleMap.setMyLocationEnabled(true);
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        }
                        break;
                }
                break;
            case 4:
                switch (childPosition) {
                    case 0:
                        //add random markers
                        ClearMarkers();
                        AddMarkers(30);
                        break;
                    case 1:
                        //Customize markers
                        CustomizeMarkers();
                        break;
                    case 2:
                        //Marker Clustering
                        ClusterMarkers();
                        break;
                    case 3:
                        //Clear everything
                        ClearMarkers();
                        break;
                }
                break;
            case 5:
                switch (childPosition) {
                    case 0:
                        //Add a line
                        ClearMarkers();
                        AddLine();
                        break;
                    case 1:
                        //Add a shape
                        ClearMarkers();
                        AddPolygon();
                        AddPolygon();
                        AddPolygon();
                        AddPolygon();
                        AddPolygon();
                        ClearMarkers();
                        break;
                    case 2:
                        ClearPoly();
                        break;
                }
                break;
            case 7:
                switch (childPosition) {
                    case 0:
                        //add heatmap
                        AddHeatMap();
                        break;
                    case 1:
                        ClearTileOverlays();
                        break;
                }
                break;
            case 9:
                List<String> search = new ArrayList<>();
                switch (childPosition) {
                    case 0:
                        //places by Clothes
                        ClearMarkers();
                        search.add("clothing_store");
                        SearchForType(search, true, true);
                        break;
                    case 1:
                        //places by Electronics
                        ClearMarkers();
                        search.add("electronics_store");
                        SearchForType(search, true, true);
                        break;
                    case 2:
                        //places by ATM
                        ClearMarkers();
                        search.add("atm");
                        SearchForType(search, true, true);
                        break;
                }
                break;
            case 11:
                List<String> search11 = new ArrayList<>();
                switch (childPosition) {
                    case 0:
                        //Search restaurants by Stars
                        //Star info windows
                        ClearMarkers();
                        search11.add("restaurant");
                        SearchForType(search11, true, true);
                        break;
                    case 1:
                        ClearMarkers();

                        //Search by stars premium
                        break;
                }
                break;
            case 14:
                switch (childPosition) {
                    case 0:
                        //Normal
                        ClearMarkers();
                        requiredMarkerMode(2, false);
                        break;
                    case 1:
                        ClearMarkers();
                        requiredMarkerMode(2, true);
                        break;
                }
                break;
            default:
                switch (childPosition) {
                    default:
                        break;
                }
                break;
        }
        CloseDrawer();
    }

    void showProgressDialog(String title, String message) {
        mProgressDialog = ProgressDialog.show(this, title, message);
    }

    void dissmissProgressDialog() {
        mProgressDialog.dismiss();
    }

    //!------------------------------------------------------------------------+
    //!                          Card Displays                                 |
    //!------------------------------------------------------------------------+
    void displayCards(int card_list_resource_id) {
        TypedArray array = getResources().obtainTypedArray(card_list_resource_id);
        List<SwipeCardsController.TapCardInterface> tapcards = new ArrayList<>();
        Log.i(TAG, "Loaded resources : " + array.length());
        for(int i = 0; i < array.length(); i++) {
            //int id = array.getResourceId(i, 0);
            //String[] itemArray = getResources().getStringArray(id);
            //Log.i(TAG, "Item Array: " + itemArray.length);

            CharSequence[] items = array.getTextArray(i);
            Log.i(TAG, "Items: " + items.length);
            String title = items[0].toString();
            String infoString = items[1].toString();
            CharSequence info = (infoString.charAt(0) == '@') ? getResources().getText(getResources().getIdentifier(infoString.replace("@string/", ""), "string", getPackageName())) : infoString;
            Drawable image = (items[2].toString().length() < 1) ? null :
                    getResources().getDrawable(getResources().getIdentifier(items[2].toString().replace("@drawable/", ""), "drawable", getPackageName()));
            tapcards.add(new InformationTapcard(title, info, image));
        }
        mForegroundInfo.setVisibility(View.VISIBLE);
        mBlockMapTouch = true;
        mCardController = new SwipeCardsController(this, mForegroundInfo, tapcards);
        mCardController.SetOnFinishHandler(new SwipeCardsController.OnFinished() {
            @Override
            public void onFinish() {
                mForegroundInfo.removeAllViews();
                mForegroundInfo.setVisibility(View.GONE);
                mBlockMapTouch = false;
                SwipeCardsController.ID_UNIQUE = 0;
            }
        });
        array.recycle();
    }

    //!------------------------------------------------------------------------+
    //!                          Marker Options                                |
    //!------------------------------------------------------------------------+
    List<Marker> mAllActiveMarkers = new ArrayList<>();

    void AddMarkers(int totalMarkers) {
        ClearMarkers();
        mAllActiveMarkers = new ArrayList<>();

        //Get bounds in camera
        LatLngBounds screenBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

        //if(mClusterManager == null) setUpClusterer();

        for(int i = 0; i < totalMarkers; i++) {
            double maxlat = Math.max(screenBounds.northeast.latitude, screenBounds.southwest.latitude);
            double minlat = Math.min(screenBounds.northeast.latitude, screenBounds.southwest.latitude);
            double randlat = (Math.random() * (maxlat - minlat)) + minlat;

            double maxlng = Math.max(screenBounds.northeast.longitude, screenBounds.southwest.longitude);
            double minlng = Math.min(screenBounds.northeast.longitude, screenBounds.southwest.longitude);
            double randlng = (Math.random() * (maxlng - minlng)) + minlng;

            mAllActiveMarkers.add(mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(randlat, randlng)).title(getString(R.string.random_marker_title) + " " + i)));
            Log.d(TAG, "Adding marker @ " + new LatLng(randlat, randlng) + ", total markers: " + mAllActiveMarkers.size());
        }

    }

    void ClearMarkers() {
        //delete all old markers
        for (Marker marker:mAllActiveMarkers) {
            marker.remove();
        }
        mAllActiveMarkers = new ArrayList<>();
        ClearCluster();

    }

    void CustomizeMarkers() {
        if(mAllActiveMarkers == null) {
            Toast.makeText(this, "There are no markers to customize", Toast.LENGTH_LONG);
            return;
        }

        for(Marker marker: mAllActiveMarkers) {
            int randNum = (int) Math.floor(Math.random() * 4);
            Drawable icon;
            switch (randNum) {
                case 0:
                    icon = getResources().getDrawable(R.drawable.chaticon);
                    break;
                case 1:
                    icon = getResources().getDrawable(R.drawable.fire_ball_icon_hi);
                    break;
                case 2:
                    icon = getResources().getDrawable(R.drawable.search_icon);
                    break;
                case 3:
                    icon = getResources().getDrawable(R.drawable.goga_logo);
                    break;
                default:
                    icon = getResources().getDrawable(R.drawable.chatsend);
                    break;
            }
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(Resize(icon, 60, 60)));
        }

    }

    void ChangeInfoWindows() {
        if(mAllActiveMarkers == null) {
            Toast.makeText(this, "There are no markers to add info windows to", Toast.LENGTH_LONG);
            return;
        }

        for(Marker marker: mAllActiveMarkers) {
            putToLocationObjectDictionary(marker.getPosition(), new RandomMarker(marker));
        }

        setInfoWindowAdapter(getMarkerInfoWindowAdapter());
    }

    void ClearCluster() {
        if(mClusterManager != null) {
            MarkerManager.Collection collection;
            collection = mClusterManager.getClusterMarkerCollection();
            for (Marker marker : collection.getMarkers())
                marker.remove();

            collection = mClusterManager.getMarkerCollection();
            for (Marker marker : collection.getMarkers())
                marker.remove();

            mClusterManager.clearItems();
        }
    }

    void ClusterMarkers() {
        if(mAllActiveMarkers == null) {
            Toast.makeText(this, "There are no markers to customize", Toast.LENGTH_LONG);
            return;
        }

        if(mClusterManager == null) setUpClusterer();

        for(Marker marker: mAllActiveMarkers) {
            XClusterItem<Void> newClusterItem = new XClusterItem<>(marker.getPosition().latitude, marker.getPosition().longitude, null, null);
            mClusterManager.addItem(newClusterItem);
            marker.setVisible(false);
        }

        mClusterManager.cluster();
    }

    //!-------------------------------------------------------------------------+
    //!                          Shape Options                                  |
    //!-------------------------------------------------------------------------+
    List<Polyline>  mAllPolyLines = new ArrayList<>();
    List<Polygon>   mAllPolygon = new ArrayList<>();

    void TheWeb(Marker source, List<Marker> markersOtherThanSource) {
        HashSet<Marker> markerSet = new HashSet<>();
        Random rand = new Random(System.currentTimeMillis());

        for (Marker marker : markersOtherThanSource) {
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            mAllPolyLines.add(mGoogleMap.addPolyline(new PolylineOptions()
                    .add(source.getPosition(), marker.getPosition())
                    .width(10)
                    .color(Color.rgb(r, g, b))));
        }
    }

    double distance(Marker m1, Marker m2) {
        LatLng a = m1.getPosition(), b = m2.getPosition();
        return Math.sqrt(Math.pow(a.latitude - b.latitude, 2)
                + Math.pow(a.longitude - b.longitude, 2));
    }

    void AddLine() {
        AddMarkers(30);
        List<Marker> allMarkers = new ArrayList<>();
        allMarkers.addAll(mAllActiveMarkers);
        Marker source = allMarkers.get(allMarkers.size() - 1);
        allMarkers.remove(source);
        TheWeb(source, allMarkers);
    }

    void AddPolygon() {
        AddMarkers(3);
        PolygonOptions polyOptions = new PolygonOptions();

        List<Marker> allMarkers = new ArrayList<>();
        allMarkers.addAll(mAllActiveMarkers);
        Marker source = allMarkers.get(allMarkers.size() - 1);
        allMarkers.remove(source);
        for(LatLng position : TheTraveller(source, allMarkers)) {
            polyOptions.add(position);
        }

        Random rand = new Random(System.currentTimeMillis());
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        polyOptions.fillColor(Color.rgb(r,g,b));

        r = rand.nextInt(255);
        g = rand.nextInt(255);
        b = rand.nextInt(255);
        polyOptions.strokeColor(Color.rgb(r, g, b));
        polyOptions.geodesic(true);

        mAllPolygon.add(mGoogleMap.addPolygon(polyOptions));
    }

    void ClearPoly() {
        for(Polyline line : mAllPolyLines) {
            line.remove();
        }

        for(Polygon poly : mAllPolygon) {
            poly.remove();
        }

        ClearMarkers();
    }

    //!-------------------------------------------------------------------------+
    //!                          Tile Options                                   |
    //!-------------------------------------------------------------------------+
    List<TileOverlay> mAllTileOverlays = new ArrayList<>();

    void ClearTileOverlays() {
        for (TileOverlay overlay : mAllTileOverlays) {
            overlay.remove();
        }
        mAllTileOverlays = new ArrayList<>();
    }

    void AddHeatMap() {
        AddMarkers(30);
        new QuickAsyncTask<Void, Void, Void>(new QuickAsyncTask.QuickTask<Void, Void>() {
            @Override
            public void Pre() {
                showProgressDialog("Loading Heatmaps", "Calculating heatmaps");
            }

            @Override
            public Void Do(Void... params) {
                try {
                    Thread.sleep(2000, 0);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted Exception");
                }
                return null;
            }

            @Override
            public void Post(Void param) {
                for (Marker marker : mAllActiveMarkers) {
                    marker.setVisible(false);
                }
                convertMarkersToHeatMap(mAllActiveMarkers);
                ClearMarkers();
                dissmissProgressDialog();
            }
        }).execute();
    }

    HeatmapTileProvider mHeatmapProvider;
    TileOverlay mHeatmapTileOverlay;

    protected void convertMarkersToHeatMap(List<Marker> markers) {
        List<LatLng> list = new ArrayList<>();

        for(Marker marker : markers) {
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
        mAllTileOverlays.add(mHeatmapTileOverlay);
    }

    //!-------------------------------------------------------------------------+
    //!                          Search Options                                 |
    //!-------------------------------------------------------------------------+
    List<PlaceJSON> SearchForType(List<String> placeTypes, final boolean consume, final boolean dismissOnFinish) {


        StringBuilder typesBuilder = new StringBuilder();
        typesBuilder.append(placeTypes.get(0));
        for (int i = 1; i < placeTypes.size(); i++) {
            typesBuilder.append("|" + placeTypes.get(i));
        }
        Log.i(TAG, "Searching for types " + typesBuilder.toString());
        UpdateLastLocation();
        SearchForPlacesTask searchForPlacesTask =
                new SearchForPlacesTask(this, null, null, getCurrentUserLastLatLng(), getCurrentCameraBoundsRadius(), typesBuilder.toString(),
                        new QuickAsyncTask.QuickTask<Void, Void>() {
                            @Override
                            public void Pre() {
                                showProgressDialog("Searching...", "Searching Google Places");
                            }

                            @Override
                            public Void Do(Void... params) {
                                return null;
                            }

                            @Override
                            public void Post(Void param) {
                                if(dismissOnFinish)
                                    dissmissProgressDialog();
                            }
                        });
        searchForPlacesTask.setConsume(consume);
        searchForPlacesTask.execute();
        return searchForPlacesTask.getPlaceJSONItems();
    }

    void showKeywordSearchDialog() {
        final Dialog KeywordDialog = new Dialog(this);
        KeywordDialog.setContentView(R.layout.shout_message_dialog);

        final EditText keyword = (EditText) KeywordDialog.findViewById(R.id.smd_editext);
        Button search = (Button) KeywordDialog.findViewById(R.id.smd_broadcast);
        Button cancel = (Button) KeywordDialog.findViewById(R.id.smd_cancel);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateLastLocation();
                SearchForPlacesTask searchForPlacesTask =
                        new SearchForPlacesTask(getGOGAMapActivity(), null, keyword.getText().toString(), getCurrentUserLastLatLng(), getCurrentCameraBoundsRadius(), null,
                                new QuickAsyncTask.QuickTask<Void, Void>() {
                                    @Override
                                    public void Pre() {
                                        mProgressDialog = ProgressDialog.show(getGOGAMapActivity(), "Searching by Keyword", "Searching Google Places...");
                                    }

                                    @Override
                                    public Void Do(Void... params) {
                                        return null;
                                    }

                                    @Override
                                    public void Post(Void param) {
                                        mProgressDialog.dismiss();
                                    }
                                });
                searchForPlacesTask.execute();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeywordDialog.dismiss();
            }
        });

        KeywordDialog.show();
    }

    void showRatingSelectorDialog() {

    }

    //!-------------------------------------------------------------------------+
    //!                          Directions API                                 |
    //!-------------------------------------------------------------------------+
    private int mPlantedMarkers = 0, mRequiredMarkers = 0;
    private boolean requiredMarkerModeOn = false;
    private boolean orGreaterMode, orGreaterFulfilled = false;
    private TextView mPlantedMarkersText;
    private Button mPlantedMarkersDone;

    void updatePlantedMarkersText() {
        String x = (orGreaterMode) ? getString(R.string.fsmm_text_label_or_greater) : getString(R.string.fsmm_text_label);
        Log.d(TAG, x);
        int markersNeeded = mRequiredMarkers - mPlantedMarkers;
        String repText = (markersNeeded > 0) ? x.replace("$", "" + markersNeeded) : getString(R.string.fsmm_text_label_done);
        mPlantedMarkersText.setText(repText);
    }

    void requiredMarkerMode(final int requiredMarkers, boolean orGreater) {
        mRequiredMarkers = requiredMarkers;
        orGreaterMode = orGreater;
        requiredMarkerModeOn = true;
        mCurrentMapMode = MapMode.SET_MARKER;
        //Add a large done button on foreground 0
        if(orGreater) {
            orGreaterFulfilled = false;
            View foreground0_fsmm = getLayoutInflater().inflate(R.layout.foreground_set_marker_mode, mForeground0);
            mPlantedMarkersText = (TextView) findViewById(R.id.fsmm_text);
            mPlantedMarkersDone = (Button) findViewById(R.id.fsmm_done);
            mPlantedMarkersDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlantedMarkers >= requiredMarkers - 1)
                        orGreaterFulfilled = true;
                    else
                        makeToast("You need to set at least " + requiredMarkers + " markers", Toast.LENGTH_LONG);
                }
            });
            updatePlantedMarkersText();
            foreground0_fsmm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Marker AddMarker(LatLng position) {
        if(requiredMarkerModeOn) {
            MarkerOptions newMarker = null;

            if(mPlantedMarkers == 0) {
                //First Marker, make it special .. TODO later
                newMarker = new MarkerOptions().title(getString(R.string.fsmm_start)).position(position);
            } else {
                newMarker = new MarkerOptions().title("Location number " + mPlantedMarkers).position(position);
            }
            mPlantedMarkers++;
            if ((orGreaterMode && orGreaterFulfilled) || (!orGreaterMode && (mPlantedMarkers == mRequiredMarkers))) {
                //this is the last marker reset everything and remove the stuff in the foreground
                newMarker.title(getString(R.string.fsmm_end));
                final boolean orGreater = orGreaterMode;

                Marker marker = mGoogleMap.addMarker(newMarker);
                mAllActiveMarkers.add(marker);

                mCurrentMapMode = MapMode.NONE;
                requiredMarkerModeOn = false;
                orGreaterMode = false;
                mRequiredMarkers = 0;
                mPlantedMarkers = 0;

                if(orGreater) {
                    mForeground0.removeAllViews();
                    mForeground0.setVisibility(View.GONE);
                }
                AddedAllMarkers(orGreater);
                return marker;
            }

            if (orGreaterMode) {
                updatePlantedMarkersText();
            }

            Marker marker = mGoogleMap.addMarker(newMarker);
            mAllActiveMarkers.add(marker);
            return marker;
        }

        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(position));
        mAllActiveMarkers.add(marker);
        return marker;
    }

    void AddedAllMarkers(final boolean waypoint) {
        //All markers added
        Log.i(TAG, "Add markers have been added " + waypoint);
        Marker startMarker  = getMarkerByNameStartsWith(getString(R.string.fsmm_start));
        Marker endMarker    = getMarkerByNameStartsWith(getString(R.string.fsmm_end));
        List<Marker> allMarkers = new ArrayList<>(mAllActiveMarkers);
        allMarkers.remove(startMarker);
        allMarkers.remove(endMarker);
        List<String> waypoints = new ArrayList<>();
        if(waypoint) {
            //Geocode every other point
            Geocoder coder = new Geocoder(this);
            for (Marker marker : allMarkers) {
                try {
                    List<Address> markerLocations = coder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                    if(markerLocations.size() > 0) {
                        String address = "";
                        int i = 0;
                        for(; i < markerLocations.get(0).getMaxAddressLineIndex(); i++) {
                            address += markerLocations.get(0).getAddressLine(i) + ", ";
                        }
                        if(i > 0) {
                            address = address.substring(0, address.length() - 2);
                        }
                        waypoints.add(address);
                        Log.i(TAG, "Address: " + address + " Full: " + markerLocations.get(0).toString());
                    }
                } catch (IOException ioe) {
                    Log.e(TAG, ioe.getMessage());
                    ioe.printStackTrace();
                }
            }
        }
        final GetDirectionsBetweenLocations task = new GetDirectionsBetweenLocations(this, startMarker.getPosition(), endMarker.getPosition(),
                GetDirectionsBetweenLocations.TravelMode.TRANSIT, false);
        task.setWaypoints(waypoints);
        task.setQuickie(new QuickAsyncTask.QuickTask<Void, DirectionsJSON>() {
            @Override
            public void Pre() {
                mProgressDialog = ProgressDialog.show(getGOGAMapActivity(), "Searching", "Using Google Directions API...");
            }

            @Override
            public DirectionsJSON Do(Void... params) {
                return null;
            }

            @Override
            public void Post(DirectionsJSON directions) {
                if(directions != null) {
                    int routes_cnt = 0;
                    for (; routes_cnt < directions.routes.size(); routes_cnt++) {
                        DirectionsJSON.Route route = directions.routes.get(routes_cnt);
                        List<LatLng> polyLine = route.getDecodedPoly();
                        drawPolyLines(polyLine);
                        break;
                    }
                }
                mProgressDialog.dismiss();
            }
        });
        task.execute();
    }

    void drawPolyLines(List<LatLng> polyLine) {
        if(polyLine == null || polyLine.size() < 1)
            return;
        int index = 0;
        while(index < polyLine.size()) {
            LatLng source = polyLine.get(index++);
            LatLng dest = null;
            if(index >= polyLine.size()) {
                return;
            }
            dest = polyLine.get(index);

            Random rand = new Random(System.currentTimeMillis());
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            mAllPolyLines.add(mGoogleMap.addPolyline(new PolylineOptions().add(source, dest).color(Color.rgb(r, g, b))));
        }
    }

    Marker getMarkerByNameStartsWith(String search) {
        Marker foundMarker = null;
        for(Marker marker : mAllActiveMarkers) {
            if(marker.getTitle().indexOf(search) == 0) {
                foundMarker = marker;
            }
        }
        return foundMarker;
    }

    //Helpers
    public static Bitmap Resize(Drawable image, int height, int width) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, height, width, false);
        return bitmapResized;
    }

    List<LatLng> TheTraveller(Marker source, List<Marker> markersOtherThanSource) {
        List<LatLng> polyPoints = new ArrayList<>();
        Marker originalSource = source;
        polyPoints.add(source.getPosition());
        HashSet<Marker> markerSet = new HashSet<>();

        for (Marker marker : markersOtherThanSource) {
            markerSet.add(marker);
        }

        while(!markerSet.isEmpty()) {
            Marker closest = null;
            double dist = Double.POSITIVE_INFINITY;
            for(Marker marker : markerSet) {
                //calc distance
                double markerDistance = distance(source, marker);
                if(markerDistance < dist) {
                    dist = markerDistance;
                    closest = marker;
                }
            }

            if(closest != null) {
                //draw a line between the source and the closest
                Random rand = new Random(System.currentTimeMillis());
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);

                polyPoints.add(closest.getPosition());

                //remove the closest from the markerSet;
                markerSet.remove(closest);
                source = closest;
            } else {
                Log.e(TAG, "No closest node found. Impossible condition");
                return new ArrayList<>();
            }
        }
        polyPoints.add(originalSource.getPosition());
        return polyPoints;
    }
}
