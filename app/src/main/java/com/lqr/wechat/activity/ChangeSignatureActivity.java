package com.lqr.wechat.activity;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lqr.wechat.R;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 更改个性签名
 */
public class ChangeSignatureActivity extends BaseActivity {

    private String mSignature;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btnOk)
    Button mBtnOk;
    @InjectView(R.id.etName)
    EditText mEtName;
    @InjectView(R.id.tvCount)
    TextView mTvCount;

    @OnClick({R.id.btnOk})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                String name = mEtName.getText().toString();
                showWaitingDialog("请稍等");
                Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                fields.put(UserInfoFieldEnum.SIGNATURE, name);
                NimUserInfoSDK.updateUserInfo(fields, new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int code, Void result, Throwable exception) {
                        hideWaitingDialog();
                        finish();
                    }
                });
                break;
        }
    }

    @Override
    public void init() {
        mSignature = getIntent().getStringExtra("signature");
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_change_signature);
        ButterKnife.inject(this);
        initToolbar();
        mEtName.setText(mSignature);
        mEtName.setSelection(mSignature.length());
        mTvCount.setText(String.valueOf(30 - mEtName.getText().toString().length()));
    }

    @Override
    public void initListener() {
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTvCount.setText(String.valueOf(30 - mEtName.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("更改名字");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mBtnOk.setVisibility(View.VISIBLE);
    }

}
