package com.example.vigi.feedbananademo;

import android.view.View;

import com.facebook.rebound.SpringConfig;

/**
 * Created by Vigi on 2016/3/31.
 */
public class DraggableViewAnimator extends ViewAnimator {
    private FollowerViewAnimator mFollower;

    public DraggableViewAnimator(View view) {
        super(view);
    }

    public DraggableViewAnimator(SpringConfig springConfig, View view) {
        super(springConfig, view);
    }

    @Override
    public void reset() {
        super.reset();
        if (mFollower != null) {
            mFollower.reset();
        }
    }

    public void setFollower(FollowerViewAnimator follower) {
        mFollower = follower;
    }

    public void notifyFollower(int x, int y) {
        if (mFollower != null) {
            mFollower.followPoint(x, y);
        }
    }
}
