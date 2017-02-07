package com.lqr.wechat.activity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lqr.wechat.R;
import com.lqr.wechat.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @创建者 CSDN_LQR
 * @描述 所有标签界面
 */
public class AllTagActvitiy extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_all_tag);
        ButterKnife.inject(this);
        initToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_one_text, menu);
        menu.getItem(0).setTitle("新建");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemOne:
                UIUtils.showToast("新建");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("所有标签");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }
}
