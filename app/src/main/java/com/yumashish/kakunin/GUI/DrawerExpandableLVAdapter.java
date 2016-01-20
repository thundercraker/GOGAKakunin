package com.yumashish.kakunin.GUI;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.yumashish.kakunin.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by yumashish on 11/2/15.
 */
public class DrawerExpandableLVAdapter extends BaseExpandableListAdapter {
    private ArrayList<ArrayList<String>> mListViewLabels;

    public ArrayList<ArrayList<String>> getLabels() {
        return mListViewLabels;
    }

    private Activity mContext;
    private Hashtable<Integer, View> mGroupHeaderViews;

    public DrawerExpandableLVAdapter(Activity context, String[] listviewLabels) {
        mListViewLabels = new ArrayList<>();
        mGroupHeaderViews = new Hashtable<>();
        mContext = context;
        for (String lvLabel:listviewLabels) {
            ArrayList<String> groupItems = new ArrayList<>();
            String[] itemLabels = lvLabel.split(",");
            for (String itemLabel:itemLabels) {
                groupItems.add(itemLabel.trim());
            }
            mListViewLabels.add(groupItems);
        }
    }

    @Override
    public int getGroupCount() {
        return mListViewLabels.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListViewLabels.get(groupPosition).size() - 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListViewLabels.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListViewLabels.get(groupPosition).get(childPosition + 1);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View groupHeaderView = (mGroupHeaderViews.containsKey(groupPosition)) ? mGroupHeaderViews.get(groupPosition) : null;
        if(groupHeaderView == null) {
            //get the label type, the first character being one of these:
            //
            // Nothing: normal drawer type
            // * <- Selectable Group Label
            // & <- Information Label
            String label = mListViewLabels.get(groupPosition).get(0);
            char firstChar = label.charAt(0);
            if(firstChar == '*' || firstChar == '!')
                label = label.substring(1, label.length());

            Log.d("DrawerAdapter", "Convert view null for " + label + " which first char " + firstChar);
            int d = 0;
            switch (firstChar) {
                case '*':
                    groupHeaderView = mContext.getLayoutInflater().inflate(R.layout.drawable_list_item_select, parent, false);
                    d=1;
                    break;
                case '!':
                    groupHeaderView = mContext.getLayoutInflater().inflate(R.layout.drawer_list_item_info, parent, false);
                    d=2;
                    break;
                default:
                    groupHeaderView = mContext.getLayoutInflater().inflate(R.layout.drawer_list_item, parent, false);
                    d=0;
                    break;
            }

            TextView labelTV = (TextView) groupHeaderView.findViewById(R.id.drawer_list_item_text);
            labelTV.setText(label);

            Log.d("DrawerAdapter", "Convertview created for " + label + " case " + d);
            mGroupHeaderViews.put(groupPosition, groupHeaderView);
        }

        return groupHeaderView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mContext.getLayoutInflater().inflate(R.layout.drawer_list_child, parent, false);
        }
        TextView labelView = (TextView) convertView.findViewById(R.id.drawer_list_item_text);
        labelView.setText(mListViewLabels.get(groupPosition).get(childPosition + 1));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
