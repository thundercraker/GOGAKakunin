/*
package com.yumashish.kakunin.Tasks;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.yumashish.kakunin.External.SimpleLocationObject;
import com.yumashish.kakunin.GOGAMapActivity;
import com.yumashish.kakunin.R;

*/
/**
 * Created by lightning on 1/14/16.
 *//*

public class LoadGogaCafeDetailsTask implements OnStreetViewPanoramaReadyCallback {
    public static final String TAG = "GOGA_LD_GC_DETAILS_TSK";
    private GOGAMapActivity mContext;
    private SimpleLocationObject mStore;
    private ViewGroup mParent;

    StreetViewPanoramaFragment mStreetViewFrag;

    public LoadGogaCafeDetailsTask(GOGAMapActivity context, ViewGroup parent, SimpleLocationObject store) {
        mContext = context;
        mStore = store;
        mParent = parent;
        context.getLayoutInflater().inflate(R.layout.gogacafe_detail_info_window, mParent, true);
        mStreetViewFrag = (StreetViewPanoramaFragment) context.getFragmentManager().findFragmentById(R.id.gdiw_streetview_fragment);
        //mContext.setActiveGogaCafe(mStore);
        mStreetViewFrag.getStreetViewPanoramaAsync(this);
        ((TextView) mParent.findViewById(R.id.gdiw_name)).setText(store.getName());
        StringBuilder sb = new StringBuilder();
        sb.append(store.getAddress() + "\n" + store.getOpentime());
        ((TextView) mParent.findViewById(R.id.gdiw_detail)).setText(sb.toString());
        Button button = (Button) mParent.findViewById(R.id.gdiw_call);
        if(store.getTel() != null || store.getTel().length() < 1) {
            button.setText(button.getText() + " " + store.getTel());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mStore.getTel()));
                    try {
                        mContext.startActivity(intent);
                    } catch (SecurityException exception) {
                        Toast.makeText(mContext, mContext.getString(R.string.call_security_message), Toast.LENGTH_LONG);
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("simple text", mStore.getTel()));
                    }
                }
            });
        } else {
            button.setEnabled(false);
            button.setText(context.getString(R.string.no_phone_number));
        }
    }

    //public void setActiveGogaCafe(SimpleLocationObject store) {
    //    mStore = store;
    //}

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        try {
            streetViewPanorama.setPosition(mStore.getLatLng());
            View progress = mParent.findViewById(R.id.gdiw_progress_frame);
            ((ViewGroup) progress.getParent()).removeView(progress);
            mStore = null;
        } catch (RuntimeException rte) {
            //ignore this stupid crap
            Log.e(TAG, rte.getMessage());
            rte.printStackTrace();
        }
    }
}
*/
