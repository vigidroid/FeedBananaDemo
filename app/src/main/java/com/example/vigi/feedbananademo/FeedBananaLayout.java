package com.example.vigi.feedbananademo;

import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

/**
 * Created by Vigi on 2015/10/29.
 */
public class FeedBananaLayout extends FrameLayout {
    private static final float SCALE_LARGE = 1.2f;

    public static final int STATE_INIT = 0;
    public static final int STATE_EATING = 1;
    public static final int STATE_FAILURE = 2;
    public static final int STATE_FINISHED = 3;

    private ViewDragHelper mViewDragHelper;
    private SpringSystem mSpringSystem;

    private FeedActionListener mFeedActionListener;
    private int mState = STATE_INIT;

    private AnimatorSet mAnimatorSet;

    private final Rect mTempRect = new Rect();

    public FeedBananaLayout(Context context) {
        super(context);
        init();
    }

    public FeedBananaLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FeedBananaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FeedBananaLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragCallBack());
        mSpringSystem = SpringSystem.create();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            return lp.mDraggable;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            LayoutParams lp = (LayoutParams) capturedChild.getLayoutParams();
            lp.abortAnimation();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            LayoutParams lp = (LayoutParams) releasedChild.getLayoutParams();
            lp.resetPos();
//            mViewDragHelper.settleCapturedViewAt(lp.mResetPosX, lp.mResetPosY);
//            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        prepareChildren();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void prepareChildren() {
        for (int i = 0; i < getChildCount(); ++i) {
            final View child = getChildAt(i);
            ViewGroup.LayoutParams vglp = child.getLayoutParams();
            if (vglp instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) vglp;
                lp.resolveAnchorView(this, child);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateOffset();
    }

    private void updateOffset() {
        for (int i = 0; i < getChildCount(); ++i) {
            final View child = getChildAt(i);
            ViewGroup.LayoutParams vglp = child.getLayoutParams();
            if (vglp instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) vglp;
                lp.updateOffsetToAnchor(this, child, mTempRect);
            }
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        int mAnchorId = View.NO_ID;
        boolean mDraggable = false;
        int mThresholdRadius = 0;

        View mAnchorView;
        int mResetPosX;
        int mResetPosY;
        ViewAnimator mViewAnimator;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FeedBananaLayout_LayoutParams);
            mAnchorId = a.getResourceId(R.styleable.FeedBananaLayout_LayoutParams_layout_view_ref, View.NO_ID);
            mDraggable = a.getBoolean(R.styleable.FeedBananaLayout_LayoutParams_banana_draggable, false);
            mThresholdRadius = a.getDimensionPixelSize(R.styleable.FeedBananaLayout_LayoutParams_banana_threshold_radius, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
        }

        private void initViewAnimator(SpringSystem springSystem, View child) {
            if (mDraggable) {
                mViewAnimator = new ViewAnimator(springSystem, child);
            }
        }

        void resolveAnchorView(FeedBananaLayout parent, View child) {
            initViewAnimator(parent.mSpringSystem, child);
            if (mAnchorId == View.NO_ID) {
                return;
            }
            mAnchorView = parent.findViewById(mAnchorId);
            if (mAnchorView != null) {
                // make mAnchorView's layout_width and layout_height wrote in xml invalid
                ViewGroup.LayoutParams lp = mAnchorView.getLayoutParams();
                lp.width = this.width;
                lp.height = this.height;
            }
        }

        void updateOffsetToAnchor(FeedBananaLayout parent, View child, Rect tempRect) {
            if (mAnchorView == null) {
                return;
            }
            mAnchorView.setVisibility(INVISIBLE);
            tempRect.set(0, 0, mAnchorView.getWidth(), mAnchorView.getHeight());
            parent.offsetDescendantRectToMyCoords(mAnchorView, tempRect);
            final int anchorPivotX = tempRect.centerX();
            final int anchorPivotY = tempRect.centerY();
            final int childPivotX = (child.getLeft() + child.getRight()) / 2;
            final int childPivotY = (child.getTop() + child.getBottom()) / 2;
            child.offsetLeftAndRight(anchorPivotX - childPivotX);
            child.offsetTopAndBottom(anchorPivotY - childPivotY);
            // save position
            mResetPosX = child.getLeft();
            mResetPosY = child.getTop();
        }

        void resetPos() {
            if (mViewAnimator == null) {
                return;
            }
            mViewAnimator.animView(mResetPosX, mResetPosY);
        }

        void abortAnimation() {
            if (mViewAnimator == null) {
                return;
            }
            mViewAnimator.abort();
        }
    }

    static class ViewAnimator {
        private Spring mSpringX;
        private Spring mSpringY;
        private View mView;

        public ViewAnimator(SpringSystem springSystem, View view) {
            mSpringX = springSystem.createSpring();
            mSpringY = springSystem.createSpring();
            mView = view;

            mSpringX.addListener(new SimpleSpringListener() {
                @Override
                public void onSpringUpdate(Spring spring) {
                    mView.offsetLeftAndRight((int) (spring.getCurrentValue() - mView.getLeft()));
                }
            });
            mSpringY.addListener(new SimpleSpringListener() {
                @Override
                public void onSpringUpdate(Spring spring) {
                    mView.offsetTopAndBottom((int) (spring.getCurrentValue() - mView.getTop()));
                }
            });
        }

        void animView(int endX, int endY) {
            mSpringX.setCurrentValue(mView.getLeft());
            mSpringY.setCurrentValue(mView.getTop());
            mSpringX.setEndValue(endX);
            mSpringY.setEndValue(endY);
        }

        void abort() {
            mSpringX.setAtRest();
            mSpringY.setAtRest();
        }
    }

    public interface FeedActionListener {
        void bananaCaught(View banana);

        void bananaPutBack();

        void eaterSeeIt();

        void eaterNotSeeIt();

        void beEatOff(int count);
    }
}
