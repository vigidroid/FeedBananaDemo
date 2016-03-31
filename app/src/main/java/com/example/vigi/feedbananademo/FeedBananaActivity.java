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
        mResetBt.setOnClickListener(this);

        mFeedBananaLayout.makeFollow(mUploaderView, mBananaView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banana_decrease_bt:
                break;
            case R.id.banana_increase_bt:
                break;
            case R.id.reset_bt:
                break;
        }
    }
}
