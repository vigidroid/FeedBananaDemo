package com.example.vigi.feedbananademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vigi on 2016/4/5.
 */
public class FeedBanana1Activity extends AppCompatActivity implements View.OnClickListener {
    private static final float SCALE_LARGE = 1.2f;
    @Bind(R.id.feed_banana_layout)
    FeedBananaLayout mFeedBananaLayout;

    @Bind(R.id.reset_bt)
    ImageView mResetBt;

    @Bind(R.id.uploader_view)
    RoundedImageView mUploaderView;

    private List<DraggableViewAnimator> mBananaAnimators = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_demo1);
        ButterKnife.bind(this);

        mResetBt.setOnClickListener(this);

        mFeedBananaLayout.setFeedActionListener(new FeedBananaLayout.FeedActionListener() {
            @Override
            public void bananaCaught(View banana) {
                banana.animate().scaleX(SCALE_LARGE).scaleY(SCALE_LARGE).start();
            }

            @Override
            public void bananaPutBack(View banana) {
                banana.animate().scaleX(1.0f).scaleY(1.0f).start();
            }

            @Override
            public void uploaderSeen(View uploader, View banana) {
                uploader.animate().scaleX(SCALE_LARGE).scaleY(SCALE_LARGE).start();
            }

            @Override
            public void uploaderMissed(View uploader, View banana) {
                uploader.animate().scaleX(1.0f).scaleY(1.0f).start();
            }

            @Override
            public boolean beEatOff(View uploader, View banana) {
                banana.animate().scaleX(0f).scaleY(0f).start();
                ViewAnimator va = mFeedBananaLayout.retrieveAnimator(banana);
                if (va != null && va instanceof DraggableViewAnimator
                        && !mBananaAnimators.contains(va)) {
                    mBananaAnimators.add((DraggableViewAnimator) va);
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_bt:
                for (DraggableViewAnimator bananaAnimator : mBananaAnimators) {
                    bananaAnimator.reset();
                }
                mUploaderView.animate().scaleX(1.0f).scaleY(1.0f).start();
                break;
        }
    }
}
