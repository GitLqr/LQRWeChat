package com.lqr.wechat.ui.activity;

import android.Manifest;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jaeger.library.StatusBarUtil;
import com.lqr.wechat.R;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.util.UIUtils;

import butterknife.Bind;
import kr.co.namee.permissiongen.PermissionGen;

/**
 * @创建者 GitLqr
 * @描述 微信闪屏页
 */
public class SplashActivity extends BaseActivity {

    @Bind(R.id.rlButton)
    RelativeLayout mRlButton;
    @Bind(R.id.btnLogin)
    Button mBtnLogin;
    @Bind(R.id.btnRegister)
    Button mBtnRegister;

    @Override
    public void init() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(
                        //Phone
                        Manifest.permission.READ_PHONE_STATE,
                        //位置
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        //相机、麦克风
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        //存储空间
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request();
        if (!TextUtils.isEmpty(UserCache.getToken())) {
            jumpToActivityAndClearTask(MainActivity.class);
        }
    }

    @Override
    public void initView() {

        StatusBarUtil.setColor(this, UIUtils.getColor(R.color.black));

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        mRlButton.startAnimation(alphaAnimation);
    }

    @Override
    public void initListener() {
        mBtnLogin.setOnClickListener(v -> {
            jumpToActivity(LoginActivity.class);
            finish();
        });
        mBtnRegister.setOnClickListener(v -> {
            jumpToActivity(RegisterActivity.class);
            finish();
        });
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_splash;
    }
}
