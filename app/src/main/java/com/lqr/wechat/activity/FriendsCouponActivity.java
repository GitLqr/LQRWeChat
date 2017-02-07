package com.lqr.wechat.activity;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.lqr.wechat.R;
import com.lqr.wechat.view.CustomDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @创建者 CSDN_LQR
 * @描述 朋友的优惠券-卡包
 */
public class FriendsCouponActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    private CustomDialog mDialog;

    @Override
    public void initView() {
        setContentView(R.layout.activity_friends_coupon);
        ButterKnife.inject(this);
        initToolbar();
        showTipDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("朋友的优惠券");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void showTipDialog() {
        View view = View.inflate(this, R.layout.dialog_tip_friends_coupon, null);
        mDialog = new CustomDialog(this, view, R.style.dialog);
        mDialog.setCancelable(false);
        mDialog.show();
        view.findViewById(R.id.tvOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mDialog = null;
            }
        });
    }
}