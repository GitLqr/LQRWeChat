package com.lqr.wechat.activity;

import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.lqr.wechat.R;
import com.lqr.wechat.factory.PopupWindowFactory;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * @创建者 CSDN_LQR
 * @描述 查看头像
 */
public class ShowBigImageActivity extends BaseActivity {

    private String mUrl;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.pv)
    PhotoView mPv;
    @InjectView(R.id.pb)
    ProgressBar mPb;
    private FrameLayout mView;
    private PopupWindow mPopupWindow;

    @Override
    public void init() {
        mUrl = getIntent().getStringExtra("url");
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_show_big_image);
        ButterKnife.inject(this);
        initToolbar();
        mPv.enable();// 启用图片缩放功能

        ImageLoaderManager.LoadNetImage(mUrl, mPv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemMore:
                showPopupMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("头像");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void showPopupMenu() {
        if (mView == null) {
            mView = new FrameLayout(this);
            mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mView.setBackgroundColor(UIUtils.getColor(R.color.white));

            TextView tv = new TextView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2Px(45));
            tv.setLayoutParams(params);
            tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tv.setPadding(UIUtils.dip2Px(20), 0, 0, 0);
            tv.setTextColor(UIUtils.getColor(R.color.gray0));
            tv.setTextSize(14);
            tv.setText("保存到手机");
            mView.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    //下载头像
                    final String dirPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getPackageName()).getAbsolutePath();
                    final String fileName = "header.jpg";
                    OkHttpUtils.get().url(mUrl).build().execute(new FileCallBack(dirPath, fileName) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            UIUtils.showToast("头像保存失败");
                        }

                        @Override
                        public void onResponse(File response, int id) {
                            UIUtils.showToast("头像保存在" + dirPath + "/" + fileName);
                        }
                    });
                }
            });
        }
        mPopupWindow = PopupWindowFactory.getPopupWindowAtLocation(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopupWindowFactory.makeWindowLight(ShowBigImageActivity.this);
            }
        });
        PopupWindowFactory.makeWindowDark(this);
    }
}
