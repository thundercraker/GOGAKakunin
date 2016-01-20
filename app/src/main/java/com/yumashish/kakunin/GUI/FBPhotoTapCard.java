package com.yumashish.kakunin.GUI;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.yumashish.kakunin.R;
import com.yumashish.kakunin.Tasks.ImageLoadedHandler;
import com.yumashish.kakunin.Tasks.LoadImageTask;

/**
 * Created by lightning on 1/15/16.
 */
public class FBPhotoTapCard implements SwipeCardsController.TapCardInterface {
    int mId;
    String mImageUrl;

    public FBPhotoTapCard(String imageUrl) {
        mId = SwipeCardsController.ID_UNIQUE++;
        mImageUrl = imageUrl;
    }

    @Override
    public int getId() {
        return mId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.swippable_fb_photo_card;
    }

    @Override
    public AsyncTask modifyTapCardView(final View card) {
        LoadImageTask imageTask = new LoadImageTask(mImageUrl, 0, 0, 0, new ImageLoadedHandler() {
            @Override
            public void Handle(Bitmap Image) {
                ((ImageView)card.findViewById(R.id.fb_photo_imageview)).setImageBitmap(Image);
            }
        });
        imageTask.execute();
        return null;
    }
}
