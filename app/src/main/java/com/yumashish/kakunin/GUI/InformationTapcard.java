package com.yumashish.kakunin.GUI;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yumashish.kakunin.GOGAMapActivity;
import com.yumashish.kakunin.R;

/**
 * Created by lightning on 12/18/15.
 */
public class InformationTapcard implements SwipeCardsController.TapCardInterface {
    int mId;
    String mTitle;
    CharSequence mInfoString;
    Drawable mImage;
    float weight_info = 0.7f, weight_image = 0.2f;

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.swippable_info_card;
    }

    @Override
    public AsyncTask modifyTapCardView(View card) {
        TextView title  = (TextView) card.findViewById(R.id.sic_title);
        TextView info   = (TextView) card.findViewById(R.id.sic_info);
        ImageView image = (ImageView) card.findViewById(R.id.sic_image);

        title.setText(mTitle);
        info.setText(mInfoString.toString());
        if(image != null) {
            image.setImageDrawable(mImage);

            LinearLayout.LayoutParams infoLp = (LinearLayout.LayoutParams) info.getLayoutParams();
            infoLp.weight = weight_info;
            info.setLayoutParams(infoLp);

            LinearLayout.LayoutParams imageLp = (LinearLayout.LayoutParams) image.getLayoutParams();
            imageLp.weight = weight_image;
            image.setLayoutParams(imageLp);
            card.invalidate();
        } else {
            Log.d(GOGAMapActivity.TAG, "Tapcard designer: No image for tapcard");
            ((ViewGroup) card).removeView(image);
            LinearLayout.LayoutParams infoLp = (LinearLayout.LayoutParams) info.getLayoutParams();
            infoLp.weight = 0.9f;
            info.setLayoutParams(infoLp);
            card.invalidate();
        }

        return null;
    }

    public InformationTapcard() {
        mId = SwipeCardsController.ID_UNIQUE++;
    }

    public InformationTapcard(String title, CharSequence infoString, Drawable image) {
        mId = SwipeCardsController.ID_UNIQUE++;
        mTitle = title;
        mInfoString = infoString;
        mImage = image;
    }
}
