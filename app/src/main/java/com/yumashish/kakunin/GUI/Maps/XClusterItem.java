package com.yumashish.kakunin.GUI.Maps;

import android.graphics.Bitmap;

/**
 * Created by lightning on 12/15/15.
 */
public class XClusterItem<T> extends MapClusterItem {
    T load;
    public Bitmap icon;

    public XClusterItem(double lat, double lng, T load, Bitmap icon) {
        super(lat, lng);
        this.load = load;
        this.icon = icon;
    }

    public T getLoad() {
        return load;
    }
}
