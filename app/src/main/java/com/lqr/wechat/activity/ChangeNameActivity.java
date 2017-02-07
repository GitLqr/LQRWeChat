package com.lqr.wechat.activity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
 * @描述 更改名字
 */
public class ChangeNameActivity extends BaseActivity {

    private String mName;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btnOk)
    Button mBtnOk;
    @InjectView(R.id.etName)
    EditText mEtName;

    @OnClick({R.id.btnOk})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                String name = mEtName.getText().toString();
                if (TextUtils.isEmpty(name.trim())) {
                    showMaterialDialog("提示", "没有输入昵称，请重新填写", "确定", "", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideMaterialDialog();
                        }
                    }, null);
                } else {
                    showWaitingDialog("请稍等");
                    Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
                    fields.put(UserInfoFieldEnum.Name, name);
                    NimUserInfoSDK.updateUserInfo(fields, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            hideWaitingDialog();
                            finish();
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void init() {
        mName = getIntent().getStringExtra("name");
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_change_name);
        ButterKnife.inject(this);
        initToolbar();
        mEtName.setText(mName);
        mEtName.setSelection(mName.length());
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
