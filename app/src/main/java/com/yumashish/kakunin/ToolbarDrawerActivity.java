package com.yumashish.kakunin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yumashish.kakunin.GUI.DrawerExpandableLVAdapter;
import com.yumashish.kakunin.Interfaces.OnToolbarMenuItemClickListener;
import com.yumashish.kakunin.Interfaces.SimpleAction;
import com.yumashish.yumamateriallistview.YumaMaterialListView;
//import com.example.yumashish.gogamarkethuddle.LaunchActivity;

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
    private YumaMaterialListView mLeftDrawerList;
    private FrameLayout mContentFrame;
    private Toolbar mToolBar;
    private TextView mTitleTextView;
    private LinearLayout mToolBarMenu;
    private ActionBarDrawerToggle mDrawerToggle;
    private BroadcastReceiver mBroadcastReceiver;
    protected SlidingUpPanelLayout mSlider;
    protected ViewGroup mSliderPanel;

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
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedState);
        setContentView(R.layout.home_drawer);

        ACTIVE = true;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_menu_layout);
        mToolBar = (Toolbar) findViewById(R.id.home_action_bar);//getToolbarId());
        setSupportActionBar(mToolBar);

        mTitleTextView = (TextView) findViewById(R.id.home_title);
        //mToolBarMenu = (LinearLayout) findViewById(R.id.action_bar_menu_container);

        mLeftDrawerList = (YumaMaterialListView) findViewById(R.id.left_drawer_listview);

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.drawer_menu_icon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setToolbarTitle(getTitle().toString());

        mSlider = (SlidingUpPanelLayout) findViewById(R.id.bottom_drawer);
        mSliderPanel = (ViewGroup) findViewById(R.id.bottom_drawer_panel);
        SetupDrawer();
        //remakeToolbarIcons(getInitialToolBarMenu());

    }

    public void disPatchTouchEventToFragment(MotionEvent evt) {
        mContentFrame.dispatchTouchEvent(evt);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_default_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
        super.onBackPressed();
    }

    protected void setToolbarTitle(String title) {
        Log.i(TAG, "Setting title to " + title);
        mTitleTextView.setText(title);
    }

    protected YumaMaterialListView getDrawerListView() {
        return mLeftDrawerList;
    }

    @OverridingMethodsMustInvokeSuper
    protected void SetupDrawer() {
        mLeftDrawerList.setOnClickResponder(new YumaMaterialListView.RespondToID() {
            @Override
            public boolean Respond(int id) {
                return onMenuItemPressed(id);
            }
        });

        mLeftDrawerList.setOnLongClickResponder(new YumaMaterialListView.RespondToID() {
            @Override
            public boolean Respond(int id) {
                return onMenuItemLongPressed(id);
            }
        });

        /*mLeftDrawerList = (ExpandableListView) findViewById(R.id.left_drawer_listview);
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
        mLeftDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //return itemLongPress(parent, view, position, id);
                int itemType = ExpandableListView.getPackedPositionType(id);

                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int pos = ExpandableListView.getPackedPositionChild(id);
                    Log.i(TAG, "Child Position: " + pos + " ID: " + id);
                    childLongPress(parent, view, pos, id);
                    return true;
                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int pos = ExpandableListView.getPackedPositionGroup(id);
                    Log.i(TAG, "Group Position: " + pos + " ID: " + id);
                    groupLongPress(parent, view, pos, id);
                    return true;
                } else {
                    Log.i(TAG, "Null Position: " + position + " ID: " + id + " Item Type " + itemType);
                    return false;
                }
            }
        });*/
    }

    /*protected DrawerExpandableLVAdapter getDrawerLVadapter() {
        return (DrawerExpandableLVAdapter) mLeftDrawerList.getExpandableListAdapter();
    }*/

    @OverridingMethodsMustInvokeSuper
    protected void CloseDrawer() {
        mDrawerLayout.closeDrawers();
    }

    protected abstract int getContentLayoutId();

    protected abstract boolean onMenuItemPressed(int id);

    protected abstract boolean onMenuItemLongPressed(int id);

    protected IntentFilter getIntentFilter() {
        return null;
    }

    protected void HandleBroadcastIntent(Context context, Intent intent) {
        Log.i(TAG, "Received a broadcasted intent");
    }

    /*
    public static class ToolBarMenu {
        public static class ToolbarMenuItem {
            private Drawable icon;
            private String label;
            private OnToolbarMenuItemClickListener onClickListener;

            public ToolbarMenuItem(Drawable _icon, String _label, OnToolbarMenuItemClickListener _onClickListener) {
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

        public void AddItem(Drawable icon, String label, OnToolbarMenuItemClickListener ocl) {
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
        for (final ToolBarMenu.ToolbarMenuItem item : menu.getMenuItems()) {
            ViewGroup root = (ViewGroup) getLayoutInflater().inflate(menu.getMenuItemRes(), getToolBarMenuContainer(), true);
            ImageView iv = (ImageView) root.getChildAt(root.getChildCount() - 1);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.onClickListener.onToolbarMenuItemClicked(v.getTag().toString());
                }
            });
            iv.setImageDrawable(item.icon);
            iv.setTag(item.label);
        }
    }

     protected ToolBarMenu getInitialToolBarMenu() {
        ToolBarMenu toolBarMenu = new ToolBarMenu(R.layout.actionbar_menu_item);
        //toolBarMenu.AddItem(getResources().getDrawable(R.drawable.search_icon), "Search APIs", this);
        return toolBarMenu;
    }


     */
}
