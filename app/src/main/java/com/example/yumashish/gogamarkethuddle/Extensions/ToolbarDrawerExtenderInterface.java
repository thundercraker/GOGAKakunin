package com.example.yumashish.gogamarkethuddle.Extensions;

import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

/**
 * Created by lightning on 11/11/15.
 */
public interface ToolbarDrawerExtenderInterface {
    void SetContent(FrameLayout contentFrame);
    int getContentLayoutId();
    int getDrawerItemsArrayId();
    int getToolbarId();
    void groupSelected(View v, int groupPosition, long id);
    void childSelected(ExpandableListView parent, View v, int groupPosition, int childPosition, long id);
}
