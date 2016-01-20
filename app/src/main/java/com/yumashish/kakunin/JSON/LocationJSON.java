package com.yumashish.kakunin.JSON;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;

/**
 * Created by yumashish on 11/4/15.
 */
public class LocationJSON {
    @Key
    public double x;

    @Key
    public double y;

    @Key
    public String logged;

    public String toString() {
        return "(" + x + " " + y + " " + logged + ")";
    }
}
