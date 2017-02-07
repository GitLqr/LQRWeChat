package com.lqr.wechat.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.lqr.wechat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 卡包
 */
public class CardPaketActivity extends BaseActivity {
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @OnClick({R.id.cvVipCard, R.id.cvFriendsCoupon, R.id.cvMyCoupon})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.cvVipCard:
                startActivity(new Intent(this, VipCardActivity.class));
                break;
            case R.id.cvFriendsCoupon:
                startActivity(new Intent(this, FriendsCouponActivity.class));
                break;
            case R.id.cvMyCoupon:
                startActivity(new Intent(this, MyCouponActivity.class));
                break;
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_card_packet);
        ButterKnife.inject(this);
        initToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_one_text, menu);
        menu.getItem(0).setTitle("消息通知");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemOne:
                startActivity(new Intent(this, MsgNotificationActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("卡包");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }
}