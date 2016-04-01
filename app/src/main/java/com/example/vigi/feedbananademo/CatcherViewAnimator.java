package com.example.vigi.feedbananademo;

import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;

/**
 * Created by Vigi on 2016/3/31.
 */
public class CatcherViewAnimator extends ViewAnimator {
    private int mThresholdRadius = 0;
    private boolean mIsFollowing = false;
    private CatcherActionListener mListener;
    private int mTempX;
    private int mTempY;

    public CatcherViewAnimator(View view, CatcherActionListener listener) {
        this(null, view, listener);
    }

    public CatcherViewAnimator(SpringConfig springConfig, View view, CatcherActionListener listener) {
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

    public int getThresholdRadius() {
        return mThresholdRadius;
    }

    public void setThresholdRadius(int thresholdRadius) {
        mThresholdRadius = thresholdRadius;
    }

    private void checkListener() {
        if (mListener != null && !mIsFollowing
                && mTempX == mResetPosX && mTempY == mResetPosY) {
            mListener.onCatcherIdle(mView);
        }
    }

    @Override
    public void reset() {
        super.reset();
        mIsFollowing = false;
    }

    void catchPoint(int intentX, int intentY) {
        mIsFollowing = true;
        int targetX = intentX;
        int targetY = intentY;
        if (mThresholdRadius <= 0) {
            animView(targetX, targetY);
            return;
        }
        if (Math.pow(intentX - mResetPosX, 2) + Math.pow(intentY - mResetPosY, 2) > Math.pow(mThresholdRadius, 2)) {
            if (intentX == mResetPosX) {
                targetX = mResetPosX;
                targetY = mResetPosY + mThresholdRadius * (intentY > mResetPosY ? 1 : -1);
            } else {
                double rad = Math.atan((double) (mResetPosY - intentY) / (intentX - mResetPosX));
                targetX = mResetPosX + (int) (mThresholdRadius * Math.cos(rad)) * (intentX > mResetPosX ? 1 : -1);
                targetY = mResetPosY - (int) (mThresholdRadius * Math.sin(rad)) * (intentX > mResetPosX ? 1 : -1);
            }
        }

        animView(targetX, targetY);
    }

    public interface CatcherActionListener {
        void onCatcherIdle(View catcher);
    }
}
