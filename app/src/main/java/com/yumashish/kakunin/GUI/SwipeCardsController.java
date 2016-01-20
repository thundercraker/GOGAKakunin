package com.yumashish.kakunin.GUI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yumashish.kakunin.R;
import com.yumashish.kakunin.Tasks.ImageLoadedHandler;
import com.yumashish.kakunin.Tasks.LoadImageTask;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lightning on 12/14/15.
 */
public class SwipeCardsController implements View.OnTouchListener {
    public static String TAG = "SWIPE_CARD_CONTROLLER";
    public static int ID_UNIQUE = 0;
    Context mAppContext;
    RelativeLayout mMainLayout;
    View mDragView;
    List<TapCardInterface> mCards;
    boolean mFinishedGeneration;
    int mCenterMarginY, mCenterMarginX;
    OnAcceptOrRejectHandler resultHandler;
    OnFinished finishHandler;
    Dictionary<Integer, TapCardInterface> mCardAssoc;
    Stack<AsyncTask> mLoaderStack;

    AtomicInteger uniqueIds;

    public SwipeCardsController(Context context, RelativeLayout mainLayout, final List<TapCardInterface> cards) {
        Log.d(TAG, "Starting SwipCardController");
        mAppContext = context;
        mMainLayout = mainLayout;
        mCards = cards;
        mCardAssoc = new Hashtable<>();
        mLoaderStack = new Stack<>();
        uniqueIds = new AtomicInteger(0);
        swiped = 0;

        int pHeight = mMainLayout.getHeight();
        if(pHeight > 0) {
            Log.i(TAG, "Main layout of Swipe Card controller is already loaded, dims: " + pHeight + " " + mMainLayout.getWidth());
            createChildren(mMainLayout, mCards);
        } else {
            Log.i(TAG, "Main layout of Swipe Card controller is not loaded, adding view tree observer: " + pHeight + " " + mMainLayout.getWidth());
            mMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.i(TAG, "On Global layout listener triggered");
                    createChildren(mMainLayout, mCards);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mMainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mMainLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                }
            });
        }

        resultHandler = new OnAcceptOrRejectHandler() {
            @Override
            public void OnAccept(TapCardInterface card) {
                //Toast.makeText(mAppContext, "Accepted " + card.getId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void OnReject(TapCardInterface card) {
                //Toast.makeText(mAppContext, "Rejected " + card.getId(), Toast.LENGTH_LONG).show();
            }
        };
    }

    public void SetOnFinishHandler(OnFinished handler) {
        finishHandler = handler;
    }

    public void SetOnAcceptOrRejectHandler(OnAcceptOrRejectHandler handler) {
        resultHandler = handler;
    }

    public SwipeCardsController GetSelf() {
        return this;
    }

    public void createChildren(ViewGroup parent, List<TapCardInterface> cardDataList)
    {
        int pHeight = parent.getHeight();
        int pWidth = parent.getWidth();

        //scale the card size to 90% of parent height and width
        int height = (int) (pHeight * 0.9f);
        int width = (int) (pWidth * 0.9f);

        mCenterMarginY = (int)((0.5f * pHeight) - (0.5f * height));
        mCenterMarginX = (int)((0.5f * pWidth) - (0.5f * width));
        //for(TapCardInterface cardData : cardDataList) {
        ///    createTapCard(parent, height, width, cardData, true);
        //}

        //createTapCard(parent, height, width, top, false);
        Log.i(TAG, "Create children, found: " + cardDataList.size() + " children");
        for(int i = cardDataList.size() - 1; i >= 0; i--) {
            createTapCard(parent, height, width, cardDataList.get(i), (i != 0));
        }

        while (!mLoaderStack.empty()) {
            AsyncTask task = mLoaderStack.pop();
            if(task != null)
                task.execute();
        }

        mFinishedGeneration = true;
        //animations to make the cards appear
        appearR(0);
        //set the top card to be draggable;
        setDraggableCard();
    }

    //helper method
    //makes a vew appear
    public void appearAnimation(View v, AnimatorListenerAdapter ala)
    {
        v.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(ala);
    }

    //Make the invisible tap card views appear one by one recurrsively
    public void appearR(int index)
    {
        if(index >= mMainLayout.getChildCount())
            return;
        final int mIndex = index + 1;
        mMainLayout.getChildAt(index).setAlpha(0f);
        mMainLayout.getChildAt(index).setVisibility(View.VISIBLE);
        appearAnimation(mMainLayout.getChildAt(index),
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        appearR(mIndex);
                    }
                });
    }

    int lfirstFrameTopMargin, lfirstFrameLeftMargin;
    public void setDraggableCard()
    {
        if(mMainLayout.getChildCount() > 0 && mFinishedGeneration) {

            mDragView = mMainLayout.getChildAt(mMainLayout.getChildCount() - 1);
            final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mDragView.getLayoutParams();
            lfirstFrameLeftMargin = params.leftMargin;
            //Log.d(TAG, lfirstFrameLeftMargin + " original animation margin");
            lfirstFrameTopMargin = params.topMargin;
            Animation animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    params.topMargin = (int)((mCenterMarginY - lfirstFrameTopMargin) * interpolatedTime) + lfirstFrameTopMargin;
                    //Log.d(DEBUG_TAG, ((int)(mCenterMarginY + mCenterMarginY - lfirstFrameTopMargin) * interpolatedTime) + " animation margin X");
                    params.leftMargin = (int)((mCenterMarginX - lfirstFrameLeftMargin) * interpolatedTime) + lfirstFrameLeftMargin;
                    mDragView.setRotation(mDragView.getRotation() * interpolatedTime);
                    mDragView.setLayoutParams(params);
                }
            };
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mDragView != null)
                        mDragView.setOnTouchListener(GetSelf());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setDuration(300);
            mDragView.startAnimation(animation);

        } else {
            mDragView = null;
        }
    }

    //Get a tapcard LinearLayout
    //refer to the tap_card layout
    int tiltSign = 1;
    private LinearLayout createTapCard(ViewGroup parent, int height, int width, TapCardInterface tapCard, boolean randomize)
    {
        final LinearLayout card = (LinearLayout) ((LayoutInflater)mAppContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                    .inflate(tapCard.getLayoutId(), null);
        card.setId(uniqueIds.incrementAndGet());
        //get a rando, degree for tilt
        int tilt = (int) ((Math.random() * 7)) * tiltSign;
        tiltSign = tiltSign * -1;

        //get random offsets for X and Y
        int rX = 0;//(int) ((Math.random() * 20d) - 10d);
        int rY = 0;//(int) ((Math.random() * 20d) - 10d);

        card.setTag(R.string.tap_card_tag, tapCard);
        card.setScaleX(1.1f);
        card.setScaleY(1.1f);
        card.setVisibility(View.GONE);
        if(randomize) card.setRotation(tilt);
        parent.addView(card);

        RelativeLayout.LayoutParams lparams = (RelativeLayout.LayoutParams) card.getLayoutParams();
        lparams.width = width;
        lparams.height = height;
        lparams.topMargin = mCenterMarginY;
        lparams.leftMargin = mCenterMarginX;

        //set to the middle by calculating corrent margins
        //margin top: 50% of parent height - 50% of child height

        //lparams.topMargin = mCenterMarginY;
        //lparams.leftMargin = mCenterMarginX;
        mLoaderStack.push(tapCard.modifyTapCardView(card));

        Log.d(TAG, "Created card Margin T/L" + mCenterMarginX + " " + mCenterMarginY + " for " + tapCard.getLayoutId());

        //TODO
        card.setLayoutParams(lparams);
        mCardAssoc.put(card.getId(), tapCard);

        return card;
    }

    public int oX, oY, oMarginX, oMarginY, mYD, mXD, lX, lY;
    VelocityTracker mVelocityTracker;
    static final int SWIPE_OFF_EDGE_OFFSET = 100;
    boolean anim_lock;

    int swiped;
    public boolean AllCardsSwiped() {
        Log.i(TAG, "Swiped: " + swiped + " == " + mCards.size());
        return ++swiped == mCards.size();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "OnTouch");
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        final int x = (int) event.getX();
        final int y = (int) event.getY();

        String tag = (v.getTag()!=null) ? v.getTag().toString() : "";

        if(mDragView == null) return true;

        switch(action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                mXD = x;
                mYD = y;
                oMarginX = layoutParams.leftMargin;
                oMarginY = layoutParams.topMargin;
                oX = x;
                oY = y;
                Log.d(TAG, mXD + "," + mYD + " Action Down Tag: " + tag);
                break;
            case MotionEvent.ACTION_MOVE:
                //Velocity and Distance are not used at present, it will be in future implementation
                //mVelocityTracker.addMovement(event);
                //mVelocityTracker.computeCurrentVelocity(1000);
                //Find Velocity
                //double velocityX = mVelocityTracker.getXVelocity();
                //double velocityY = mVelocityTracker.getYVelocity();

                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                lParams.leftMargin = lParams.leftMargin + (x - mXD);
                lParams.topMargin = lParams.topMargin + (y - mYD);
                lParams.rightMargin = -500;
                lParams.bottomMargin = -500;
                v.setLayoutParams(lParams);

                //if the current X,Y is MOVE_DISTANCE or greater, start a translation animation
                //double distance = Math.sqrt(Math.pow(x - lX, 2) + Math.pow(y - lY, 2));
                //Log.d(TAG, "Distance: " + distance);
                //Log.d(TAG, x + "," + y + " Action Move Tag: " + tag + " Velocity " + velocityX + " " + velocityY);
                Log.d(TAG, "OnMove dX " + (x - mXD) + " dY " + (y - mYD));
                break;
            case MotionEvent.ACTION_UP:

                //Detect if the object is in deletion Zone
                if (event.getRawX() < SWIPE_OFF_EDGE_OFFSET) {
                    mMainLayout.removeView(v);
                    setDraggableCard();
                    resultHandler.OnReject(mCardAssoc.get(v.getId()));

                    if(AllCardsSwiped()) {
                        CloseLayer();
                    }
                }
                else if(event.getRawX() > mMainLayout.getWidth() - SWIPE_OFF_EDGE_OFFSET)
                {
                    mMainLayout.removeView(v);
                    setDraggableCard();
                    resultHandler.OnAccept(mCardAssoc.get(v.getId()));

                    if(AllCardsSwiped()) {
                        CloseLayer();
                    }
                }

                Log.d(TAG, event.getRawX() + "," + event.getRawY() + " Action up Tag: " + tag);
                break;
        }
        mMainLayout.invalidate();

        return true;
    }

    public void CloseLayer() {
        if(finishHandler != null)
            finishHandler.onFinish();
    }

    public interface TapCardInterface {
        int getId();
        int getLayoutId();

        AsyncTask modifyTapCardView(final View card);
    }

    public interface OnAcceptOrRejectHandler {
        void OnAccept(TapCardInterface card);
        void OnReject(TapCardInterface card);
    }

    public interface OnFinished {
        void onFinish();
    }
}
