package com.yumashish.kakunin;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.kml.KmlLayer;
import com.yumashish.kakunin.External.GrahamScan;
import com.yumashish.kakunin.External.LSObject;
import com.yumashish.kakunin.External.LocationSearchHelper;
import com.yumashish.kakunin.External.Point2D;
import com.yumashish.kakunin.External.SimpleLocationObject;
import com.yumashish.kakunin.External.SimpleOpeningAndClosingParser;
import com.yumashish.kakunin.External.StaticDistanceClusterer;
import com.yumashish.kakunin.GUI.DirectionDetailWindow;
import com.yumashish.kakunin.GUI.FBPhotoListAdapter;
import com.yumashish.kakunin.GUI.FBPhotoTapCard;
import com.yumashish.kakunin.GUI.Maps.MapClusterItem;
import com.yumashish.kakunin.GUI.Maps.MarkerInfoWindowAdapter;
import com.yumashish.kakunin.Interfaces.SimpleAction;
import com.yumashish.kakunin.JSON.DirectionsJSON;
import com.yumashish.kakunin.JSON.LoadLocationObject;
import com.yumashish.kakunin.JSON.PlaceJSON;
import com.yumashish.kakunin.JSON.RandomMarker;
import com.yumashish.kakunin.GUI.Maps.XClusterItem;
import com.yumashish.kakunin.GUI.SwipeCardsController;
import com.yumashish.kakunin.GUI.InformationTapcard;
import com.yumashish.kakunin.Tasks.GetDirectionsBetweenLocations;
import com.yumashish.kakunin.Tasks.ImageLoadedHandler;
import com.yumashish.kakunin.Tasks.LoadImageTask;
import com.yumashish.kakunin.Tasks.SearchForPlacesTask;
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
import com.yumashish.yumamateriallistview.YumaMaterialListView;
import com.yumashish.yumamateriallistview.YumaMaterialListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.acl.AclEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Created by lightning on 12/16/15.
 */
public class KakuninHomeActivity extends GOGAMapActivity {
    public static String TAG = "GOGA_KKN_MAIN";

    RelativeLayout mForeground0, mForegroundInfo, mForegroundMaster;
    SwipeCardsController mCardController;
    QuickAsyncTask<Void, Integer, Object> mForegroundTask;
    String mTitleString;
    boolean mFirstStreetViewSelect = true, mFirstGeocode = true;
    ViewGroup mSpecialView;

    CallbackManager mCallbackManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "KakuninHome Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.yumashish.kakunin/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "KakuninHome Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.yumashish.kakunin/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    enum ForegroundMode {NONE, SEARCH, AUTOCOMPLETE, INFO, DIRECTION_DETAIL, STORE_LOCATION}


    ForegroundMode foregroundMode;
    ViewTreeObserver mDrawerVTO;
    ViewTreeObserver.OnGlobalLayoutListener mGLL;
    View mSignInView = null;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        FacebookSdk.sdkInitialize(this);
        mCallbackManager = CallbackManager.Factory.create();

