package com.yumashish.kakunin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by lightning on 12/28/15.
 */
public class StreetViewActivity extends AppCompatActivity
        implements OnStreetViewPanoramaReadyCallback {
    public static String TAG = "goga_street_view";
    public static String STREET_VIEW_POSITION_LAT = "street_view_position_lat";
    public static String STREET_VIEW_POSITION_LNG = "street_view_position_lng";
    LatLng mStartPosition;

    Toolbar mToolBar;
    TextView mTitleTextView;

    public static void StartActivity(Activity context, LatLng position) {
        Intent intent = new Intent(context, StreetViewActivity.class);
        Bundle b = new Bundle();
        b.putDouble(STREET_VIEW_POSITION_LAT, position.latitude);
        b.putDouble(STREET_VIEW_POSITION_LNG, position.longitude);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view_layout);

        mToolBar = (Toolbar) findViewById(R.id.home_action_bar);
        mTitleTextView = (TextView) findViewById(R.id.home_title);
        setSupportActionBar(mToolBar);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.streetviewpanorama);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        if(data.containsKey(STREET_VIEW_POSITION_LAT) &&
                data.containsKey(STREET_VIEW_POSITION_LNG)) {
            mStartPosition = new LatLng(data.getDouble(STREET_VIEW_POSITION_LAT),
                    data.getDouble(STREET_VIEW_POSITION_LNG));
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fatal Error");
            builder.setMessage("No location was provided for the street view");
            builder.setCancelable(true);
            builder.show();
        }

        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer_menu_icon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setToolbarTitle(getTitle().toString());
    }

    protected void setToolbarTitle(String title) {
        Log.i(TAG, "Setting title to " + title);
        mTitleTextView.setText(title);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(mStartPosition);
    }
}
