package com.example.yumashish.gogamarkethuddle.GUIAdapters;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.yumashish.gogamarkethuddle.Connector.JSON.PlaceJSON;
import com.example.yumashish.gogamarkethuddle.Kakunin.GOGAMapActivity;
import com.example.yumashish.gogamarkethuddle.R;

/**
 * Created by yumashish on 11/5/15.
 */
public class PlaceDetailLayout {
    public static final String TAG = "GOGA_PLC_DTAIL_LYT";
    public LinearLayout frame, progressBarFrame, detailFrame;
    public RelativeLayout flipperFrame;
    public ViewFlipper imageSwitcher;
    public TextView title, detail;
    public RatingBar rating;
    public ProgressBar loading;
    public GOGAMapActivity context;

    private int increment;

    public PlaceDetailLayout(GOGAMapActivity context, PlaceJSON place, ViewGroup parent) {
        this.context = context;
        frame = (LinearLayout) context.getLayoutInflater().inflate(R.layout.place_detail_info_window, parent);

        imageSwitcher = (ViewFlipper) frame.findViewById(R.id.pdiw_imageSwitcher);

        title = (TextView) frame.findViewById(R.id.pdiw_name);
        detail = (TextView) frame.findViewById(R.id.pdiw_detail);
        rating = (RatingBar) frame.findViewById(R.id.pdiw_rating);
        loading = (ProgressBar) frame.findViewById(R.id.imageLoadProgressBar);
        progressBarFrame = (LinearLayout) frame.findViewById(R.id.imageLoadProgressBarFrame);
        flipperFrame = (RelativeLayout) frame.findViewById(R.id.flipperFrame);
        detailFrame = (LinearLayout) frame.findViewById(R.id.detailFrame);

        title.setText(place.name);
        detail.setText((place.detailText == null) ? "Loading details..." : place.detailText);
        //new LoadPlaceDetailsTask(context, this, place).execute();
    }

    public void SetupProgress(int items) {
        increment = items/100;
    }

    public void AddBitmap(Bitmap b) {
        ImageView imageView = (ImageView) context.getLayoutInflater().inflate(R.layout.simple_image_view, imageSwitcher, false);
        imageView.setImageBitmap(b);
        imageSwitcher.addView(imageView);
        loading.setProgress(loading.getProgress() + increment);
    }

    public void FinishedImageLoading() {
        //disable the progress bar
        if(imageSwitcher.getChildCount() > 0) {
            flipperFrame.removeView(progressBarFrame);

            if (!imageSwitcher.isFlipping()) {
                Log.i(TAG, "ViewFlipper starting...");
                //imagesBundle1.object.imageSwitcher.setAutoStart(true);
                imageSwitcher.setFlipInterval(2000);
                imageSwitcher.startFlipping();
            }
            imageSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getGOGAMapActivity(), "Showing next child (" + imagesBundle1.object.imageSwitcher.getChildCount() + " children)", Toast.LENGTH_LONG).show();
                    imageSwitcher.stopFlipping();
                    imageSwitcher.showNext();
                }
            });
        } else {
            frame.removeView(flipperFrame);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            detailFrame.setLayoutParams(param);
        }
    }

    public void RemoveRating() {
        ((ViewGroup)rating.getParent()).removeView(rating);
    }
}
