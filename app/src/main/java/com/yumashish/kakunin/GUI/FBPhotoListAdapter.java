package com.yumashish.kakunin.GUI;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.yumashish.kakunin.KakuninHomeActivity;
import com.yumashish.kakunin.R;
import com.yumashish.kakunin.Tasks.ImageLoadedHandler;
import com.yumashish.kakunin.Tasks.LoadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lightning on 1/20/16.
 */
public class FBPhotoListAdapter extends ArrayAdapter<JSONObject> {
    public static Bundle summaryFieldBundle() {
        Bundle b = new Bundle();
        b.putBoolean("summary", true);
        return b;
    }

    public static String StandardTimeString(Date dateTime) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dateTime);
    }

    public FBPhotoListAdapter(Context context, List<JSONObject> objects) {
        super(context, R.layout.facebook_album_list_child, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.facebook_album_list_child, parent, false);
            JSONObject photo = getItem(position);

            try {
                final String id = photo.getString("id");
                String from = photo.getJSONObject("from").getString("name");
                String time = photo.getString("created_time");

                String photoUrl = photo.getJSONArray("images").getJSONObject(0).getString("source");

                try {
                    if(time != null) {
                        Date dateTime = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").parse(time.split("¥+")[0]);
                        ((TextView) convertView.findViewById(R.id.falc_date_detail)).setText(StandardTimeString(dateTime));
                    } else {
                        throw new ParseException("Date string is null for " + id, 0);
                    }
                } catch (ParseException e) {
                    Log.e(KakuninHomeActivity.TAG, e.getMessage());
                    e.printStackTrace();
                }

                final TextView likeText = (TextView) convertView.findViewById(R.id.falc_like_detail);
                likeText.setText("Loading like details...");
                ((TextView) convertView.findViewById(R.id.falc_from_detail)).setText(from);

                GraphRequest likesRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/" + id + "/likes",
                        summaryFieldBundle(),
                        HttpMethod.GET,
                        new GraphRequest.Callback() {

                            @Override
                            public void onCompleted(GraphResponse response) {
                                JSONObject likes;
                                if((likes = response.getJSONObject()) != null) {
                                    try {
                                        int total = likes.getJSONObject("summary").getInt("total_count");
                                        JSONArray data = likes.getJSONArray("data");
                                        int i;
                                        String likeString = "";
                                        for(i = 0; i < Math.min(3, total); i++) {
                                            JSONObject like = data.getJSONObject(i);
                                            likeString += like.getString("name") + ", ";
                                        }
                                        if (i > 0) {
                                            likeString = likeString.substring(0, likeString.length() - 2);
                                            if(i < total) {
                                                likeString = " and " + (total - i) + " others";
                                            }
                                        } else {
                                            likeString = "No likes yet";
                                        }
                                        likeText.setText(likeString);
                                    } catch (JSONException e) {
                                        Log.e(KakuninHomeActivity.TAG, e.getMessage());
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e(KakuninHomeActivity.TAG, "Empty likes response for " + id);
                                }
                            }
                        });
                likesRequest.executeAsync();

                final ImageView photoIV = (ImageView) convertView.findViewById(R.id.falc_main_image);
                new LoadImageTask(photoUrl, 0, 0, 0, new ImageLoadedHandler() {
                    @Override
                    public void Handle(Bitmap Image) {
                        photoIV.setImageBitmap(Image);
                    }
                });

            } catch (JSONException e) {
                Log.e(KakuninHomeActivity.TAG, e.getMessage());
                e.printStackTrace();
            }
        }

        return convertView;
    }
}
