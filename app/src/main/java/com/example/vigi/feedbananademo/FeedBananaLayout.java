package com.example.vigi.feedbananademo;

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

import com.facebook.rebound.SpringConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vigi on 2015/10/29.
 */
public class FeedBananaLayout extends FrameLayout
        implements DraggableViewAnimator.DraggableActionListener, CatcherViewAnimator.CatcherActionListener {

    private ViewDragHelper mViewDragHelper;
    private ViewDragCallBack mDragCallBack;
    private Map<View, Boolean> mBananasState = new HashMap<>();

    private FeedActionListener mFeedActionListener;

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
        mDragCallBack = new ViewDragCallBack();
        mViewDragHelper = ViewDragHelper.create(this, mDragCallBack);
    }

    public void setFeedActionListener(FeedActionListener feedActionListener) {
        mFeedActionListener = feedActionListener;
    }

    public ViewAnimator retrieveAnimator(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof LayoutParams) {
            return ((LayoutParams) lp).mViewAnimator;
        }
        return null;
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

    @Override
    public void onDistanceChanged(CatcherViewAnimator catcherAnimator
            , DraggableViewAnimator viewAnimator, int distance) {
        boolean isSeen = catcherAnimator.getThresholdRadius() > distance;
        boolean changed = false;

        Boolean stateSaved = mBananasState.get(viewAnimator.getView());
        if (stateSaved == null) {
            mBananasState.put(viewAnimator.getView(), isSeen);
        } else if (!stateSaved.equals(isSeen)) {
            changed = true;
            mBananasState.put(viewAnimator.getView(), isSeen);
        }

        if (changed && mFeedActionListener != null) {
            if (isSeen) {
                mFeedActionListener.uploaderSeen(
                        catcherAnimator.getView(), viewAnimator.getView()
                );
            } else {
                mFeedActionListener.uploaderMissed(
                        catcherAnimator.getView(), viewAnimator.getView()
                );
            }
        }
    }

    @Override
    public void onViewIdle(DraggableViewAnimator viewAnimator) {
        if (mFeedActionListener != null) {
            mFeedActionListener.bananaPutBack(viewAnimator.getView());
        }
    }

    @Override
    public void onCatcherIdle(CatcherViewAnimator catcherAnimator) {

    }

    class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (!child.isShown()) {
                return false;
            }
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            return lp.mViewAnimator instanceof DraggableViewAnimator;
        }

        @Override
        public int getOrderedChildIndex(int index) {
            int i = index;
            while (i >= 0) {
                LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
                if (lp.mDraggable) {
                    return i;
                }
                i--;
            }
            return index;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            LayoutParams lp = (LayoutParams) capturedChild.getLayoutParams();
            DraggableViewAnimator dragViewAnimator = (DraggableViewAnimator) lp.mViewAnimator;

            if (mFeedActionListener != null) {
                mFeedActionListener.bananaCaught(capturedChild);
            }
            dragViewAnimator.onStartDrag();
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            LayoutParams lp = (LayoutParams) changedView.getLayoutParams();
            if (lp.mViewAnimator != null && lp.mViewAnimator instanceof DraggableViewAnimator) {
                ((DraggableViewAnimator) lp.mViewAnimator).onPositionChange(
                        lp.mViewAnimator.getViewPivotX(), lp.mViewAnimator.getViewPivotY()
                );
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            boolean resetView = true;
            DraggableViewAnimator bananaAnimator;

            LayoutParams lp = (LayoutParams) releasedChild.getLayoutParams();
            if (lp.mViewAnimator != null && lp.mViewAnimator instanceof DraggableViewAnimator) {
                bananaAnimator = (DraggableViewAnimator) lp.mViewAnimator;
            } else {
                throw new IllegalStateException("the view be dragged must has \"banana_draggable\" in xml!");
            }

            Boolean currState = mBananasState.get(releasedChild);
            if (mFeedActionListener != null && currState != null && currState.equals(true)) {
                if (mFeedActionListener.beEatOff(bananaAnimator.getCatcher().getView(), releasedChild)) {
                    resetView = false;
                }
            }

            bananaAnimator.onRelease(resetView);
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
                lp.prepare(this, child);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        onFinishLayout();
    }

    private void onFinishLayout() {
        for (int i = 0; i < getChildCount(); ++i) {
            final View child = getChildAt(i);
            ViewGroup.LayoutParams vglp = child.getLayoutParams();
            if (vglp instanceof LayoutParams) {
                LayoutParams lp = (LayoutParams) vglp;
                lp.updateOffsetToAnchor(this, child);
                lp.bindCatcher(this);
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
        private int mAnchorId = View.NO_ID;
        private int mCatcherId = View.NO_ID;
        private boolean mDraggable = false;
        private int mThresholdRadius = 0;

        View mAnchorView;
        ViewAnimator mViewAnimator;
        final Rect mTempRect = new Rect();

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FeedBananaLayout_LayoutParams);
            mAnchorId = a.getResourceId(R.styleable.FeedBananaLayout_LayoutParams_layout_view_ref, View.NO_ID);
            mCatcherId = a.getResourceId(R.styleable.FeedBananaLayout_LayoutParams_banana_catcher, View.NO_ID);
            mDraggable = a.getBoolean(R.styleable.FeedBananaLayout_LayoutParams_banana_draggable, false);
            mThresholdRadius = a.getDimensionPixelSize(R.styleable.FeedBananaLayout_LayoutParams_banana_threshold_radius, 0);
            a.recycle();

            if (mDraggable && mThresholdRadius > 0) {
                throw new IllegalStateException("banana_threshold_radius and banana_draggable cannot both exist!");
            }
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

        void prepare(FeedBananaLayout parent, View child) {
            initViewAnimator(parent, child);
            resolveAnchorView(parent);
        }

        private void initViewAnimator(FeedBananaLayout parent, View child) {
            if (mViewAnimator != null) {
                return;
            }
            if (mThresholdRadius > 0) {
                mViewAnimator = new CatcherViewAnimator(
                        SpringConfig.fromOrigamiTensionAndFriction(30, 15), child, parent
                );
                ((CatcherViewAnimator) mViewAnimator).setThresholdRadius(mThresholdRadius);
                return;
            }
            if (mDraggable) {
                mViewAnimator = new DraggableViewAnimator(child, parent);
            }
        }

        private void resolveAnchorView(FeedBananaLayout parent) {
            if (mAnchorView != null) {
                return;
            }
            if (mAnchorId != NO_ID) {
                mAnchorView = parent.findViewById(mAnchorId);
            }
        }

        void updateOffsetToAnchor(FeedBananaLayout parent, View child) {
            if (mAnchorView == null) {
                return;
            }
            mTempRect.set(0, 0, mAnchorView.getWidth(), mAnchorView.getHeight());
            parent.offsetDescendantRectToMyCoords(mAnchorView, mTempRect);
            final int anchorPivotX = mTempRect.centerX();
            final int anchorPivotY = mTempRect.centerY();
            final int childPivotX = (child.getLeft() + child.getRight()) / 2;
            final int childPivotY = (child.getTop() + child.getBottom()) / 2;
            child.offsetLeftAndRight(anchorPivotX - childPivotX);
            child.offsetTopAndBottom(anchorPivotY - childPivotY);
            // save position
            if (mViewAnimator != null) {
                mViewAnimator.setResetPos(anchorPivotX, anchorPivotY);
            }
        }

        void bindCatcher(FeedBananaLayout parent) {
            if (mCatcherId == View.NO_ID
                    || mViewAnimator == null || !(mViewAnimator instanceof DraggableViewAnimator)) {
                return;
            }
            final View catcherView = parent.findViewById(mCatcherId);
            if (catcherView == null) {
                throw new IllegalStateException("can not find banana_catcher in FeedBananaLayout!");
            }
            LayoutParams flp = (LayoutParams) catcherView.getLayoutParams();
            if (flp.mViewAnimator != null && flp.mViewAnimator instanceof CatcherViewAnimator) {
                ((DraggableViewAnimator) mViewAnimator).setCatcher((CatcherViewAnimator) flp.mViewAnimator);
            }
        }
    }

    public interface FeedActionListener {
        void bananaCaught(View banana);

        void bananaPutBack(View banana);

        void uploaderSeen(View uploader, View banana);

        void uploaderMissed(View uploader, View banana);

        boolean beEatOff(View uploader, View banana);
    }
}
