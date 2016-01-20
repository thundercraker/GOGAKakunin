package com.yumashish.kakunin.GUI;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.yumashish.kakunin.GOGAMapActivity;
import com.yumashish.kakunin.JSON.DirectionsJSON;
import com.yumashish.kakunin.KakuninHomeActivity;
import com.yumashish.kakunin.R;
import com.yumashish.kakunin.StreetViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lightning on 12/25/15.
 */
public class DirectionListViewAdapter extends BaseAdapter {
    DirectionsJSON.Route mRoute;
    List<DirectionsJSON.Route.Leg.Step> mAllSteps;
    GOGAMapActivity mContext;
    LayoutInflater mLayoutInflater;

    public DirectionListViewAdapter(GOGAMapActivity application, DirectionsJSON.Route route) {
        mRoute = route;
        mContext = application;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mAllSteps = new ArrayList<>();
        for (DirectionsJSON.Route.Leg leg : mRoute.legs) {
            mAllSteps.addAll(leg.steps);
        }
    }

    @Override
    public int getCount() {
        return mAllSteps.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllSteps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.directions_detail_listview_point_element, parent, false);

            final DirectionsJSON.Route.Leg.Step step = (DirectionsJSON.Route.Leg.Step) getItem(position);

            ImageView directionIcon = (ImageView) convertView.findViewById(R.id.ddl_direction_icon);
            TextView distance = (TextView) convertView.findViewById(R.id.ddl_distance);
            TextView main = (TextView) convertView.findViewById(R.id.ddl_main_text);
            ImageView streetview = (ImageView) convertView.findViewById(R.id.dd_streetview);
            streetview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Streetview hook: " + step.start_location, Toast.LENGTH_LONG);
                    Log.d(KakuninHomeActivity.TAG, "Streetview hook: " + step.start_location);
                    StreetViewActivity.StartActivity(mContext, step.start_location.getLatLng());
                }
            });

            distance.setText(step.distance.text);
            if(step.html_instructions != null) {
                directionIcon.setImageDrawable(getDirectionIcon(Html.fromHtml(step.html_instructions).toString()));
                //main.setText(Html.fromHtml(step.html_instructions).toString());
                main.setText(Html.fromHtml(step.html_instructions).toString());
                Log.d(KakuninHomeActivity.TAG, "Spanned to String: " + Html.fromHtml(step.html_instructions).toString() + " Raw " + step.html_instructions);
            } else {
                directionIcon.setImageDrawable(getDirectionIconFromType(DirectionType.UNKNOWN));
                main.setText("No instructions");
            }
        }

        return convertView;
    }

    public enum DirectionType { HEAD_ON, TURN_RIGHT, TURN_LEFT, SLIGHT_RIGHT, SLIGHT_LEFT, UNKNOWN };

    Drawable getDirectionIcon(String direction) {
        if(direction.toLowerCase().indexOf(mContext.getString(R.string.direction_head_substring)) == 0) {
            return getDirectionIconFromType(DirectionType.HEAD_ON);
        } else if(direction.toLowerCase().indexOf(mContext.getString(R.string.direction_left_substring)) == 0) {
            return getDirectionIconFromType(DirectionType.TURN_LEFT);
        } else if(direction.toLowerCase().indexOf(mContext.getString(R.string.direction_right_substring)) == 0) {
            return getDirectionIconFromType(DirectionType.TURN_RIGHT);
        } else if(direction.toLowerCase().indexOf(mContext.getString(R.string.direction_sleft_substring)) == 0) {
            return getDirectionIconFromType(DirectionType.SLIGHT_LEFT);
        } else if(direction.toLowerCase().indexOf(mContext.getString(R.string.direction_sright_substring)) == 0) {
            return getDirectionIconFromType(DirectionType.SLIGHT_RIGHT);
        }
        return getDirectionIconFromType(DirectionType.UNKNOWN);
    }

    private Drawable getDirectionIconFromType(DirectionType direction) {
        int res_id;
        switch (direction) {
            case HEAD_ON:
                res_id = R.drawable.light_up;
                break;
            case TURN_RIGHT:
                res_id = R.drawable.light_right;
                break;
            case TURN_LEFT:
                res_id = R.drawable.light_left;
                break;
            case SLIGHT_LEFT:
                res_id = R.drawable.light_left;
                break;
            case SLIGHT_RIGHT:
                res_id = R.drawable.light_right;
                break;
            default:
                res_id = R.drawable.light_marker_1;
                break;
        }
        return mContext.getResources().getDrawable(res_id);
    }
}