        mDrawerVTO = getDrawerListView().getViewTreeObserver();
        mGLL = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i(TAG, "Drawer finished loading");
                mSpecialView = (ViewGroup) ((YumaMaterialListViewAdapter) getDrawerListView().getExpandableListAdapter()).getSpecialView();
                //see if logged in
                if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
                    Log.i(TAG, "User is logegd in");
                    //already logged in
                    FBLoginSuccess();
                } else {
                    Log.i(TAG, "User is not logegd in");
                    //not logged in
                    LoginTopView();
                }


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getDrawerListView().getViewTreeObserver().removeGlobalOnLayoutListener(mGLL);
                } else {
                    getDrawerListView().getViewTreeObserver().removeOnGlobalLayoutListener(mGLL);
                }
            }
        };
        mDrawerVTO.addOnGlobalLayoutListener(mGLL);

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                FBLoginSuccess();
            }

            @Override
            public void onCancel() {
                LoginTopView();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, error.getMessage());
                error.printStackTrace();
                LoginTopView();
            }
        });

        mForegroundInfo = (RelativeLayout) findViewById(R.id.home_foreground_info);
        mForeground0 = (RelativeLayout) findViewById(R.id.home_foreground_0);
        mForegroundMaster = (RelativeLayout) findViewById(R.id.home_master_foreground);
        mTitleString = getTitle().toString();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    void LoginTopView() {
        //setup sign in button and options
        mSpecialView.removeAllViews();
        ViewGroup.LayoutParams params = mSpecialView.getLayoutParams();
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        mSpecialView.requestLayout();

        getLayoutInflater().inflate(R.layout.drawer_top_sign_in, mSpecialView, true);
        Button signin = (Button) mSpecialView.findViewById(R.id.dtv_sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getGOGAMapActivity(),
                        Arrays.asList("public_profile", "user_friends", "user_photos", "user_status", "user_posts"));
            }
        });
    }

    int mFinishedFbDetailDownload;
    void FBLoginSuccess() {
        mFinishedFbDetailDownload = 0;

        mSpecialView.removeAllViews();
        ViewGroup.LayoutParams params = mSpecialView.getLayoutParams();
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
        mSpecialView.requestLayout();
        getGOGAMapActivity().getLayoutInflater().inflate(R.layout.drawer_top_fb_details, mSpecialView, true);

        GraphRequest graphRequest = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + AccessToken.getCurrentAccessToken().getUserId(),
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        Log.i(TAG, "Response: " + response.getRawResponse());
                        JSONObject obj = response.getJSONObject();

                        if(obj != null) {
                            try {
                                ((TextView) mSpecialView.findViewById(R.id.dtfb_name)).setText(obj.getString("name"));
                                String sub = (obj.has("location")) ? obj.getString("location") :
                                        ((obj.has("email")) ? obj.getString("email") : "");
                                ((TextView) mSpecialView.findViewById(R.id.dtfb_user_sub)).setText(sub);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.i(TAG, "Profile Info has been loaded. "  + mFinishedFbDetailDownload);
                            if (mFinishedFbDetailDownload > 0) {
                                View hider = mSpecialView.findViewById(R.id.dtfb_hider);
                                ((ViewGroup) hider.getParent()).removeView(hider);
                            } else {
                                mFinishedFbDetailDownload++;
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, email, location");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

        new QuickAsyncTask<Void, Void, Bitmap>(new QuickAsyncTask.QuickTask<Void, Bitmap>() {
            @Override
            public void Pre() {
                //nothing
            }

            @Override
            public Bitmap Do(Void... params) {
                try {
                    URL imageURL = new URL("https://graph.facebook.com/" + AccessToken.getCurrentAccessToken().getUserId() + "/picture?type=large");
                    Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    return bitmap;
                } catch (MalformedURLException mfe) {
                    Log.e(TAG, mfe.getMessage());
                    mfe.printStackTrace();
                    return null;
                }catch (IOException mfe) {
                    Log.e(TAG, mfe.getMessage());
                    mfe.printStackTrace();
                    return null;
                }
            }

            @Override
            public void Post(Bitmap result) {
                if(result != null) {
                    ((ImageView)mSpecialView.findViewById(R.id.dtfb_profile_image)).setImageBitmap(result);
                }

                Log.i(TAG, "Profile Image has been loaded. "  + mFinishedFbDetailDownload);
                if(mFinishedFbDetailDownload > 0) {
                    View hider = mSpecialView.findViewById(R.id.dtfb_hider);
                    ((ViewGroup) hider.getParent()).removeView(hider);
                } else {
                    mFinishedFbDetailDownload++;
                }
            }
        }).execute();

        ((Button) mSpecialView.findViewById(R.id.dtfb_photomap)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLoadPhotos();
                CloseDrawer();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    void setForegroundMode(ForegroundMode mode) {
        //unset current mode
        if (foregroundMode != ForegroundMode.NONE)
            unsetForegroundMode(foregroundMode);
        if (mode == ForegroundMode.INFO) {
            mForegroundInfo.setVisibility(View.VISIBLE);
            mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
        } else if (mode == ForegroundMode.SEARCH) {
            mForeground0.setVisibility(View.VISIBLE);
        } else if (mode == ForegroundMode.AUTOCOMPLETE) {
            mAutoCompleteLayer.setVisibility(View.VISIBLE);
        } else if (mode == ForegroundMode.INFO) {
            mForegroundInfo.setVisibility(View.VISIBLE);
        } else if (mode == ForegroundMode.DIRECTION_DETAIL
                || mode == ForegroundMode.STORE_LOCATION) {
            final float scale = getResources().getDisplayMetrics().density;
            final int px = (int) (60 * scale + 0.5f);
            mSlider.setPanelHeight(px);
            //mSlider.setEnabled(true);
            mGoogleMap.setPadding(0, px, 0, px);
        }
        foregroundMode = mode;
    }

    void unsetForegroundMode(ForegroundMode mode) {
        if (mode == ForegroundMode.SEARCH) {
            mForeground0.removeAllViews();
            mForeground0.setVisibility(View.GONE);
        } else if (mode == ForegroundMode.INFO) {
            mForegroundInfo.removeAllViews();
            mForegroundInfo.setVisibility(View.GONE);
            mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
        } else if (mode == ForegroundMode.AUTOCOMPLETE) {
            mAutoCompleteLayer.setVisibility(View.GONE);
        } else if (mode == ForegroundMode.INFO) {
            mForegroundInfo.removeAllViews();
            mForegroundInfo.setVisibility(View.GONE);
        } else if (mode == ForegroundMode.DIRECTION_DETAIL
                || mode == ForegroundMode.STORE_LOCATION) {
            mSlider.setPanelHeight(0);
            mSliderPanel.removeAllViews();
            //mSlider.setEnabled(false);
            mGoogleMap.setPadding(0, 0, 0, 0);
        }
        setToolbarTitle(getTitle().toString());
        foregroundMode = ForegroundMode.NONE;
    }

    @Override
    public void onBackPressed() {
        if(!hiddenShown) {
            if (foregroundMode != ForegroundMode.NONE) {
                unsetForegroundMode(foregroundMode);
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void ShowMain() {
        //make drawer visible
        if(foregroundMode == ForegroundMode.DIRECTION_DETAIL || foregroundMode == ForegroundMode.STORE_LOCATION) {
            final float scale = getResources().getDisplayMetrics().density;
            final int px = (int) (60 * scale + 0.5f);
            mSlider.setPanelHeight(px);
        }
        super.ShowMain();
    }

    @Override
    public void ShowHidden() {
        if(foregroundMode == ForegroundMode.DIRECTION_DETAIL || foregroundMode == ForegroundMode.STORE_LOCATION) {
            //hide the drawer
            mSlider.setPanelHeight(0);
        }
        super.ShowHidden();
    }

    CharSequence getComposite(CharSequence main, CharSequence sub) {
        return (main != null && main.length() > 0) ?
                    (sub != null && sub.length() > 0) ? main + " :: " + sub : main
                : (sub != null && sub.length() > 0) ? sub : getTitle().toString();
    }

    CharSequence getFullTitle(int menu_id) {
        CharSequence[] seperatorAndTitle = getDrawerListView().getSeperatorAndTitle(menu_id);
        if(seperatorAndTitle != null && seperatorAndTitle.length > 1) {
            return getComposite(seperatorAndTitle[0], seperatorAndTitle[1]);
        } else {
            Log.e(TAG, "Incorrect title return for " + getResources().getResourceName(menu_id));
            return getTitle();
        }
    }

    protected boolean groupLongPress(View p, View v, int position, long id) {
        switch (position) {
            case 0:
                displayCards(R.array.info_card_maps_api);
                CloseDrawer();
                return true;
            case 8:
                displayCards(R.array.info_card_places_api);
                CloseDrawer();
                return true;
            case 12:
                displayCards(R.array.info_card_directions_api);
                CloseDrawer();
                return true;
            case 3:
                displayCards(R.array.info_card_autocomplete);
                CloseDrawer();
                return true;
            case 4:
                displayCards(R.array.info_card_markers);
                CloseDrawer();
                return true;
            case 5:
                displayCards(R.array.info_card_shapes);
                CloseDrawer();
                return true;
            case 6:
                displayCards(R.array.info_card_ground_overlay);
                CloseDrawer();
                return true;
            case 7:
                displayCards(R.array.info_card_tile_overlay);
                CloseDrawer();
                return true;
            case 15:
                //showAboutDeveloper();
                //TODO streetview cards
                CloseDrawer();
                return true;
            default:
                return true;
        }
    }

    @Override
    protected boolean onMenuItemPressed(int id) {
        String title = "";
        List<String> search = new ArrayList<>();
        boolean isOpenableGroup = false;
        switch (id) {
            case R.id.menu_map_type:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_map_type_title);
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.map_types), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String map_type = getTitle().toString();
                        switch (which) {
                            case 0:
                                //Road
                                changeMapType(GoogleMap.MAP_TYPE_NORMAL);
                                map_type = getString(R.string.map_type_none);
                                break;
                            case 1:
                                //Road
                                changeMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                map_type = getString(R.string.map_type_satellite);
                                break;
                            case 2:
                                //Road
                                changeMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                map_type = getString(R.string.map_type_terrain);
                                break;
                            case 3:
                                //Road
                                changeMapType(GoogleMap.MAP_TYPE_HYBRID);
                                map_type = getString(R.string.map_type_hybrid);
                                break;
                            default:
                                //Road
                                changeMapType(GoogleMap.MAP_TYPE_NONE);
                                break;
                        }
                        setToolbarTitle(getComposite(getString(R.string.title_maps_api), map_type).toString());
                    }
                });
                builder.setNegativeButton(getString(R.string.dialog_cancel), null);
                builder.show();
                break;
            case R.id.menu_geocoding:
                if (mFirstGeocode) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setTitle(getString(R.string.dialog_title_info));
                    builder1.setMessage(getString(R.string.dialog_message_gc));
                    builder1.setPositiveButton(getString(R.string.dialog_okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }
                    });
                    builder1.show();
                    mFirstGeocode = false;
                }
                mCurrentMapMode = MapMode.SELECT_GEOCODE;
                title = getString(R.string.title_geocoding);
                break;
            case R.id.menu_my_location:
                Location loc;
                if(UpdateLastLocation() && (loc = getCurrentUserLastLocation()) != null) {
                    MoveCamera(new LatLng(loc.getLatitude(), loc.getLongitude()), 10);

                    if (mGoogleMap != null) {
                        mGoogleMap.setMyLocationEnabled(true);
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    }
                    title = getString(R.string.title_my_location);
                } else {
                    AlertDialog.Builder b1 = new AlertDialog.Builder(this);
                    b1.setTitle(getString(R.string.dialog_error));
                    b1.setMessage(getString(R.string.msg_location_not_enabled));
                    b1.setPositiveButton(getString(R.string.dialog_okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }
                    });
                    b1.show();
                }
                break;
            case R.id.menu_add_random_markers:
                //add random markers
                ClearMarkers();
                AddMarkers(30);
                title = getString(R.string.title_added_random_marker);
                break;
            case R.id.menu_customize_markers:
                //Customize markers
                CustomizeMarkers();
                title = getString(R.string.title_custom_markers);
                break;
            case R.id.menu_clustering_markers:
                //Marker Clustering
                ClusterMarkers();
                title = getString(R.string.title_markers_clustered);
                break;
            case R.id.menu_clear_markers:
                //Clear everything
                ClearMarkers();
                title = null;
                break;
            case R.id.menu_add_polylines:
                //Add a line
                ClearMarkers();
                AddLine();
                title = getString(R.string.title_added_polylines);
                break;
            case R.id.menu_add_polyshapes:
                //Add a shape
                ClearMarkers();
                selectPolygonN();
                title = getString(R.string.title_added_polyshapes);
                break;
            case R.id.menu_clear_shapes:
                ClearPoly();
                title = null;
                break;
            case R.id.menu_go_target:
                addGroundOverlay("target_circle", R.drawable.target_circle);
                title = getString(R.string.title_target_circle_overlay);
                break;
            case R.id.menu_go_kakunin:
                addGroundOverlay("kakunin_logo", R.drawable.kakunin_logo);
                title = getString(R.string.title_kakunin_logo_overlay);
                break;
            case R.id.menu_clear_go:
                ClearGroundOverlays();
                title = null;
                break;
            case R.id.menu_add_heatmap:
                //add heatmap
                AddHeatMap();
                title = getString(R.string.title_added_heatmap);
                break;
            case R.id.menu_add_providers:
                tileProviderSelectionDialog();
                title = getString(R.string.title_alternate_map_tiles);
                break;
            case R.id.menu_clear_to:
                ClearTileOverlays();
                title = null;
                break;
            case R.id.menu_places_clothing:
                //places by Clothes
                ClearMarkers();
                search.add("clothing_store");
                SearchForType(search, R.drawable.marker_clothes, true);
                title = getString(R.string.title_nearby_place_type);
                break;
            case R.id.menu_places_electronics:
                //places by Electronics
                ClearMarkers();
                search.add("electronics_store");
                SearchForType(search, R.drawable.marker_electronics, true);
                title = getString(R.string.title_nearby_place_type);
                break;
            case R.id.menu_places_ATM:
                //places by ATM
                ClearMarkers();
                search.add("atm");
                SearchForType(search, R.drawable.marker_atm, true);
                title = getString(R.string.title_nearby_place_type);
                break;
            case R.id.menu_clear_places:
                ClearMarkers();
                title = null;
                break;
            case R.id.menu_get_directions:
                //Normal
                ClearMarkers();
                requiredMarkerMode(2, false);
                title = getString(R.string.title_adding_markers);
                break;
            //case R.id.menu_get_directions_waypoints:
            //    ClearMarkers();
            //    requiredMarkerMode(2, true);
            //    title = getString(R.string.title_adding_markers);
            //    break;
            case R.id.menu_sv_select:
                if (mFirstStreetViewSelect) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setTitle(getString(R.string.dialog_title_info));
                    builder1.setMessage(getString(R.string.dialog_message_sv));
                    builder1.setPositiveButton(getString(R.string.dialog_okay), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing
                        }
                    });
                    builder1.show();
                    mFirstStreetViewSelect = false;
                }
                mCurrentMapMode = MapMode.SELECT_STREETVIEW;
                break;
            //case R.id.menu_map_api_seperator:
            //    displayCards(R.array.info_card_maps_api);
            //    title = getString(R.string.title_maps_api);
            //    break;
            case R.id.menu_autocomplete:
                if (mAutoCompleteLayer.getVisibility() == View.VISIBLE) HideAutoCompleteLayer();
                else ShowAutoCompleteLayer();
                title = getString(R.string.title_autocomplete);
                setForegroundMode(ForegroundMode.AUTOCOMPLETE);
                break;
            //case R.id.menu_places_api_seperator:
            //    displayCards(R.array.info_card_places_api);
            //    title = getString(R.string.title_places_api);
            //    break;
            case R.id.menu_places_by_keyword:
                ClearMarkers();
                showKeywordSearchDialog(R.drawable.marker_compass);
                title = getString(R.string.title_searchby_keyword);
                break;
            //case R.id.menu_directions_api_seperator:
            //    displayCards(R.array.info_card_directions_api);
            //    title = getString(R.string.title_directions_api);
            //    break;
            case R.id.menu_travel_mode:
                selectTravelMode();
                title = getString(R.string.title_directions_api);
                break;
            case R.id.menu_store_locator:
                storeLocatorInit();
                title = getString(R.string.title_store_locator);
                break;
            default:
                isOpenableGroup = true;
                break;
        }
        Log.i(TAG, "Title: " + title);
        if(title == null) {
            setToolbarTitle(getTitle().toString());
        } else if(title.length() < 1) {
            setToolbarTitle(getFullTitle(id).toString());
        } else {
            CharSequence[] fullTitle = getDrawerListView().getSeperatorAndTitle(id);
            Log.i(TAG, "Full Title for " + getResources().getResourceName(id) + " " + fullTitle[0] + " :: " + fullTitle[1]);
            if(fullTitle.length > 0) {
                setToolbarTitle(getComposite(fullTitle[0], title).toString());
            } else {
                setToolbarTitle(getTitle().toString());
            }
        }
        if (!isOpenableGroup) CloseDrawer();
        return false;
    }

    @Override
    protected boolean onMenuItemLongPressed(int id) {
        return false;
    }

    Hashtable<String, Integer> mDrawerTable;

    @Override
    @OverridingMethodsMustInvokeSuper
    public boolean onCreateOptionsMenu(Menu menu) {
        //setup the search strings
        Menu temp = new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.drawer_menu, temp);

        mDrawerTable = new Hashtable<>();

        for (int i = 0; i < temp.size(); i++) {
            MenuItem groupItem = temp.getItem(i);
            mDrawerTable.put(groupItem.getTitle().toString(), groupItem.getItemId());
            if (groupItem.hasSubMenu()) {
                for (int j = 0; j < groupItem.getSubMenu().size(); j++) {
                    MenuItem childItem = groupItem.getSubMenu().getItem(j);
                    mDrawerTable.put(childItem.getTitle().toString(), childItem.getItemId());
                }
            }
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.drawer_search) {
            AutoCompleteTextView searchTextView = (AutoCompleteTextView) getLayoutInflater().inflate(R.layout.autocomplete_formatted, mForeground0, false);
            //AutoCompleteTextView searchTextView = (AutoCompleteTextView) mForeground0.findViewById(R.id.search_autocomplete);

            List<String> labels = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : mDrawerTable.entrySet()) {
                labels.add(entry.getKey());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, labels);
            searchTextView.setAdapter(adapter);
            searchTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String labelString = parent.getAdapter().getItem(position).toString();
                    unsetForegroundMode(foregroundMode);
                    onMenuItemPressed(mDrawerTable.get(labelString));
                    Log.i(TAG, "Search API Press: " + labelString + " => " + mDrawerTable.get(labelString));
                    //makeToast("Search For: "+ drawerPosition.groupPosition + " - " + drawerPosition.childPosition, Toast.LENGTH_LONG);
                }
            });

            setForegroundMode(ForegroundMode.SEARCH);
            mForeground0.addView(searchTextView);
            return true;
        } else if(id == R.id.drawer_logout) {
            if(AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
                LoginTopView();
            } else {
                Toast.makeText(this, "You are not logged in", Toast.LENGTH_LONG).show();
                //switch back to login stuff
            }
        }
        makeToast("Item clicked " + getResources().getResourceName(item.getItemId()), Toast.LENGTH_LONG);
        return super.onOptionsItemSelected(item);
    }

    //!------------------------------------------------------------------------+
    //!                          Card Displays                                 |
    //!------------------------------------------------------------------------+
    void displayCards(int card_list_resource_id) {
        TypedArray array = getResources().obtainTypedArray(card_list_resource_id);
        List<SwipeCardsController.TapCardInterface> tapcards = new ArrayList<>();
        Log.i(TAG, "Loaded resources : " + array.length());
        for (int i = 0; i < array.length(); i++) {
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
        setForegroundMode(ForegroundMode.INFO);
        mCardController = new SwipeCardsController(this, mForegroundInfo, tapcards);
        mCardController.SetOnFinishHandler(new SwipeCardsController.OnFinished() {
            @Override
            public void onFinish() {
                unsetForegroundMode(foregroundMode);
                SwipeCardsController.ID_UNIQUE = 0;
            }
        });
        array.recycle();
    }

    //!------------------------------------------------------------------------+
    //!                          Marker Options                                |
    //!------------------------------------------------------------------------+
    HashMap<LatLng, Marker> mAllActiveMarkers = new HashMap<>();

    void AddMarkers(int totalMarkers) {
        ClearMarkers();
        mAllActiveMarkers = new HashMap<>();

        //Get bounds in camera
        LatLngBounds screenBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

        //if(mClusterManager == null) setUpClusterer();

        for (int i = 0; i < totalMarkers; i++) {
            double maxlat = Math.max(screenBounds.northeast.latitude, screenBounds.southwest.latitude);
            double minlat = Math.min(screenBounds.northeast.latitude, screenBounds.southwest.latitude);
            double randlat = (Math.random() * (maxlat - minlat)) + minlat;

            double maxlng = Math.max(screenBounds.northeast.longitude, screenBounds.southwest.longitude);
            double minlng = Math.min(screenBounds.northeast.longitude, screenBounds.southwest.longitude);
            double randlng = (Math.random() * (maxlng - minlng)) + minlng;

            LatLng pos = new LatLng(randlat, randlng);
            mAllActiveMarkers.put(pos, mGoogleMap.addMarker(new MarkerOptions().position(pos).title(getString(R.string.random_marker_title) + " " + i)));
            Log.d(TAG, "Adding marker @ " + new LatLng(randlat, randlng) + ", total markers: " + mAllActiveMarkers.size());
        }

    }

    void ClearMarkers() {
        //delete all old markers
        for (Map.Entry<LatLng, Marker> markerSet : mAllActiveMarkers.entrySet()) {
            markerSet.getValue().remove();
        }
        mAllActiveMarkers = new HashMap<>();

        ClearCluster();
    }

    void CustomizeMarkers() {
        if (mAllActiveMarkers == null) {
            Toast.makeText(this, getString(R.string.msg_no_markers), Toast.LENGTH_LONG);
            return;
        }

        for (Map.Entry<LatLng, Marker> markerSet : mAllActiveMarkers.entrySet()) {
            int randNum = (int) Math.floor(Math.random() * 4);
            Drawable icon;
            switch (randNum) {
                case 0:
                    icon = getResources().getDrawable(R.drawable.marker_atm);
                    break;
                case 1:
                    icon = getResources().getDrawable(R.drawable.marker_clothes);
                    break;
                case 2:
                    icon = getResources().getDrawable(R.drawable.marker_electronics);
                    break;
                case 3:
                    icon = getResources().getDrawable(R.drawable.marker_penta);
                    break;
                default:
                    icon = getResources().getDrawable(R.drawable.marker_penta);
                    break;
            }
            markerSet.getValue().setIcon(BitmapDescriptorFactory.fromBitmap(Resize(icon, 120, 81)));
        }

    }

    void ClearCluster() {
        if (mClusterManager != null) {
            MarkerManager.Collection collection;
            collection = mClusterManager.getClusterMarkerCollection();
            for (Marker marker : collection.getMarkers())
                marker.remove();

            collection = mClusterManager.getMarkerCollection();
            for (Marker marker : collection.getMarkers())
                marker.remove();

            mClusterManager.clearItems();
            clearClusterDictionaries();
        }
    }

    void ClusterMarkers() {
        if (mAllActiveMarkers == null || (mAllActiveMarkers != null && mAllActiveMarkers.size() < 1)) {
            Toast.makeText(this, getString(R.string.msg_no_markers), Toast.LENGTH_LONG).show();
            Log.i(TAG, getString(R.string.msg_no_markers));
            return;
        }

        for (Map.Entry<LatLng, Marker> markerSet : mAllActiveMarkers.entrySet()) {
            XClusterItem<Void> newClusterItem = new XClusterItem<>(markerSet.getValue().getPosition().latitude, markerSet.getValue().getPosition().longitude, null, null);
            addToClusterManager(newClusterItem);
            markerSet.getValue().setVisible(false);
            Log.i(TAG, "Added marker to cluster manager @ " + markerSet.getValue().getPosition() + " - " + markerSet.getValue().getTitle());
        }
        cluster();
    }

    //!-------------------------------------------------------------------------+
    //!                          Shape Options                                  |
    //!-------------------------------------------------------------------------+
    List<Polyline> mAllPolyLines = new ArrayList<>();
    List<Polygon> mAllPolygon = new ArrayList<>();

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
        allMarkers.addAll(mAllActiveMarkers.values());
        Marker source = allMarkers.get(allMarkers.size() - 1);
        allMarkers.remove(source);
        TheWeb(source, allMarkers);
    }

    class SelectPolygonNDialog extends Dialog implements View.OnClickListener {
        NumberPicker Picker;
        Button Confirm, Cancel;
        SimpleAction<Integer> onSelected;

        public SelectPolygonNDialog(Context context, SimpleAction<Integer> onSelected) {
            super(context);
            this.onSelected = onSelected;
        }

        @Override
        protected void onCreate(Bundle savedInstance) {
            super.onCreate(savedInstance);
            setTitle(getString(R.string.msg_select_no_sides));
            setContentView(R.layout.dialog_frame_selector);
            Confirm = (Button) findViewById(R.id.confirm);
            Cancel = (Button) findViewById(R.id.cancel);
            Picker = (NumberPicker) findViewById(R.id.numberPicker);
            Picker.setMinValue(3);
            Picker.setMaxValue(10);
            Picker.setWrapSelectorWheel(false);

            Confirm.setOnClickListener(this);
            Cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.confirm:
                    onSelected.Action(Picker.getValue());
                    dismiss();
                    break;
                case R.id.cancel:
                    dismiss();
                    break;
            }
        }
    }

    void selectPolygonN() {
        new SelectPolygonNDialog(this, new SimpleAction<Integer>() {
            @Override
            public void Action(Integer arg) {
                AddPolygon(arg);
            }
        }).show();
    }

    PolygonOptions PolyGrahamScan(final List<Point2D> points) {
        if (points.size() < 1)
            return new PolygonOptions();
        GrahamScan grahamScan = new GrahamScan(points.toArray(new Point2D[points.size()]));
        Iterator<Point2D> itr = grahamScan.hull().iterator();
        Point2D source = itr.next();
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.add(new LatLng(source.x(), source.y()));
        while (itr.hasNext()) {
            Point2D point = itr.next();
            polygonOptions.add(new LatLng(point.x(), point.y()));
        }
        polygonOptions.add(new LatLng(source.x(), source.y()));
        return polygonOptions;
    }

    void AddPolygon(int points) {
        //Get bounds in camera
        LatLngBounds screenBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;

        //if(mClusterManager == null) setUpClusterer();
        List<Point2D> randPoints = new ArrayList<>();
        for (int i = 0; i < points; i++) {
            double maxlat = Math.max(screenBounds.northeast.latitude, screenBounds.southwest.latitude);
            double minlat = Math.min(screenBounds.northeast.latitude, screenBounds.southwest.latitude);
            double randlat = (Math.random() * (maxlat - minlat)) + minlat;

            double maxlng = Math.max(screenBounds.northeast.longitude, screenBounds.southwest.longitude);
            double minlng = Math.min(screenBounds.northeast.longitude, screenBounds.southwest.longitude);
            double randlng = (Math.random() * (maxlng - minlng)) + minlng;

            randPoints.add(new Point2D(randlat, randlng));
        }

        PolygonOptions polyOptions = PolyGrahamScan(randPoints);

        //Marker source = allMarkers.get(allMarkers.size() - 1);
        //allMarkers.remove(source);
        //for(LatLng position : TheTraveller(source, allMarkers)) {
        //    polyOptions.add(position);
        //}

        Random rand = new Random(System.currentTimeMillis());
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        polyOptions.fillColor(Color.argb(200, r, g, b));

        r = rand.nextInt(255);
        g = rand.nextInt(255);
        b = rand.nextInt(255);
        polyOptions.strokeColor(Color.argb(200, r, g, b));
        polyOptions.geodesic(true);

        mAllPolygon.add(mGoogleMap.addPolygon(polyOptions));
    }

    void ClearPoly() {
        for (Polyline line : mAllPolyLines) {
            line.remove();
        }

        for (Polygon poly : mAllPolygon) {
            poly.remove();
        }

        ClearMarkers();
    }

    //!-------------------------------------------------------------------------+
    //!                          Ground Overlays                                |
    //!-------------------------------------------------------------------------+
    HashMap<String, GroundOverlay> mAllGroundOverlays = new HashMap<>();

    void ClearGroundOverlays() {
        for (Map.Entry<String, GroundOverlay> entry : mAllGroundOverlays.entrySet()) {
            entry.getValue().remove();
        }
        mAllTileOverlays.clear();
    }

    void RemoveGroundOverlayById(String id) {
        if (mAllGroundOverlays.containsKey(id)) {
            mAllGroundOverlays.get(id).remove();
            mAllGroundOverlays.remove(id);
        }
    }

    void addGroundOverlay(String title, int res_id) {
        Bitmap fin = BitmapFactory.decodeResource(getResources(), res_id);
        fin = makeTransparent(fin, 96);

        GroundOverlayOptions newOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(fin))
                .position(mGoogleMap.getCameraPosition().target, 5000f, 5000f);

        mAllGroundOverlays.put(title, mGoogleMap.addGroundOverlay(newOverlay));
    }


    public Bitmap makeTransparent(Bitmap src, int value) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap transBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        // config paint
        final Paint paint = new Paint();
        paint.setAlpha(value);
        canvas.drawBitmap(src, 0, 0, paint);
        return transBitmap;
    }


    //!-------------------------------------------------------------------------+
    //!                          Tile Options                                   |
    //!-------------------------------------------------------------------------+
    HashMap<String, TileOverlay> mAllTileOverlays = new HashMap<>();

    void ClearTileOverlays() {
        for (Map.Entry<String, TileOverlay> entry : mAllTileOverlays.entrySet()) {
            entry.getValue().remove();
        }
        mAllTileOverlays.clear();
    }

    void RemoveTileOverlayById(String id) {
        if (mAllTileOverlays.containsKey(id)) {
            mAllTileOverlays.get(id).remove();
            mAllTileOverlays.remove(id);
        }
    }

    ErrorHandler defaultTileProviderErrorHander() {
        return new ErrorHandler() {
            @Override
            public void Handle(Exception e) {
                //TODO error
            }
        };
    }

    int mSelectedTileProvider = 0;
    String mSelectedTileProviderId;
    AlertDialog mtileProviderSelectorDialog;
    CharSequence[] mTileProviderChoices;

    void tileProviderSelectionDialog() {
        mTileProviderChoices = new String[]{getString(R.string.tile_p_none), getString(R.string.tile_p_weather),
                getString(R.string.tile_p_landscape), getString(R.string.tile_p_cycling)};
        mSelectedTileProviderId = mTileProviderChoices[mSelectedTileProvider].toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(mTileProviderChoices, mSelectedTileProvider, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = mTileProviderChoices[which].toString();
                RemoveTileOverlayById(mSelectedTileProviderId);
                switch (which) {
                    case 0:
                        break;
                    case 1:
                        AddTileProvider(id, "http://tileserver.maptiler.com/weather/%d/%d/%d.png", 256, 256);
                        //zoom out
                        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(1f));
                        break;
                    case 2:
                        AddTileProvider(id, "http://a.tile.thunderforest.com/landscape/%d/%d/%d.png", 256, 256);
                        break;
                    case 3:
                        AddTileProvider(id, "http://a.tile.opencyclemap.org/cycle/%d/%d/%d.png", 256, 256);
                        break;
                }
                mSelectedTileProvider = which;
                mSelectedTileProviderId = id;
                mtileProviderSelectorDialog.dismiss();
            }
        });
        mtileProviderSelectorDialog = builder.show();
        Log.i(TAG, "Choices: " + mTileProviderChoices.length + " Selected " + mSelectedTileProvider);
    }

    public interface ErrorHandler {
        void Handle(Exception e);
    }

    void AddTileProvider(final String titleID, final String tileProviderURL, final int width, final int height) {
        mForegroundTask = new QuickAsyncTask<Void, Integer, Object>(new QuickAsyncTask.QuickTask<Void, Object>() {
            @Override
            public void Pre() {
                showProgressDialog(getString(R.string.dialog_loading), getString(R.string.msg_loading_tiles));
            }

            @Override
            public Object Do(Void... params) {
                UrlTileProvider urlTileProvider = new UrlTileProvider(height, width) {
                    @Override
                    public URL getTileUrl(int x, int y, int z) {
                        String s = String.format(tileProviderURL, z, x, y);

                        if (!checkTileExists(x, y, z))
                            return null;

                        try {
                            return new URL(s);
                        } catch (MalformedURLException e) {
                            dismissProgress();
                            sendAlert("Internal Error", "The URL provided for the tile server was malformed");
                            Log.e(TAG, e.getMessage());
                            e.printStackTrace();
                            return null;
                        }
                    }

                    private boolean checkTileExists(int x, int y, int z) {
                        int minZoom = 1;
                        int maxZoom = 16;

                        if ((z < minZoom) || (z > maxZoom)) {
                            return false;
                        }
                        return true;
                    }
                };

                return new TileOverlayOptions().tileProvider(urlTileProvider);
            }

            @Override
            public void Post(Object param) {
                mAllTileOverlays.put(titleID, mGoogleMap.addTileOverlay((TileOverlayOptions) param));
                getProgressDialog().dismiss();
            }
        });
        mForegroundTask.execute();
    }


    void AddHeatMap() {
        AddMarkers(30);
        new QuickAsyncTask<Void, Void, Void>(new QuickAsyncTask.QuickTask<Void, Void>() {
            @Override
            public void Pre() {
                showProgressDialog(getString(R.string.dialog_loading), getString(R.string.dialog_loading));
            }

            @Override
            public Void Do(Void... params) {
                return null;
            }

            @Override
            public void Post(Void param) {
                for (Marker marker : mAllActiveMarkers.values()) {
                    marker.setVisible(false);
                }
                convertMarkersToHeatMap(new ArrayList<>(mAllActiveMarkers.values()));
                ClearMarkers();
                dismissProgress();
            }
        }).execute();
    }

    HeatmapTileProvider mHeatmapProvider;
    TileOverlay mHeatmapTileOverlay;
    private static String HEATMAP_KEY = "heatmap_kakunin";

    protected void convertMarkersToHeatMap(List<Marker> markers) {
        List<LatLng> list = new ArrayList<>();

        for (Marker marker : markers) {
            list.add(marker.getPosition());
        }

        if (list.size() == 0) {
            makeToast(getString(R.string.msg_no_markers), Toast.LENGTH_LONG);
            return;
        }

        mHeatmapProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();

        mHeatmapTileOverlay = mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mHeatmapProvider));
        mAllTileOverlays.put(HEATMAP_KEY, mHeatmapTileOverlay);
    }

    //!-------------------------------------------------------------------------+
    //!                          Search Options                                 |
    //!-------------------------------------------------------------------------+
    void SearchForType(List<String> placeTypes, final int marker_id, final boolean dismissOnFinish) {


        StringBuilder typesBuilder = new StringBuilder();
        typesBuilder.append(placeTypes.get(0));
        for (int i = 1; i < placeTypes.size(); i++) {
            typesBuilder.append("|" + placeTypes.get(i));
        }
        Log.i(TAG, "Searching for types " + typesBuilder.toString());
        UpdateLastLocation();

        LatLngBounds cameraBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
        LatLng cameraCenter = cameraBounds.getCenter();
        double radius = getCurrentCameraBoundsRadius();
        radius = (radius < 10000) ? radius : 10000;

        final SearchForPlacesTask searchForPlacesTask =
                new SearchForPlacesTask(this, null, null, cameraCenter, radius, typesBuilder.toString(),
                        new QuickAsyncTask.QuickTask<Void, List<PlaceJSON>>() {
                            @Override
                            public void Pre() {
                                showProgressDialog(getString(R.string.dialog_searching) + "...", getString(R.string.msg_searching_google_maps));
                            }

                            @Override
                            public List<PlaceJSON> Do(Void... params) {
                                return null;
                            }

                            @Override
                            public void Post(List<PlaceJSON> places) {
                                if (dismissOnFinish)
                                    dismissProgress();

                                for (PlaceJSON place : places) {
                                    XClusterItem<PlaceJSON> MCI = new XClusterItem<>(place.geometry.location.lat, place.geometry.location.lng, place, Resize(getResources().getDrawable(marker_id), 120, 81));
                                    addToClusterManager(MCI);
                                    putToLocationObjectDictionary(MCI.getPosition(), MCI.getLoad());
                                    putToClusterItemDictionary(MCI.getPosition(), MCI);
                                }
                                cluster();
                                //setInfoWindowAdapter(getMarkerInfoWindowAdapter());
                            }
                        });
        searchForPlacesTask.execute();
    }

    void showKeywordSearchDialog(final int marker_id) {
        final Dialog KeywordDialog = new Dialog(this);
        KeywordDialog.setContentView(R.layout.shout_message_dialog);

        final EditText keyword = (EditText) KeywordDialog.findViewById(R.id.smd_editext);
        Button search = (Button) KeywordDialog.findViewById(R.id.smd_broadcast);
        Button cancel = (Button) KeywordDialog.findViewById(R.id.smd_cancel);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateLastLocation();

                LatLngBounds cameraBounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
                LatLng cameraCenter = cameraBounds.getCenter();
                double radius = getCurrentCameraBoundsRadius();
                radius = (radius < 10000) ? radius : 10000;

                SearchForPlacesTask searchForPlacesTask =
                        new SearchForPlacesTask(getGOGAMapActivity(), null, keyword.getText().toString(), cameraCenter, radius, null,
                                new QuickAsyncTask.QuickTask<Void, List<PlaceJSON>>() {
                                    @Override
                                    public void Pre() {
                                        showProgressDialog(getString(R.string.dialog_searching), getString(R.string.msg_searching_google_places));
                                    }

                                    @Override
                                    public List<PlaceJSON> Do(Void... params) {
                                        return null;
                                    }

                                    @Override
                                    public void Post(List<PlaceJSON> places) {
                                        dismissProgress();
                                        for (PlaceJSON place : places) {
                                            XClusterItem<PlaceJSON> MCI = new XClusterItem<>(place.geometry.location.lat, place.geometry.location.lng, place, Resize(getResources().getDrawable(marker_id), 120, 81));
                                            addToClusterManager(MCI);
                                            putToLocationObjectDictionary(MCI.getPosition(), MCI.getLoad());
                                        }
                                        cluster();
                                        //setInfoWindowAdapter(getMarkerInfoWindowAdapter());
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

    //!-------------------------------------------------------------------------+
    //!                          Directions API                                 |
    //!-------------------------------------------------------------------------+
    GetDirectionsBetweenLocations.TravelMode mTravelMode = GetDirectionsBetweenLocations.TravelMode.TRANSIT;
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

    void selectTravelMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        List<CharSequence> travelModesL = new ArrayList<>();
        travelModesL.add("Transit");
        travelModesL.add("Driving");
        travelModesL.add("Bicycle");
        travelModesL.add("Walking");
        builder.setSingleChoiceItems(travelModesL.toArray(new String[travelModesL.size()]), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mTravelMode = GetDirectionsBetweenLocations.TravelMode.TRANSIT;
                        dialog.dismiss();
                        break;
                    case 1:
                        mTravelMode = GetDirectionsBetweenLocations.TravelMode.DRIVING;
                        dialog.dismiss();
                        break;
                    case 2:
                        mTravelMode = GetDirectionsBetweenLocations.TravelMode.BICYCLING;
                        dialog.dismiss();
                        break;
                    case 3:
                        mTravelMode = GetDirectionsBetweenLocations.TravelMode.WALKING;
                        dialog.dismiss();
                        break;
                    default:
                        mTravelMode = GetDirectionsBetweenLocations.TravelMode.TRANSIT;
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    void requiredMarkerMode(final int requiredMarkers, boolean orGreater) {
        mRequiredMarkers = requiredMarkers;
        orGreaterMode = orGreater;
        requiredMarkerModeOn = true;
        mCurrentMapMode = MapMode.SET_MARKER;
        //Add a large done button on foreground 0
        if (orGreater) {
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
        if (requiredMarkerModeOn) {
            MarkerOptions newMarker = null;

            if (mPlantedMarkers == 0) {
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
                mAllActiveMarkers.put(position, marker);

                mCurrentMapMode = MapMode.NONE;
                requiredMarkerModeOn = false;
                orGreaterMode = false;
                mRequiredMarkers = 0;
                mPlantedMarkers = 0;

                if (orGreater) {
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
            mAllActiveMarkers.put(position, marker);
            return marker;
        }

        Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(position));
        mAllActiveMarkers.put(position, marker);
        return marker;
    }

    void AddedAllMarkers(final boolean waypoint) {
        //All markers added
        Log.i(TAG, "Add markers have been added " + waypoint);
        Marker startMarker = getMarkerByNameStartsWith(getString(R.string.fsmm_start));
        Marker endMarker = getMarkerByNameStartsWith(getString(R.string.fsmm_end));
        List<Marker> allMarkers = new ArrayList(mAllActiveMarkers.values());
        allMarkers.remove(startMarker);
        allMarkers.remove(endMarker);
        List<String> waypoints = new ArrayList<>();
        if (waypoint) {
            //Geocode every other point
            Geocoder coder = new Geocoder(this);
            for (Marker marker : allMarkers) {
                try {
                    List<Address> markerLocations = coder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                    if (markerLocations.size() > 0) {
                        String address = "";
                        int i = 0;
                        for (; i < markerLocations.get(0).getMaxAddressLineIndex(); i++) {
                            address += markerLocations.get(0).getAddressLine(i) + ", ";
                        }
                        if (i > 0) {
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
                mTravelMode, false);
        task.setWaypoints(waypoints);
        task.setQuickie(new QuickAsyncTask.QuickTask<Void, DirectionsJSON>() {
            @Override
            public void Pre() {
                showProgressDialog("Searching", "Using Google Directions API...");
            }

            @Override
            public DirectionsJSON Do(Void... params) {
                return null;
            }

            @Override
            public void Post(DirectionsJSON directions) {
                if (directions != null) {
                    int routes_cnt = 0;
                    for (; routes_cnt < directions.routes.size(); routes_cnt++) {
                        DirectionsJSON.Route route = directions.routes.get(routes_cnt);
                        List<LatLng> polyLine = route.getDecodedPoly();
                        drawPolyLines(polyLine);
                        setToolbarTitle("Viewing Directions");
                        showDirectionsDetail(directions);
                        break;
                    }
                }
                dismissProgress();
            }
        });
        task.execute();
    }

    void showDirectionsDetail(DirectionsJSON directions) {
        //mForegroundMaster.addView(directionDetailWindow.getLayout());
        //mSliderPanel.addView(directionDetailWindow.getLayout());
        setForegroundMode(ForegroundMode.DIRECTION_DETAIL);
        DirectionDetailWindow directionDetailWindow = new DirectionDetailWindow(this, mSliderPanel, directions, 0);
    }

    void drawPolyLines(List<LatLng> polyLine) {
        if (polyLine == null || polyLine.size() < 1)
            return;
        int index = 0;
        while (index < polyLine.size()) {
            LatLng source = polyLine.get(index++);
            LatLng dest = null;
            if (index >= polyLine.size()) {
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
        for (Marker marker : mAllActiveMarkers.values()) {
            if (marker.getTitle().indexOf(search) == 0) {
                foundMarker = marker;
            }
        }
        return foundMarker;
    }

    //Helpers
    public static Bitmap Resize(Drawable image, int height, int width) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, false);
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

        while (!markerSet.isEmpty()) {
            Marker closest = null;
            double dist = Double.POSITIVE_INFINITY;
            for (Marker marker : markerSet) {
                //calc distance
                double markerDistance = distance(source, marker);
                if (markerDistance < dist) {
                    dist = markerDistance;
                    closest = marker;
                }
            }

            if (closest != null) {
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

    //!-------------------------------------------------------------------------+
    //!                              KML    API                                 |
    //!-------------------------------------------------------------------------+
    HashMap<String, KmlLayer> mAllKMLLayers = new HashMap<>();

    void removeKML(String name) {
        if (mAllKMLLayers.containsKey(name))
            mAllKMLLayers.get(name).removeLayerFromMap();
    }

    void removeAllKML() {
        for (Map.Entry<String, KmlLayer> item : mAllKMLLayers.entrySet()) {
            item.getValue().removeLayerFromMap();
        }
    }

    void world_borders_KML() {
        try {
            KmlLayer kmlLayer = new KmlLayer(mGoogleMap, R.raw.world_borders, this);
            kmlLayer.addLayerToMap();
            mAllKMLLayers.put("world_borders", kmlLayer);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Toast.makeText(this, "Error reading the KML File [File read error]", Toast.LENGTH_LONG).show();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading the KML File [Bad File]", Toast.LENGTH_LONG).show();
        }
    }

    //!-------------------------------------------------------------------------+
    //!                          Photo Map                                      |
    //!-------------------------------------------------------------------------+

    Bundle getGraphFields(String fields) {
        Bundle request_params = new Bundle();
        request_params.putString("fields", fields);
        return request_params;
    }

    List<JSONObject> allPhotos;
    HashSet<String> photoIdSet;
    int proc_count, photo_load_count;
    void facebookLoadPhotos() {
        //Toast.makeText(this, "This feature is coming soon", Toast.LENGTH_LONG).show();
        /*GraphRequest graphRequest = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + AccessToken.getCurrentAccessToken().getUserId() + "/photos",
                null,

        )*/
        allPhotos = new ArrayList<>();
        photoIdSet = new HashSet<>();
        proc_count = 0;

        final String req_fields = "link,place,images,picture,from{name},created_time,likes";

        String user_id = AccessToken.getCurrentAccessToken().getUserId();
        final String req_tagged = "/me/photos";
        GraphRequest tagGraphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                req_tagged,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            if(response != null && response.getRawResponse() != null) {
                                JSONArray dataArray = response.getJSONObject().getJSONArray("data");
                                int i;
                                for(i = 0; i < dataArray.length(); i++) {
                                    JSONObject photo = dataArray.getJSONObject(i);
                                    if(!photoIdSet.contains(photo.getString("id")) && photo.has("place")) {
                                        allPhotos.add(photo);
                                        photoIdSet.add(photo.getString("id"));
                                    }
                                }
                                Log.i(TAG, "Photos with place: " + allPhotos.size());
                            }
                            else Log.i(TAG, "Response was null/empty");
                            proc_count++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        tagGraphRequest.setParameters(getGraphFields(req_fields));
        tagGraphRequest.executeAsync();
        GraphRequest uploadedGraphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                req_tagged,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            if(response != null && response.getRawResponse() != null) {
                                JSONArray dataArray = response.getJSONObject().getJSONArray("data");
                                int i;
                                for(i = 0; i < dataArray.length(); i++) {
                                    JSONObject photo = dataArray.getJSONObject(i);
                                    if(!photoIdSet.contains(photo.getString("id")) && photo.has("place")) {
                                        allPhotos.add(photo);
                                        photoIdSet.add(photo.getString("id"));
                                    }
                                }
                                Log.i(TAG, "[UP] Photos with place: "  + allPhotos.size());
                            }
                            else Log.i(TAG, "[UP] Response was null/empty");
                            proc_count++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle req_bundle = getGraphFields(req_fields);
        req_bundle.putString("type", "uploaded");
        uploadedGraphRequest.setParameters(req_bundle);
        uploadedGraphRequest.executeAsync();
        new QuickAsyncTask<Void, Void, Void>(new QuickAsyncTask.QuickTask<Void, Void>() {
            @Override
            public void Pre() {

            }

            @Override
            public Void Do(Void... params) {
                while(proc_count < 2) {
                    //wait
                }
                return null;
            }

            @Override
            public void Post(Void param) {
                //all photos loaded
                if(allPhotos.size() > 0)
                    putPhotoMarkers();
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getGOGAMapActivity());
                    builder.setTitle("No Content");
                    builder.setMessage("You donot have any content with locations");
                    builder.setCancelable(true);
                    builder.show();
                }
            }
        }).execute();
    }

    public static class TypedJSONObject {
        public enum Type { PHOTO, POST, PERSON };
        public Type type;
        public JSONObject object;

        public TypedJSONObject(Type type, JSONObject object) {
            this.type = type;
            this.object = object;
        }
    }

    void setupFBClusterManager() {
        //reset everything marker/cluster related
        ClearMarkers();
        ClearCluster();
        setUpClusterer();

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MapClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<MapClusterItem> cluster) {
                //show photo album window with cards
                List<SwipeCardsController.TapCardInterface> tapcards = new ArrayList<>();

                for(MapClusterItem clusterItem : cluster.getItems()) {
                    Log.i(TAG, "Cluster touch: " + clusterItem.toString());
                    try {
                        TypedJSONObject loadItem = ((XClusterItem<TypedJSONObject>) clusterItem).getLoad();
                        if(loadItem.type == TypedJSONObject.Type.PHOTO) {
                            JSONObject photo = loadItem.object;
                            JSONObject image = photo.getJSONArray("images").getJSONObject(0);
                            String url = image.getString("source");
                            tapcards.add(new FBPhotoTapCard(url));
                        }
                    } catch (ClassCastException e) {
                        Log.e(TAG, "Improper object type");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

                setForegroundMode(ForegroundMode.INFO);
                mCardController = new SwipeCardsController(getGOGAMapActivity(), mForegroundInfo, tapcards);
                mCardController.SetOnFinishHandler(new SwipeCardsController.OnFinished() {
                    @Override
                    public void onFinish() {
                        unsetForegroundMode(foregroundMode);
                        SwipeCardsController.ID_UNIQUE = 0;
                    }
                });
                return false;
            }
        });
    }

    @Override
    public void FBPhotoIWClick(List<XClusterItem> photoClusterItems) {
        /*try {
            List<SwipeCardsController.TapCardInterface> tapcards = new ArrayList<>();
            JSONObject image = photo.getJSONArray("images").getJSONObject(0);
            String url = image.getString("source");
            tapcards.add(new FBPhotoTapCard(url));

            setForegroundMode(ForegroundMode.INFO);
            mCardController = new SwipeCardsController(getGOGAMapActivity(), mForegroundInfo, tapcards);
            mCardController.SetOnFinishHandler(new SwipeCardsController.OnFinished() {
                @Override
                public void onFinish() {
                    unsetForegroundMode(foregroundMode);
                    SwipeCardsController.ID_UNIQUE = 0;
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }*/

        //clusteritem is LoadLocation with TypedJSONObject
        List<JSONObject> photos = new ArrayList<>();
        for(XClusterItem clusterItem : photoClusterItems) {
            JSONObject photo = ((TypedJSONObject)((LoadLocationObject) clusterItem.getLoad()).getLoad()).object;
            photos.add(photo);
        }

        //show the hidden view list view
        getLayoutInflater().inflate(R.layout.facebook_album_viewer, hiddenFrame);
        ((ListView) hiddenFrame.findViewById(R.id.fav_listview)).setAdapter(new FBPhotoListAdapter(this, photos));
        ShowHidden();
    }

    List<XClusterItem> mPendingMarkers;
    LatLngBounds.Builder bounder;
    List<Marker> mClusterHacks;
    void putPhotoMarkers() {
        mPendingMarkers = new ArrayList<>();
        mClusterHacks = new ArrayList<>();
        bounder = LatLngBounds.builder();
        for(final JSONObject photo : allPhotos) {
            try {
                JSONObject place = photo.getJSONObject("place");
                JSONObject location = place.getJSONObject("location");
                final double lat = location.getDouble("latitude");
                final double lng = location.getDouble("longitude");

                //add marker
                final LatLng position = new LatLng(lat, lng);
                JSONObject firstImage = photo.getJSONArray("images").getJSONObject(0);
                double heightOverWidth = firstImage.getDouble("height") / firstImage.getDouble("width");
                double properHeight = heightOverWidth * 100;

                new LoadImageTask(photo.getString("picture"), 100, (int) properHeight, 5).SetImageLoadedHandler(new ImageLoadedHandler() {
                    @Override
                    public void Handle(Bitmap Image) {
                        if(Image.getHeight() > 0 && Image.getWidth() > 0) {
                            //mAllActiveMarkers.put(position, mGoogleMap.addMarker(new MarkerOptions().position(position)
                            //        .icon(BitmapDescriptorFactory.fromBitmap(Image))));
                            TypedJSONObject jsonObject = new TypedJSONObject(TypedJSONObject.Type.PHOTO, photo);
                            XClusterItem<TypedJSONObject> xClusterItem =
                                    new XClusterItem<>(position.latitude, position.longitude, jsonObject, Image);
                            mPendingMarkers.add(xClusterItem);
                            //mAllActiveMarkers.put(position, mGoogleMap.addMarker(new MarkerOptions().position(position).
                            //        icon(BitmapDescriptorFactory.fromBitmap(Image)).title("Facebook Photo")));
                        } else {
                            Log.e(TAG, "FB Photo marker image was 0x0px");
                        }
                        Log.i(TAG, "Facebook photo downloaded: " + ++photo_load_count + "/" + allPhotos.size());
                    }
                }).execute();
                bounder.include(position);
            } catch (JSONException e) {
                Log.i(TAG, "Failed to get location deets: " + e.getMessage());
                e.printStackTrace();
            }
        }
        new QuickAsyncTask<Void, Void, Void>(new QuickAsyncTask.QuickTask<Void, Void>() {
            @Override
            public void Pre() {

            }

            @Override
            public Void Do(Void... params) {
                while(photo_load_count < allPhotos.size());
                return null;
            }

            @Override
            public void Post(Void param) {
                //All facebook photos loaded
                /*
                XClusterItem<JSONObject> xClusterItem = new XClusterItem<>(lat, lng, photo, null);
                putToClusterItemDictionary(position, xClusterItem);
                putToLocationObjectDictionary(position, new LoadLocationObject(photo, position));
                addToClusterManager(xClusterItem);
                 */;
                //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounder.build(), 100));
                //AddMarkers(10);
                Log.i(TAG, "Finished downloading photos, cluster items: " + mPendingMarkers.size());
                FacebookAlbumClustering(mPendingMarkers);
            }
        }).execute();

        Toast.makeText(this, "This feature is still under development", Toast.LENGTH_LONG).show();
    }

    void FacebookAlbumClustering(List<XClusterItem> items) {
        //country-wise manual clustering
        HashMap<String, List<XClusterItem>> countryClusters = new HashMap();
        HashMap<String, LatLngBounds.Builder> centerCalc = new HashMap();

        for(XClusterItem<TypedJSONObject> clusterItem : items) {
            //cluster photos by country
            JSONObject photo = clusterItem.getLoad().object;
            try {
                JSONObject place = photo.getJSONObject("place");
                JSONObject location = place.getJSONObject("location");
                String country = location.getString("country");
                Log.i(TAG, "Clustering " + photo.getString("from") + " -> " + country);
                if(!countryClusters.containsKey(country)) {
                    List<XClusterItem> countryItems = new ArrayList();
                    countryItems.add(clusterItem);
                    centerCalc.put(country, new LatLngBounds.Builder().include(clusterItem.getPosition()));
                    countryClusters.put(country, countryItems);
                } else {
                    centerCalc.put(country, centerCalc.get(country).include(clusterItem.getPosition()));
                    List<XClusterItem> countryItems = countryClusters.get(country);
                    countryItems.add(clusterItem);
                    countryClusters.put(country, countryItems);
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            Log.i(TAG, "Added to cluster @ " + clusterItem.getPosition());
        }

        LatLngBounds.Builder bounder = new LatLngBounds.Builder();
        for(Map.Entry<String, List<XClusterItem>> countryCluster : countryClusters.entrySet()) {
            String country = countryCluster.getKey();
            List<XClusterItem> clusterItems = countryCluster.getValue();
            Log.i(TAG, "[" + country + "] Country cluster size: " + clusterItems.size());

            View iconView;
            if(clusterItems.size() == 1) {
                iconView = getLayoutInflater().inflate(R.layout.cluster_grouped_icon_1, null);
                ((ImageView) iconView.findViewById(R.id.photo_1)).setImageBitmap(clusterItems.get(0).icon);
            } else if (clusterItems.size() == 2) {
                iconView = getLayoutInflater().inflate(R.layout.cluster_grouped_icon_2, null);
                ((ImageView) iconView.findViewById(R.id.photo_1)).setImageBitmap(clusterItems.get(0).icon);
                ((ImageView) iconView.findViewById(R.id.photo_2)).setImageBitmap(clusterItems.get(1).icon);
            } else {
                iconView = getLayoutInflater().inflate(R.layout.cluster_grouped_icon_4, null);
                ((ImageView) iconView.findViewById(R.id.photo_1)).setImageBitmap(clusterItems.get(0).icon);
                ((ImageView) iconView.findViewById(R.id.photo_2)).setImageBitmap(clusterItems.get(1).icon);
                ((ImageView) iconView.findViewById(R.id.photo_3)).setImageBitmap(clusterItems.get(2).icon);
                ((ImageView) iconView.findViewById(R.id.photo_4)).setImageBitmap(clusterItems.get(3).icon);
                ((TextView) iconView.findViewById(R.id.photo_count)).setText((clusterItems.size() < 1001) ? "" + clusterItems.size() : "1000+");
            }
            Bitmap albumIcon = loadBitmapFromView(150, 150, iconView);
            LatLng markerPos = centerCalc.get(country).build().getCenter();

            Log.i(TAG, "[" + country + "] Marker @ " + markerPos);
            XClusterItem<List<XClusterItem>> xClusterItem =
                    new XClusterItem<>(markerPos.latitude, markerPos.longitude, clusterItems, albumIcon);
            addToClusterManager(xClusterItem);
            putToClusterItemDictionary(markerPos, xClusterItem);
            putToLocationObjectDictionary(markerPos, new LoadLocationObject(clusterItems, markerPos));
            bounder.include(markerPos);
        }
        cluster();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounder.build(), 100));
    }

    public static Bitmap loadBitmapFromView(int width, int height, View v) {
        if (v.getMeasuredHeight() <= 0) {
            int specWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int specHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            v.measure(specWidth, specHeight);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        } else {
            Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            v.draw(c);
            return b;
        }
    }

    //!-------------------------------------------------------------------------+
    //!                          Store Location                                 |
    //!-------------------------------------------------------------------------+
    LocationSearchHelper mLocationSearchHelper;
    DatePickerDialog mDatePickerDialog;
    TimePickerDialog mTimePickerDialog;
    ProgressDialog mProgressDialog;
    Calendar mChosenDate;
    HashMap<LatLng, SimpleOpeningAndClosingParser.SimpleOpeningAndClosing> mOCMap;

    void storeLocatorInit() {
        //Get every object from map
        if(mLocationSearchHelper == null)  {
            int res_id = getResources().getIdentifier("raw/cafegoga", "raw", getPackageName());
            InputStream raw = getResources().openRawResource(res_id);
            mLocationSearchHelper = new LocationSearchHelper(new SimpleLocationObject(), raw);
        }
        LatLng center = getCurrentUserLastLatLng();
        List<LSObject> results = mLocationSearchHelper.Search(new LocationSearchHelper.PredicateBuilder()
                .AddWithinRadius(center.latitude, center.longitude, (20d / 100d) * 20000d).Build());
        ClearMarkers();
        addLSOMarkers(results);
        storeLocatorGUI();
    }

    List<LSObject> onlyActiveLSOMarkers() {
        //area specific
        boolean isUnlimited = ((Switch) mSlider.findViewById(R.id.sld_unlimited_distance)).isChecked();
        LocationSearchHelper.PredicateBuilder predicates = new LocationSearchHelper.PredicateBuilder();
        if(!isUnlimited) {
            int progress = ((SeekBar) mSlider.findViewById(R.id.sld_seekbar)).getProgress();
            LatLng center = getCurrentUserLastLatLng();
            predicates.AddWithinRadius(center.latitude, center.longitude, (progress / 100d) * 20000d);
        }
        //wifi specific
        boolean wifiOnly = ((Switch) mSlider.findViewById(R.id.sld_wifi_switch)).isChecked();
        if(wifiOnly) {
            predicates.AddCategoryTrue("wifi");
        }
        //new latter specific
        boolean latteOnly = ((Switch) mSlider.findViewById(R.id.sld_switch_latte)).isChecked();
        if(latteOnly) {
            predicates.AddCategoryTrue("newlatte");
        }
        //date specific
        if(mChosenDate != null && mOCMap != null) {
            predicates.AddPredicate(new Predicate<LSObject>() {
                @Override
                public boolean apply(LSObject o) {
                    return mOCMap.get(o.getLatLng()).IsOpenAt(mChosenDate.get(Calendar.DAY_OF_WEEK),
                            mChosenDate.get(Calendar.HOUR_OF_DAY), mChosenDate.get(Calendar.MINUTE));
                }
            });
        }
        List<LSObject> activeObjects = mLocationSearchHelper.Search(predicates.Build());
        ClearMarkers();
        addLSOMarkers(activeObjects);
        TextView titleView = ((TextView) mSlider.findViewById(R.id.sld_title));
        titleView.setText(getString(R.string.title_sld_search) + " [" + activeObjects.size() + " " + getString(R.string.title_sld_search_counter) + "]");
        return activeObjects;
    }

    void addLSOMarkers(List<LSObject> objects) {
        Bitmap icon = Resize(getResources().getDrawable(R.drawable.marker_coffee), 120, 81);
        for (LSObject lo : objects) {
            LatLng position = new LatLng(lo.latitude, lo.longitude);
            //mAllActiveMarkers.put(position, mGoogleMap.addMarker(new MarkerOptions().icon(bd)
            //                .title(lo.getName()).position(position)));
            XClusterItem<LSObject> newClusterItem = new XClusterItem<>(position.latitude, position.longitude, lo, icon);
            putToLocationObjectDictionary(position, lo);
            putToClusterItemDictionary(position, newClusterItem);
            addToClusterManager(newClusterItem);
        }
        cluster();
    }

    HashMap<LatLng, SimpleOpeningAndClosingParser.SimpleOpeningAndClosing> getLocationObjectOpeningAndClosing(List<LSObject> objects) {
        HashMap<LatLng, SimpleOpeningAndClosingParser.SimpleOpeningAndClosing> resultMap = new HashMap<>();
        SimpleOpeningAndClosingParser parser = SimpleOpeningAndClosingParser.ExampleParser();
        for(LSObject object : objects) {
            SimpleLocationObject location = (SimpleLocationObject) object;
            parser.Parse(location.getOpentime());
            resultMap.put(object.getLatLng(), parser.getResult());
            parser = SimpleOpeningAndClosingParser.ExampleParser();
        }
        return resultMap;
    }

    void storeLocatorGUI() {
        //add the special drawer
        //mSliderPanel
        getLayoutInflater().inflate(R.layout.store_location_detail, mSliderPanel, true);
        final SeekBar seekbar = (SeekBar) mSlider.findViewById(R.id.sld_seekbar);
        seekbar.setProgress(20);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //set unlimited distance to off
                Switch unlimited = (Switch) mSlider.findViewById(R.id.sld_unlimited_distance);
                if (!unlimited.isChecked()) {
                    onlyActiveLSOMarkers();
                } else {
                    Toast.makeText(getApplicationContext(), "You cannot set the distance while Unlimited Distance is on", Toast.LENGTH_LONG);
                }
            }
        });
        mSlider.findViewById(R.id.sld_unlimited_distance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((Switch) v).isChecked();
                if (isChecked) {
                    seekbar.setEnabled(false);
                } else {
                    seekbar.setEnabled(true);
                }
                onlyActiveLSOMarkers();
            }
        });
        mSlider.findViewById(R.id.sld_wifi_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlyActiveLSOMarkers();
            }
        });
        mSlider.findViewById(R.id.sld_switch_latte).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlyActiveLSOMarkers();
            }
        });
        final Calendar today = Calendar.getInstance();
        mSlider.findViewById(R.id.sld_button_open_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog = new DatePickerDialog(getGOGAMapActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mChosenDate = Calendar.getInstance();
                        mChosenDate.set(year, monthOfYear, dayOfMonth);

                        //start time picker
                        mTimePickerDialog = new TimePickerDialog(getGOGAMapActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mChosenDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mChosenDate.set(Calendar.MINUTE, minute);

                                //search if open
                                QuickAsyncTask<Void, Void, HashMap> SearchIfOpen
                                        = new QuickAsyncTask<Void, Void, HashMap>(new QuickAsyncTask.QuickTask<Void, HashMap>() {
                                    @Override
                                    public void Pre() {
                                        mProgressDialog = new ProgressDialog(getGOGAMapActivity());
                                        mProgressDialog.setTitle("Searching");
                                        mProgressDialog.setMessage("Searching for store that will be open");
                                    }

                                    @Override
                                    public HashMap Do(Void... params) {
                                        return getLocationObjectOpeningAndClosing(mLocationSearchHelper.getLSObjects());
                                    }

                                    @Override
                                    public void Post(HashMap param) {
                                        mOCMap = (HashMap<LatLng, SimpleOpeningAndClosingParser.SimpleOpeningAndClosing>) param;
                                        onlyActiveLSOMarkers();
                                        mProgressDialog.dismiss();
                                    }
                                });
                                SearchIfOpen.execute();

                            }
                        }, today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), true);
                        mTimePickerDialog.show();
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
                mDatePickerDialog.show();
            }
        });

        mSlider.findViewById(R.id.sld_open_on_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChosenDate = null;
                mOCMap = null;
                onlyActiveLSOMarkers();
            }
        });
        setForegroundMode(ForegroundMode.STORE_LOCATION);
    }

    //!-------------------------------------------------------------------------+
    //!                          About Developer                                |
    //!-------------------------------------------------------------------------+
    void showAboutDeveloper() {
        String url = "http://www.yumashish.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
