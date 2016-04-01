package com.example.vigi.feedbananademo;

import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;

/**
 * Created by Vigi on 2016/3/31.
 */
public class FollowerViewAnimator extends ViewAnimator {
    private int mThresholdRadius = 0;
    private boolean mIsFollowing = false;
    private FollowerActionListener mListener;
    private int mTempX;
    private int mTempY;

    public FollowerViewAnimator(View view, FollowerActionListener listener) {
        this(null, view, listener);
    }

    public FollowerViewAnimator(SpringConfig springConfig, View view, FollowerActionListener listener) {
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
            mListener.onFollowerIdle(mView);
        }
    }

    @Override
    public void reset() {
        super.reset();
        mIsFollowing = false;
    }

    void followPoint(int intentX, int intentY) {
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

    public interface FollowerActionListener {
        void onDistanceChange(View follower, int distance);

        void onFollowerIdle(View follower);
    }
}
