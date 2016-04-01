package com.example.vigi.feedbananademo;

import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;

/**
 * Created by Vigi on 2016/3/31.
 */
public class DraggableViewAnimator extends ViewAnimator {
    private CatcherViewAnimator mCatcher;
    private boolean mDragging = false;
    private DraggableActionListener mListener;
    private int mTempX;
    private int mTempY;

    public DraggableViewAnimator(View view, DraggableActionListener listener) {
        this(null, view, listener);
    }

    public DraggableViewAnimator(SpringConfig springConfig, View view, DraggableActionListener listener) {
        super(springConfig, view);
        mListener = listener;
        mSpringX.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                mTempX = (int) spring.getCurrentValue();
                checkListener();
            }
        });
        mSpringY.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                mTempY = (int) spring.getCurrentValue();
                checkListener();
            }
        });
    }

    private void checkListener() {
        if (mListener != null && !mDragging
                && mTempX == mResetPosX && mTempY == mResetPosY) {
            mListener.onViewIdle(mView);
        }
    }

    public void onStartDrag() {
        abortAnimation();
        mDragging = true;
        notifyCatcher(getViewPivotX(), getViewPivotY());
    }

    public void onRelease() {
        mDragging = false;
        reset();
        if (mCatcher != null) {
            mCatcher.reset();
        }
    }

    public void onPositionChange(int x, int y) {
        notifyCatcher(x, y);
    }

    public void setCatcher(CatcherViewAnimator catcher) {
        mCatcher = catcher;
    }

    private void notifyCatcher(int x, int y) {
        if (mCatcher != null) {
            mCatcher.catchPoint(x, y);
        }
    }

    public interface DraggableActionListener {
        void fallInDanger(View catcher, View view);

        void onViewIdle(View view);
    }
}
