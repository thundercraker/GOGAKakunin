package com.example.yumashish.gogamarkethuddle.Kakunin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yumashish.gogamarkethuddle.GUIAdapters.DrawerExpandableLVAdapter;
//import com.example.yumashish.gogamarkethuddle.LaunchActivity;
import com.example.yumashish.gogamarkethuddle.R;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Created by yumashish on 11/5/15.
 */
public abstract class ToolbarDrawerActivity extends AppCompatActivity {
    public static final String TAG = "GOGA_TOOLBAR_DRAWER_ACT";

    public static boolean ACTIVE = false;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mLeftDrawerList;
    private FrameLayout mContentFrame;
    private Toolbar mToolBar;
    private TextView mTitleTextView;
    private LinearLayout mToolBarMenu;
    private ActionBarDrawerToggle mDrawerToggle;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @OverridingMethodsMustInvokeSuper
    protected void SetContent(FrameLayout contentFrame) {
        getLayoutInflater().inflate(getContentLayoutId(), contentFrame);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.home_drawer);

        ACTIVE = true;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_menu_layout);
        SetupDrawer();

        mToolBar = (Toolbar) findViewById(R.id.home_action_bar);//getToolbarId());
        setSupportActionBar(mToolBar);

        mTitleTextView = (TextView) findViewById(R.id.home_title);
        mToolBarMenu = (LinearLayout) findViewById(R.id.action_bar_menu_container);

        //resize content_frame by measuring the toolbar first
        mContentFrame = (FrameLayout) findViewById(R.id.content_frame);

        //Log.i(TAG, "Height calc " + mToolBar.getHeight() + " " + mDrawerLayout.getHeight() + " " + mContentFrame.getHeight());

        //mContentFrame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        SetContent(mContentFrame);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                //getActionBar(),
                mToolBar,
                //R.drawable.ic_actionbar_menu_icon,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getSupportActionBar().setTitle("Closed");
                //invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                //getSupportActionBar().setTitle("Open");
                //invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.gogaactionbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setToolbarTitle(getTitle().toString());

        remakeToolbarIcons(getInitialToolBarMenu());
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onResume() {
        super.onResume();

        if(getIntentFilter() != null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    HandleBroadcastIntent(context, intent);
                }
            };
            registerReceiver(mBroadcastReceiver, getIntentFilter());
            Log.i(TAG, "Registered a broadcast recevier");
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onPause() {
        super.onPause();
        if(getIntentFilter() != null) {
            unregisterReceiver(mBroadcastReceiver);
            Log.i(TAG, "Unregistered broadcast receiver");
        }
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    protected void onStop() {
        super.onStop();
        ACTIVE = false;
    }

    @Override
    public void startActivity(Intent intent) {
        //register as parent

        Bundle extras = intent.getExtras();
        super.startActivity(intent);
    }

    public static final String INTENT_BACK_FLAG = "INTENT_BLACK_FLAG";

    @Override
    public void onBackPressed() {
    }

    @OverridingMethodsMustInvokeSuper
    protected void SetupDrawer() {
        //TODO
        mLeftDrawerList = (ExpandableListView) findViewById(R.id.left_drawer_listview);
        mLeftDrawerList.setGroupIndicator(null);
        mLeftDrawerList.setAdapter(new DrawerExpandableLVAdapter(this, getResources().getStringArray(getDrawerItemsArrayId())));
        mLeftDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                childSelected(parent, v, groupPosition, childPosition, id);
                return true;
            }
        });
        mLeftDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                groupSelected(v, groupPosition, id);
                return false;
            }
        });
    }

    protected void setToolbarTitle(String title) {
        Log.i(TAG, "Setting title to " + title);
        mTitleTextView.setText(title);
    }

    public static class ToolBarMenu {
        public static class ToolbarMenuItem {
            private Drawable icon;
            private String label;
            private View.OnClickListener onClickListener;

            public ToolbarMenuItem(Drawable _icon, String _label, View.OnClickListener _onClickListener) {
                icon = _icon;
                label = _label;
                onClickListener = _onClickListener;
            }
        }

        int menuItemRes;
        List<ToolbarMenuItem> menuItems;

        public ToolBarMenu(int menuItemRes) {
            menuItems = new ArrayList<>();
            this.menuItemRes = menuItemRes;
        }

        public void AddItem(Drawable icon, String label, View.OnClickListener ocl) {
            menuItems.add(new ToolbarMenuItem(icon, label, ocl));
        }

        public List<ToolbarMenuItem> getMenuItems() {
            return menuItems;
        }

        public int getMenuItemRes() {
            return menuItemRes;
        }
    }

    protected LinearLayout getToolBarMenuContainer() {
        return mToolBarMenu;
    }

    protected void remakeToolbarIcons(ToolBarMenu menu) {
        mToolBarMenu.removeAllViews();
        for (ToolBarMenu.ToolbarMenuItem item : menu.getMenuItems()) {
            ViewGroup root = (ViewGroup) getLayoutInflater().inflate(menu.getMenuItemRes(), getToolBarMenuContainer(), true);
            ImageView iv = (ImageView) root.getChildAt(root.getChildCount() - 1);
            iv.setOnClickListener(item.onClickListener);
            iv.setImageDrawable(item.icon);
            iv.setTag(item.label);
        }
    }

    @OverridingMethodsMustInvokeSuper
    protected void CloseDrawer() {
        mDrawerLayout.closeDrawers();
    }

    protected ToolBarMenu getInitialToolBarMenu() {
        return new ToolBarMenu(R.layout.actionbar_menu_item);
    }

    protected abstract int getContentLayoutId();

    protected abstract int getDrawerItemsArrayId();

    protected abstract void groupSelected(View v, int groupPosition, long id);

    protected abstract void childSelected(ExpandableListView parent, View v, int groupPosition, int childPosition, long id);

    protected IntentFilter getIntentFilter() {
        return null;
    }

    protected void HandleBroadcastIntent(Context context, Intent intent) {
        Log.i(TAG, "Received a broadcasted intent");
    }
}
