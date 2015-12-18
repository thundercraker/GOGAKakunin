package com.example.yumashish.gogamarkethuddle;

import android.view.View;
import android.widget.ExpandableListView;

/**
 * Created by yumashish on 11/5/15.
 */
public interface DrawerHandler {
    public void groupSelected(View v, int childPosition, long id);
    public void childSelected(ExpandableListView parent, View v, int groupPosition, int childPosition, long id);
}
