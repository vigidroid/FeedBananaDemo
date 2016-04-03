package com.example.vigi.feedbananademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vigi on 2016/3/29.
 */
public class FeedBananaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final float SCALE_LARGE = 1.2f;
    @Bind(R.id.feed_banana_layout)
    FeedBananaLayout mFeedBananaLayout;

    @Bind(R.id.banana_decrease_bt)
    View mDecreaseBt;

    @Bind(R.id.banana_increase_bt)
    View mIncreaseBt;

    @Bind(R.id.reset_bt)
    ImageView mResetBt;

    @Bind(R.id.uploader_view)
    RoundedImageView mUploaderView;

    @Bind(R.id.banana_view)
    TextView mBananaView;

    private int mSelectBananaCount = 1;
    private DraggableViewAnimator mBananaAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_demo);
        ButterKnife.bind(this);

        mDecreaseBt.setOnClickListener(this);
        mIncreaseBt.setOnClickListener(this);
        mResetBt.setOnClickListener(this);

        mBananaView.setText(String.valueOf(mSelectBananaCount));
        mFeedBananaLayout.setFeedActionListener(new FeedBananaLayout.FeedActionListener() {
            @Override
            public void bananaCaught(DraggableViewAnimator bananaAnimator) {
                bananaAnimator.getView().animate().scaleX(SCALE_LARGE).scaleY(SCALE_LARGE).start();
            }

            @Override
            public void bananaPutBack(DraggableViewAnimator bananaAnimator) {
                bananaAnimator.getView().animate().scaleX(1.0f).scaleY(1.0f).start();
            }

            @Override
            public void uploaderSeen(CatcherViewAnimator uploaderAnimator, DraggableViewAnimator bananaAnimator) {
                uploaderAnimator.getView().animate().scaleX(SCALE_LARGE).scaleY(SCALE_LARGE).start();
            }

            @Override
            public void uploaderMissed(CatcherViewAnimator uploaderAnimator, DraggableViewAnimator bananaAnimator) {
                uploaderAnimator.getView().animate().scaleX(1.0f).scaleY(1.0f).start();
            }

            @Override
            public boolean beEatOff(CatcherViewAnimator uploaderAnimator, DraggableViewAnimator bananaAnimator) {
                bananaAnimator.getView().animate().scaleX(0f).scaleY(0f).start();
                mBananaAnimator = bananaAnimator;
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banana_decrease_bt:
                mBananaView.setText(String.valueOf(--mSelectBananaCount));
                break;
            case R.id.banana_increase_bt:
                mBananaView.setText(String.valueOf(++mSelectBananaCount));
                break;
            case R.id.reset_bt:
                if (mBananaAnimator != null) {
                    mBananaAnimator.onRelease();
                }
                break;
        }
    }
}
