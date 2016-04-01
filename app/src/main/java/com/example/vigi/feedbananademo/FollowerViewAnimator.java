package com.example.vigi.feedbananademo;

import android.view.View;

import com.facebook.rebound.SpringConfig;

/**
 * Created by Vigi on 2016/3/31.
 */
public class FollowerViewAnimator extends ViewAnimator {
    private int mThresholdRadius = 0;

    public FollowerViewAnimator(SpringConfig springConfig, View view) {
        super(springConfig, view);
    }

    public FollowerViewAnimator(View view) {
        super(view);
    }

    public int getThresholdRadius() {
        return mThresholdRadius;
    }

    public void setThresholdRadius(int thresholdRadius) {
        mThresholdRadius = thresholdRadius;
    }

    void followPoint(int intentX, int intentY) {
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
}
