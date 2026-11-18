package com.lqr.wechat.ui.activity;

import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.lqr.wechat.R;
import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.util.IOUtils;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.PopupWindowUtils;
import com.lqr.wechat.util.UIUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @创建者 GitLqr
 * @描述 查看头像
 */
public class ShowBigImageActivity extends BaseActivity {

    private String mUrl;

    @Bind(R.id.ibToolbarMore)
    ImageButton mIbToolbarMore;
    @Bind(R.id.pv)
    PhotoView mPv;
    @Bind(R.id.pb)
    ProgressBar mPb;
    private FrameLayout mView;
    private PopupWindow mPopupWindow;

    @Override
    public void init() {
        mUrl = getIntent().getStringExtra("url");
    }

    @Override
    public void initView() {
        setToolbarTitle(UIUtils.getString(R.string.header_pic));
        mIbToolbarMore.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mUrl)) {
            finish();
            return;
        }
        mPv.enable();// 启用图片缩放功能
        Glide.with(this).load(Uri.parse(mUrl)).placeholder(R.mipmap.default_image).centerCrop().into(mPv);
    }

    @Override
    public void initListener() {
        mIbToolbarMore.setOnClickListener(v -> showPopupMenu());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_show_big_image;
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
            tv.setText(UIUtils.getString(R.string.save_to_phone));
            mView.addView(tv);

            tv.setOnClickListener(v -> savePic());
        }
        mPopupWindow = PopupWindowUtils.getPopupWindowAtLocation(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(() -> PopupWindowUtils.makeWindowLight(ShowBigImageActivity.this));
        PopupWindowUtils.makeWindowDark(ShowBigImageActivity.this);
    }

    private void savePic() {
        //Dismiss right away so the menu cannot trigger a second save while this one runs
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
        Observable<Boolean> save;
        if (mUrl.startsWith("file")) {
            File file = new File(Uri.parse(mUrl).getPath());
            save = Observable.fromCallable(() -> writeToDisk(new FileInputStream(file)));
        } else {
            save = ApiRetrofit.getInstance()
                    .mApi
                    .downloadPic(mUrl)
                    .map(responseBody -> writeToDisk(responseBody.byteStream()));
        }
        //Do the IO on the io thread and show the Toast on the main thread
        save.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSaveFinished, throwable -> {
                    LogUtils.e(throwable);
                    onSaveFinished(false);
                });
    }

    private void onSaveFinished(boolean success) {
        UIUtils.showToast(UIUtils.getString(success ? R.string.save_success : R.string.save_fail));
    }

    /**
     * Copy the stream into the header save dir; the stream is always closed
     */
    private boolean writeToDisk(InputStream in) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(AppConst.HEADER_SAVE_DIR, System.currentTimeMillis() + "_header.jpg"));
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return true;
        } catch (IOException e) {
            LogUtils.e(e);
            return false;
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
    }
}

