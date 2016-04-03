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
    private final Rect mTempRect = new Rect();

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
                        catcherAnimator, viewAnimator
                );
            } else {
                mFeedActionListener.uploaderMissed(
                        catcherAnimator, viewAnimator
                );
            }
        }
    }

    @Override
    public void onViewIdle(DraggableViewAnimator viewAnimator) {
        if (mFeedActionListener != null) {
            mFeedActionListener.bananaPutBack(viewAnimator);
        }
    }

    @Override
    public void onCatcherIdle(CatcherViewAnimator catcherAnimator) {

    }

    class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            return lp.mViewAnimator instanceof DraggableViewAnimator;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            LayoutParams lp = (LayoutParams) capturedChild.getLayoutParams();
            DraggableViewAnimator dragViewAnimator = (DraggableViewAnimator) lp.mViewAnimator;

            if (mFeedActionListener != null) {
                mFeedActionListener.bananaCaught(dragViewAnimator);
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
            boolean isReleaseView = true;
            DraggableViewAnimator bananaAnimator;

            LayoutParams lp = (LayoutParams) releasedChild.getLayoutParams();
            if (lp.mViewAnimator != null && lp.mViewAnimator instanceof DraggableViewAnimator) {
                bananaAnimator = (DraggableViewAnimator) lp.mViewAnimator;
            } else {
                throw new IllegalStateException("the view be dragged must has \"banana_draggable\" in xml!");
            }

            Boolean currState = mBananasState.get(releasedChild);
            if (mFeedActionListener != null && currState != null && currState.equals(true)) {
                if (mFeedActionListener.beEatOff(bananaAnimator.getCatcher(), bananaAnimator)) {
                    isReleaseView = false;
                }
            }

            if (isReleaseView) {
                bananaAnimator.onRelease();
            }
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
                lp.updateOffsetToAnchor(this, child, mTempRect);
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

        private View mAnchorView;
        ViewAnimator mViewAnimator;

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

        void prepare(FeedBananaLayout parent, View child) {
            initViewAnimator(parent, child);
            if (mAnchorId != View.NO_ID) {
                mAnchorView = parent.findViewById(mAnchorId);
                if (mAnchorView != null) {
                    // make mAnchorView's layout_width and layout_height wrote in xml invalid
                    ViewGroup.LayoutParams lp = mAnchorView.getLayoutParams();
                    lp.width = this.width;
                    lp.height = this.height;
                }
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
        void bananaCaught(DraggableViewAnimator bananaAnimator);

        void bananaPutBack(DraggableViewAnimator bananaAnimator);

        void uploaderSeen(CatcherViewAnimator uploaderAnimator, DraggableViewAnimator bananaAnimator);

        void uploaderMissed(CatcherViewAnimator uploaderAnimator, DraggableViewAnimator bananaAnimator);

        boolean beEatOff(CatcherViewAnimator uploaderAnimator, DraggableViewAnimator bananaAnimator);
    }
}
