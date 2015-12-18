package com.example.yumashish.gogamarkethuddle.Connector.JSON;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;

import java.io.Serializable;

/**
 * Created by yumashish on 11/4/15.
 */
public interface LocationObject {
    /*@Key
    public double X;

    @Key
    public double y;

    @Key
    public double Z;

    @Key
    public DateTime logged;
    */

    double getLat();
    double getLng();
}
