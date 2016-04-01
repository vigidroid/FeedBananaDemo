package com.example.vigi.feedbananademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vigi on 2016/3/29.
 */
public class FeedBananaActivity extends AppCompatActivity implements View.OnClickListener {
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
            public void bananaCaught(View banana) {
                banana.animate().scaleX(1.3f).scaleY(1.3f).start();
            }

            @Override
            public void bananaPutBack(View banana) {
                banana.animate().scaleX(1.0f).scaleY(1.0f).start();
            }

            @Override
            public void uploaderSeen(View banana, View uploader) {

            }

            @Override
            public void uploaderMissed(View banana, View uploader) {

            }

            @Override
            public void beEatOff(View banana, View uploader) {

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
                break;
        }
    }
}
