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

    @Bind(R.id.uploader_place_holder)
    Space mUploaderPlaceHolder;

    @Bind(R.id.banana_decrease_bt)
    TextView mDecreaseBt;

    @Bind(R.id.banana_place_holder)
    Space mBananaPlaceHolder;

    @Bind(R.id.banana_increase_bt)
    TextView mIncreaseBt;

    @Bind(R.id.reset_bt)
    ImageView mResetBt;

    @Bind(R.id.uploader_view)
    RoundedImageView mUploaderView;

    @Bind(R.id.banana_view)
    View mBananaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_demo);
        ButterKnife.bind(this);

        mDecreaseBt.setOnClickListener(this);
        mIncreaseBt.setOnClickListener(this);

//        mFeedBananaLayout.feedWhom(mUpLoaderView, mUpLoaderView);
//        mFeedBananaLayout.setFeedActionListener(new FeedBananaLayout.FeedActionListener() {
//
//            @Override
//            public void bananaCaught(Banana banana) {
//                mBananaView.setTextColor(Color.GREEN);
//            }
//
//            @Override
//            public void bananaPutBack() {
//                mBananaView.setTextColor(Color.WHITE);
//            }
//
//            @Override
//            public void eaterSeeIt() {
//                mBananaView.setTextColor(Color.RED);
//                mUpLoaderView.setBackgroundColor(Color.LTGRAY);
//            }
//
//            @Override
//            public void eaterNotSeeIt() {
//                mBananaView.setTextColor(Color.GREEN);
//                mUpLoaderView.setBackgroundColor(Color.TRANSPARENT);
//            }
//
//            @Override
//            public void beEatOff(int count) {
//                Toast.makeText(FeedBDemoActivity.this, "吃了" + count + "个香蕉", Toast.LENGTH_LONG).show();
//                mFeedBananaLayout.eatUp(false, mSuccessAlert);
//            }
//
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.icon_close:
//                finish();
//                break;
//            case R.id.decrease_banana: {
//                final int oldCount = mBananaView.getCount();
//                if (oldCount > 1) {
//                    final int newCount = oldCount - 1;
//                    mBananaView.setCount(newCount);
//                    if (newCount <= 1) {
//                        mDeCreaseView.setClickable(false);
//                    }
//                    if (!mInCreaseView.isClickable()) {
//                        mInCreaseView.setClickable(true);
//                    }
//                }
//                break;
//            }
//            case R.id.increase_banana: {
//                final int oldCount = mBananaView.getCount();
//                if (oldCount < 5) {
//                    final int newCount = oldCount + 1;
//                    mBananaView.setCount(newCount);
//                    if (newCount >= 5) {
//                        mInCreaseView.setClickable(false);
//                    }
//                    if (!mDeCreaseView.isClickable()) {
//                        mDeCreaseView.setClickable(true);
//                    }
//                }
//                break;
//            }
        }
    }
}
