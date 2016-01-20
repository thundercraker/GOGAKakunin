package com.yumashish.kakunin.External;

/**
 * Created by lightning on 1/12/16.
 */

import com.yumashish.kakunin.Interfaces.LocationObject;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.Serializable;

/**
 * Created by lightning on 1/12/16.
 */
public class SimpleLocationObject extends LSObject implements Serializable {
    public long key;
    public String address;
    public String pub_start;
    public String pub_end;
    public String tel;
    public String opentime;
    public int wifi;
    public int newlatte;

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPub_start() {
        return pub_start;
    }

    public void setPub_start(String pub_start) {
        this.pub_start = pub_start;
    }

    public String getPub_end() {
        return pub_end;
    }

    public void setPub_end(String pub_end) {
        this.pub_end = pub_end;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public int getWifi() {
        return wifi;
    }

    public void setWifi(int wifi) {
        this.wifi = wifi;
    }

    public int getNewlatte() {
        return newlatte;
    }

    public void setNewlatte(int newlatte) {
        this.newlatte = newlatte;
    }

    @Override
    public CellProcessor[] getCellProcessors() {
        return new CellProcessor[] {
                new NotNull(new ParseLong()), //key
                new NotNull(), //name
                new NotNull(new ParseDouble()), //lat
                new NotNull(new ParseDouble()), //long
                new Optional(), //address
                new Optional(),
                new Optional(),
                new Optional(), //telephone
                new Optional(), //open time
                new NotNull(new ParseInt()),
                new NotNull(new ParseInt())
        };
    }
}

