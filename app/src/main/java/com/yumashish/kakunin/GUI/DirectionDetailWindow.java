package com.yumashish.kakunin.GUI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yumashish.kakunin.GOGAMapActivity;
import com.yumashish.kakunin.JSON.DirectionsJSON;
import com.yumashish.kakunin.KakuninHomeActivity;
import com.yumashish.kakunin.R;

/**
 * Created by lightning on 12/25/15.
 */
public class DirectionDetailWindow {
    GOGAMapActivity mContext;
    DirectionsJSON mDirections;
    DirectionsJSON.Route mSelectedRoute;
    RelativeLayout mLayout;

    TextView vLengthTime, vFromDetail, vToDetail;
    ListView vListView;

    public DirectionDetailWindow(GOGAMapActivity context, ViewGroup parent, DirectionsJSON directionsJSON, int selectedRoute) {
        mContext    = context;
        mDirections = directionsJSON;

        context.getLayoutInflater().inflate(R.layout.directions_list_window, parent, true);
        mLayout = (RelativeLayout) parent.findViewById(R.id.ddl_layout);

        vLengthTime = (TextView) mLayout.findViewById(R.id.ddl_length_time);
        vFromDetail = (TextView) mLayout.findViewById(R.id.ddl_from_detail);
        vToDetail   = (TextView) mLayout.findViewById(R.id.ddl_to_detail);
        vListView   = (ListView) mLayout.findViewById(R.id.ddl_directions_list);

        if(directionsJSON.routes.size() > selectedRoute && directionsJSON.routes.get(selectedRoute).legs.size() > 0) {
            mSelectedRoute = directionsJSON.routes.get(selectedRoute);
            vLengthTime.setText(mSelectedRoute.summary);
            vFromDetail.setText(mSelectedRoute.legs.get(0).start_address);
            vToDetail.setText(mSelectedRoute.legs.get(mSelectedRoute.legs.size() - 1).end_address);
            vListView.setAdapter(new DirectionListViewAdapter(context, mSelectedRoute));
        }
    }
}
