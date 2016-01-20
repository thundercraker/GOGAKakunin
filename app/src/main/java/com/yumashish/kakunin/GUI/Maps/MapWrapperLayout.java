package com.yumashish.kakunin.GUI.Maps;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by lightning on 12/1/15.
 */
public class MapWrapperLayout extends FrameLayout {

    public interface OnDragListener {
        void onDrag(MotionEvent motionEvent);
    }

    private OnDragListener mOnDragListener;
    private OnTouchListener mOnTouchListener;

    public MapWrapperLayout(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mOnDragListener != null) {
            mOnDragListener.onDrag(ev);
        }
        if(mOnTouchListener != null) {
            mOnTouchListener.onTouch(this,ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    public void setOnDragListener(OnDragListener mOnDragListener) {
        this.mOnDragListener = mOnDragListener;
    }

    public void setOnTouchListener(OnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
    }
}
