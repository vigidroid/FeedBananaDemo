package com.example.vigi.feedbananademo;

import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;

/**
 * Created by Vigi on 2016/3/31.
 */
public abstract class ViewAnimator {
    private static final SpringSystem SPRING_SYSTEM = SpringSystem.create();

    protected Spring mSpringX;
    protected Spring mSpringY;

    private View mView;
    protected int mResetPosX;
    protected int mResetPosY;

    public ViewAnimator(View view) {
        this(null, view);
    }

    public ViewAnimator(SpringConfig springConfig, View view) {
        mSpringX = SPRING_SYSTEM.createSpring();
        mSpringY = SPRING_SYSTEM.createSpring();
        if (springConfig != null) {
            mSpringX.setSpringConfig(springConfig);
            mSpringY.setSpringConfig(springConfig);
        }
        mView = view;

        mSpringX.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                mView.offsetLeftAndRight((int) spring.getCurrentValue() - getViewPivotX());
            }
        });
        mSpringY.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                mView.offsetTopAndBottom((int) spring.getCurrentValue() - getViewPivotY());
            }
        });
    }

    public void setResetPos(int x, int y) {
        setCurrentPos(x, y);
        mResetPosX = x;
        mResetPosY = y;
    }

    private void setCurrentPos(int pivotX, int pivotY) {
        mSpringX.setCurrentValue(pivotX);
        mSpringY.setCurrentValue(pivotY);
    }

    public void reset() {
        setCurrentPos(getViewPivotX(), getViewPivotY());
        animView(mResetPosX, mResetPosY);
    }

    public void animView(int endX, int endY) {
        mSpringX.setEndValue(endX);
        mSpringY.setEndValue(endY);
    }

    public void abortAnimation() {
        mSpringX.setAtRest();
        mSpringY.setAtRest();
    }

    public View getView() {
        return mView;
    }

    public int getViewPivotX() {
        return (int) (mView.getX() + mView.getWidth() / 2);
    }

    public int getViewPivotY() {
        return (int) (mView.getY() + mView.getHeight() / 2);
    }
}
