package com.yumashish.kakunin;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yumashish.kakunin.External.SimpleLocationObject;
import com.yumashish.kakunin.Tasks.ImageLoadedHandler;
import com.yumashish.kakunin.Tasks.LoadImageTask;

/**
 * Created by lightning on 1/14/16.
 */
public class GogaCafeDetailWindow {
    public static String TAG = "goga_cafe_detail";
    public static String STORE_OBJ = "store_street_view_obj";
    SimpleLocationObject mStore;
    ViewGroup mParent;

    private View findViewById(int res) {
        return mParent.findViewById(res);
    }

    public GogaCafeDetailWindow(final Activity activity, ViewGroup parent, SimpleLocationObject store) {
        ((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.gogacafe_detail_info_window, parent, true);
        mParent = parent;
        mStore = store;

        ((TextView) findViewById(R.id.gdiw_name)).setText(mStore.getName());
        StringBuilder sb = new StringBuilder();
        sb.append(mStore.getAddress() + "\n" + mStore.getOpentime());
        ((TextView) findViewById(R.id.gdiw_detail)).setText(sb.toString());
        Button button = (Button) findViewById(R.id.gdiw_call);
        if (mStore.getTel() != null || mStore.getTel().length() < 1) {
            button.setText(activity.getString(R.string.Call) + " " + mStore.getTel());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mStore.getTel()));
                    try {
                        activity.startActivity(intent);
                    } catch (SecurityException exception) {
                        Toast.makeText(activity, activity.getString(R.string.call_security_message), Toast.LENGTH_LONG);
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("simple text", mStore.getTel()));
                    }
                }
            });
        } else {
            button.setEnabled(false);
            button.setText(activity.getString(R.string.no_phone_number));
        }
        final ImageView svImage = (ImageView)findViewById(R.id.gdiw_streetview_image);
        (svImage).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String svURL = "https://maps.googleapis.com/maps/api/streetview?size=" + svImage.getWidth() + "x" + svImage.getHeight() + "&location=" + mStore.latitude +"," + mStore.longitude
                        + "&heading=151.78&pitch=-0.76&key=";
                Log.i(TAG, "URL: " + svURL);
                svURL += activity.getString(R.string.WAK);
                LoadImageTask loadSVImage = new LoadImageTask(svURL, svImage.getWidth(), svImage.getHeight(), 0);
                loadSVImage.SetImageLoadedHandler(new ImageLoadedHandler() {
                    @Override
                    public void Handle(Bitmap Image) {
                        svImage.setImageBitmap(Image);
                        View prog = findViewById(R.id.gdiw_progress_frame);
                        if(prog != null)
                            ((ViewGroup) prog.getParent()).removeView(prog);
                    }
                });
                loadSVImage.execute();

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    svImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    svImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }
}
