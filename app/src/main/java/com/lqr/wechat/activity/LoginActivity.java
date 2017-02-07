package com.lqr.wechat.activity;


import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lqr.wechat.R;
import com.lqr.wechat.model.UserCache;
import com.lqr.wechat.nimsdk.NimAccountSDK;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * @创建者 CSDN_LQR
 * @描述 登录界面
 */
public class LoginActivity extends BaseActivity {

    private String mUsername;
    private String mPassword;
    private String mToken;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.etPhone)
    EditText mEtPhone;
    @InjectView(R.id.etPwd)
    EditText mEtPwd;
    @InjectView(R.id.vLinePhone)
    View mVLinePhone;
    @InjectView(R.id.vLinePwd)
    View mVLinePwd;

    @InjectView(R.id.btnLogin)
    Button mBtnLogin;
    private AbortableFuture<LoginInfo> mLoginRequest;

    @OnClick(R.id.tvOtherLogin)
    public void otherLogin() {
        startActivity(new Intent(this, OtherLoginActivity.class));
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        initToolbar();

        if (!TextUtils.isEmpty(mEtPhone.getText().toString()) && !TextUtils.isEmpty(mEtPwd.getText().toString())) {
            mBtnLogin.setEnabled(true);
        }
    }

    @Override
    public void initListener() {
        /*------------------ 监听编辑框，变换样式 begin ------------------*/
        mEtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mVLinePhone.setBackgroundColor(UIUtils.getColor(R.color.green0));
                } else {
                    mVLinePhone.setBackgroundColor(UIUtils.getColor(R.color.line));
                }
            }
        });
        mEtPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.green0));
                } else {
                    mVLinePwd.setBackgroundColor(UIUtils.getColor(R.color.line));
                }
            }
        });
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(mEtPhone.getText().toString()) && !TextUtils.isEmpty(mEtPwd.getText().toString())) {
                    mBtnLogin.setEnabled(true);
                } else {
                    mBtnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(mEtPhone.getText().toString()) && !TextUtils.isEmpty(mEtPwd.getText().toString())) {
                    mBtnLogin.setEnabled(true);
                } else {
                    mBtnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*------------------ 监听编辑框，变换样式 end ------------------*/

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
                mBtnLogin.setEnabled(false);
            }
        });
        super.initListener();
    }

    /**
     * 设置ToolBar
     */
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("使用手机号登录");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    /**
     * 登录
     */
    public void doLogin() {
        showWaitingDialog("正在登录...");
        mUsername = mEtPhone.getText().toString().trim();
        mPassword = mEtPwd.getText().toString().trim();
        //判断非空
        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
            UIUtils.showToast("用户名和密码不能为空");
            return;
        }
        //根据密码得到token(根据创建密码的方式，如果注册时使用的密码使用了MD5加密过的，就对密码进行md5加密得到token)
//        mToken = MD5Utils.decode16(mPassword);
        mToken = mPassword;

        //配置登录信息，并开始登录
        mLoginRequest = NimAccountSDK.login(mUsername, mToken, new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                onLoginDone();

                //保存用户名到内存中
                UserCache.setAccount(mUsername);
                //保存用户信息到本地，方便下次启动APP做自动登录用
                NimAccountSDK.saveUserAccount(mUsername);
                NimAccountSDK.saveUserToken(mToken);
                //更新本地用户资料
                List<String> list = new ArrayList<String>();
                list.add(UserCache.getAccount());
                NimUserInfoSDK.getUserInfosFormServer(list, null);

                //进行主界面
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == 302 || code == 404) {
                    MaterialDialog materialDialog = showMaterialDialog("", "", "确定", "", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideMaterialDialog();
                        }
                    }, null);
                    TextView tv = new TextView(LoginActivity.this);
                    tv.setText("账号或密码错误，请重新填写。");
                    tv.setTextColor(UIUtils.getColor(R.color.black0));
                    tv.setPadding(0, UIUtils.dip2Px(15), 0, UIUtils.dip2Px(18));
                    materialDialog.setContentView(tv);
//                    UIUtils.showToast("帐号或密码错误");
                } else {
                    UIUtils.showToast("登录失败: " + code);
                }
            }

            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                UIUtils.showToast("无效输入");
            }
        });
    }

    private void onLoginDone() {
        hideWaitingDialog();
        mLoginRequest = null;
        mBtnLogin.setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //ToolBar的返回点击事件
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}