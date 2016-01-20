//package com.example.yumashish.gogamarkethuddle;
//
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.location.Location;
//import android.os.Bundle;
//import android.os.SystemClock;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ExpandableListView;
//import android.widget.RelativeLayout;
//
//import com.yumashish.kakunin.GUI.Maps.XClusterItem;
//import com.yumashish.kakunin.GOGAMapActivity;
//import com.yumashish.kakunin.GUI.SwipeCardsController;
//import com.yumashish.kakunin.Interfaces.Toastmaster;
//import com.yumashish.kakunin.Tasks.LoadImageTask;
//import com.yumashish.kakunin.Tasks.SearchForPlacesTask;
//import com.example.yumashish.gogamarkethuddle.Search.SearchForSellersTask;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
///**
// * Created by yumashish on 10/29/15.
// */
//public class CustomerHomeActivity extends GOGAMapActivity implements Toastmaster {
//    public final static String TAG = "GOGA_CUSTOMER_HOME";
//    public final static int STATION_SEARCH_RADIUS = 50000;
//
//    protected static int content_frame_layout = R.layout.home_map_content, drawer_items_array = R.array.customer_category_selections;
//
//    CustomerHomeActivity mContext;
//    RelativeLayout mHomeHiddenForeground;
//    SwipeCardsController mSwipeCardController;
//    DatabaseConnector.HttpRequestTask mDealsLoader;
//
//    ProgressDialog mProgressDialog;
//
//    class DealTapCard implements SwipeCardsController.TapCardInterface {
//        public DealJSON mDeal;
//        public Bitmap image;
//        LoadImageTask mPictureRetreiver;
//
//        public DealTapCard(DealJSON deal)
//        {
//            mDeal = deal;
//            mPictureRetreiver = new LoadImageTask(mDeal.picture, 0, 0, 0);
//        }
//
//        @Override
//        public LoadImageTask getPictureRetreiver() {
//            return mPictureRetreiver;
//        }
//
//        @Override
//        public String getName() {
//            return mDeal.name;
//        }
//
//        @Override
//        public int getId() {
//            return mDeal.id;
//        }
//
//        @Override
//        public void setImage(Bitmap b) {
//            image = b;
//        }
//
//        @Override
//        public String getLine1() {
//            return mDeal.discount_min + "% to " + mDeal.discount_max;
//        }
//
//        @Override
//        public String getLine2() {
//            return "From " + mDeal.when_end + " to " + mDeal.when_start;
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstance) {
//        super.onCreate(savedInstance);
//        mContext = this;
//
//        final RelativeLayout MainDealsLayout = (RelativeLayout) findViewById(R.id.home_foreground_0);
//
//        mProgressDialog = ProgressDialog.show(this, "Loading latest deals", "", true, true, new DialogInterface.OnCancelListener()
//        {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                mProgressDialog.dismiss();
//                mDealsLoader.cancel(true);
//            }
//        });
//
//        mDealsLoader = new DatabaseConnector.HttpRequestTask(new DatabaseConnector.HttpRequestDelegate() {
//             @Override
//             public InputStream Task() {
//                 try {
//                     return getDatabaseConnector().LoadAllDeals();
//                 } catch (IOException ioe) {
//                     DatabaseConnector.LogAndFlush(TAG, ioe);
//                     return null;
//                 }
//             }
//
//             @Override
//             public void Handler(InputStream arg) {
//                if(arg != null) {
//                    try {
//                        DatabaseConnector.DealsResponseJson dealsResponseJson = getDatabaseConnector().ParseAndClose(arg, DatabaseConnector.DealsResponseJson.class);
//                        List<SwipeCardsController.TapCardInterface> dealCards = new ArrayList<>();
//                        for (DealJSON deal:dealsResponseJson.result) {
//                            dealCards.add(new DealTapCard(deal));
//                        }
//
//                        mHomeHiddenForeground = (RelativeLayout) findViewById(R.id.home_foreground_0);
//                        mSwipeCardController = new SwipeCardsController(getApplicationContext(), mHomeHiddenForeground, dealCards);
//                        mContext.setUpClusterer();
//
//                        //final List<LatLng> positions = new ArrayList<>();
//
//                        mSwipeCardController.SetOnAcceptOrRejectHandler(new SwipeCardsController.OnAcceptOrRejectHandler() {
//                            @Override
//                            public void OnAccept(SwipeCardsController.TapCardInterface card) {
//                                DealTapCard dtc = (DealTapCard) card;
//                                Log.d(TAG, "Accepted: " + dtc.getName() + " @ " + dtc.mDeal.location);
//
//                                //Add Markers for these places
//                                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.fire_ball_icon_hi);
//                                b = Bitmap.createScaledBitmap(b, 70, 70, false);
//                                XClusterItem<DealJSON> dealClusterItem = new XClusterItem<DealJSON>(dtc.mDeal.location.x, dtc.mDeal.location.y, dtc.mDeal, b);
//                                //addToClusterManagerWhenReady(dealClusterItem, dealClusterItem.getLoad(), b);
//                                LatLng position = new LatLng(dtc.mDeal.location.x, dtc.mDeal.location.y);
//                                mContext.addToClusterManager(dealClusterItem);
//                                mContext.putToLocationObjectDictionary(position, dtc.mDeal);
//                                //positions.add(position);
//                            }
//
//                            @Override
//                            public void OnReject(SwipeCardsController.TapCardInterface card) {
//                                DealTapCard dtc = (DealTapCard) card;
//                                Log.d(TAG, "Rejected: " + dtc.getName() + " @ " + dtc.mDeal.location);
//                            }
//                        });
//
//                        mSwipeCardController.SetOnFinishHandler(new SwipeCardsController.OnFinished() {
//                            @Override
//                            public void onFinish() {
//                                Log.d(TAG, "Finished swipe card controller");
//                                cluster();
//                                /*Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.fire_ball_icon_hi);
//                                b = Bitmap.createScaledBitmap(b, 70, 70, false);
//                                for (LatLng position:positions) {
//                                    mContext.redrawMarker(position, b);
//                                }*/
//                            }
//                        });
//
//                        mProgressDialog.dismiss();
//                    } catch (IOException ioe) {
//                        DatabaseConnector.LogAndFlush(TAG, ioe);
//                    }
//                }
//             }
//
//            @Override
//            public void OnCancelled() {
//                ((ViewGroup)MainDealsLayout.getParent()).removeView(MainDealsLayout);
//                mContext.getDatabaseConnector().CancelCurrentRequest();
//            }
//        });
//        mDealsLoader.execute();
//
//        StartMessageCheckerService();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        CloseApplication();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(mAutoCompleteLayer.getVisibility() == View.VISIBLE) {
//            HideAutoCompleteLayer();
//        }
//        super.onBackPressed();
//    }
//
//    protected void CloseApplication() {
//        StopMessageCheckerService();
//    }
//
//    public void CancelRepeatingUpdate() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(this, ChatMonitorService.class);
//        i.putExtra(ChatMonitorService.SCHEDULED_TASK_KEY, true);
//        i.putExtra(ChatMonitorService.USER_ID_KEY, mCurrentUser.id);
//        PendingIntent pending = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
//        alarmManager.cancel(pending);
//        Log.i(TAG, "Cancelled next update");
//    }
//
//    public void ScheduleRepeatingUpdate() {
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent i = new Intent(this, ChatMonitorService.class);
//        i.putExtra(ChatMonitorService.SCHEDULED_TASK_KEY, true);
//        i.putExtra(ChatMonitorService.USER_ID_KEY, mCurrentUser.id);
//        PendingIntent pending = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
//        //Calendar cal = Calendar.getInstance();
//        //cal.add(Calendar.SECOND, 0);
//        Log.i(TAG, "Setting update checker start " + (SystemClock.elapsedRealtime() + ChatMonitorService.CHAT_MONITOR_REPEAT_TIME) + " repeat every " + ChatMonitorService.CHAT_MONITOR_REPEAT_TIME);
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + ChatMonitorService.CHAT_MONITOR_REPEAT_TIME, ChatMonitorService.CHAT_MONITOR_REPEAT_TIME, pending);
//        Log.i(TAG, "Set repeated message update intent");
//    }
//
//    @Override
//    protected int getDrawerItemsArrayId() {
//        return R.array.customer_category_selections;
//    }
//
//    @Override
//    protected void childSelected(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//        List<String> selectedItems = new ArrayList<>();
//        selectedItems.add(groupPosition + " " + childPosition);
//        SelectDrawerItemHTTP(selectedItems);
//        CloseDrawer();
//    }
//
//    @Override
//    protected void groupSelected(View v, int groupPosition, long id) {
//        Log.i(TAG, "Selected group position: " + groupPosition);
//        switch (groupPosition) {
//            case 4:
//                Intent intent = new Intent(this, ChatMainWindow.class);
//                intent.putExtra(INTENT_CURRENT_USER_TAG, mCurrentUser.id);
//                startActivity(intent);
//                CloseDrawer();
//                break;
//            case 5:
//                mCurrentMapMode = MapMode.SHOUT_CIRCLE;
//                CloseDrawer();
//                break;
//            case 6:
//                if(mAutoCompleteLayer.getVisibility() == View.VISIBLE) {
//                    HideAutoCompleteLayer();
//                } else {
//                    ShowAutoCompleteLayer();
//                }
//                CloseDrawer();
//                break;
//            case 7:
//                showMapTypeSelectorDialog();
//                CloseDrawer();
//                break;
//            case 8:
//                ToggleHeatMap();
//                CloseDrawer();
//                break;
//        }
//    }
//
//    private void SelectDrawerItemHTTP(Collection<String> ItemNumbers) {
//        mGoogleMap.clear();
//        ArrayList<String> sfilters = new ArrayList<String>();
//        for (String ItemNumberText: ItemNumbers) {
//            String[] ItemNumberTokens = ItemNumberText.split(" ");
//            int groupId = Integer.parseInt(ItemNumberTokens[0].trim());
//            int childId = Integer.parseInt(ItemNumberTokens[1].trim());
//            switch (groupId) {
//                case 0:
//                    sfilters.add("electronics_store");
//                    SearchForTypeHTTP(sfilters);
//                    break;
//                case 1:
//                    switch (childId) {
//                        case 0:
//                            sfilters.add("clothing_store");
//                            SearchForTypeHTTP(sfilters);
//                            break;
//                        case 1:
//                            sfilters.add("clothing");
//                            SearchForSeller(null, 1000d, sfilters);
//                            break;
//                    }
//                    break;
//                case 2:
//                    switch (childId) {
//                        case 1:
//                            sfilters.add("atm");
//                            sfilters.add("bank");
//                            sfilters.add("convenience_store");
//                            SearchForTypeHTTP(sfilters);
//                            break;
//                    }
//                    break;
//                case 3:
//                    sfilters.add("subway_station");
//                    sfilters.add("train_station");
//                    switch (childId) {
//                        case 0:
//                            //info
//                            break;
//                        case 1:
//                            //find JR Stations
//                            SearchForHTTP("JR", null, null, STATION_SEARCH_RADIUS, sfilters);
//                            break;
//                        case 2:
//                            //find Keio Stations
//                            SearchForHTTP(null, "Keio", null, STATION_SEARCH_RADIUS, sfilters);
//                            break;
//                        case 3:
//                            //find Tokyo Metro Stations
//                            SearchForHTTP(null, "tokyo metro", null, STATION_SEARCH_RADIUS, sfilters);
//                            break;
//                        case 4:
//                            //find Tokyu Stations
//                            SearchForHTTP(null, "tokyu", null, STATION_SEARCH_RADIUS, sfilters);
//                            break;
//                        case 5:
//                            //find All Stations
//                            SearchForHTTP(null, null, null, STATION_SEARCH_RADIUS, sfilters);
//                            break;
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    private void SearchForTypeHTTP(List<String> placeTypes) {
//        LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
//        float[] distarr = new float[1];
//        Location.distanceBetween(bounds.northeast.latitude,
//                bounds.northeast.longitude,
//                bounds.southwest.latitude,
//                bounds.southwest.longitude,
//                distarr);
//        float radius = distarr[0] / 2f;
//
//        StringBuilder typesBuilder = new StringBuilder();
//        typesBuilder.append(placeTypes.get(0));
//        for (int i = 1; i < placeTypes.size(); i++) {
//            typesBuilder.append("|" + placeTypes.get(i));
//        }
//        Log.i(TAG, "Searching for types " + typesBuilder.toString());
//        SearchForPlacesTask asyncQuery = new SearchForPlacesTask(this, null, null, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), radius, typesBuilder.toString());
//        asyncQuery.execute();
//    }
//
//    private void SearchForHTTP(String name, String keyword, LatLng location, double radius, List<String> placeTypes) {
//        if(radius <= 50){
//            LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
//            float[] distarr = new float[1];
//            Location.distanceBetween(bounds.northeast.latitude,
//                    bounds.northeast.longitude,
//                    bounds.southwest.latitude,
//                    bounds.southwest.longitude,
//                    distarr);
//            radius = distarr[0] / 2f;
//        }
//        StringBuilder typesBuilder = new StringBuilder();
//        typesBuilder.append(placeTypes.get(0));
//        for (int i = 1; i < placeTypes.size(); i++) {
//            typesBuilder.append("|" + placeTypes.get(i));
//        }
//        if(location == null) {
//            location = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
//        }
//        SearchForPlacesTask asyncQuery = new SearchForPlacesTask(this, name, keyword, location.latitude, location.longitude, radius, typesBuilder.toString());
//        asyncQuery.execute();
//    }
//
//    public void SearchForSeller(LatLng bias, double radius, List<String> sellerTypes) {
//        LatLng location = (bias == null) ?
//                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()) :
//                bias;
//        SearchForSellersTask asyncQuery = new SearchForSellersTask(this, location, radius, sellerTypes);
//        asyncQuery.execute();
//    }
//
//    public void StartMessageCheckerService() {
//        long lastChecked = 0; //TODO store the last update time
//
//        /*Intent serviceIntent = new Intent(this, ChatMonitorService.class);
//        serviceIntent.putExtra(ChatMonitorService.START_SCHEDULING_KEY, true);
//        serviceIntent.putExtra(ChatMonitorService.USER_ID_KEY, mCurrentUser.id);
//        serviceIntent.putExtra(ChatMonitorService.LAST_CHECK_KEY, lastChecked);*/
//        Log.i(TAG, "Starting the message checker service");
//        //startService(serviceIntent);
//        ScheduleRepeatingUpdate();
//    }
//
//    public void StopMessageCheckerService() {
//        //Intent serviceIntent = new Intent(this, ChatMonitorService.class);
//        Log.i(TAG, "Stop the message checker service");
//        //serviceIntent.putExtra(ChatMonitorService.STOP_SCHEDULING_KEY, true);
//        //startService(serviceIntent);
//        CancelRepeatingUpdate();
//    }
//}
