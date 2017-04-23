package com.lqr.wechat.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lqr.wechat.R;
import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.db.model.Friend;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.PinyinUtils;
import com.lqr.wechat.util.UIUtils;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @创建者 CSDN_LQR
 * @描述 设备备注界面
 */

public class SetAliasActivity extends BaseActivity {

    private String mFriendId;
    private Friend mFriend;

    @Bind(R.id.btnToolbarSend)
    Button mBtnToolbarSend;
    @Bind(R.id.etAlias)
    EditText mEtAlias;

    @Override
    public void init() {
        mFriendId = getIntent().getStringExtra("userId");
    }

    @Override
    public void initView() {
        if (TextUtils.isEmpty(mFriendId)) {
            finish();
            return;
        }

        mBtnToolbarSend.setVisibility(View.VISIBLE);
        mBtnToolbarSend.setText(UIUtils.getString(R.string.complete));
    }

    @Override
    public void initData() {
        mFriend = DBManager.getInstance().getFriendById(mFriendId);
        if (mFriend != null)
            mEtAlias.setText(mFriend.getDisplayName());
        mEtAlias.setSelection(mEtAlias.getText().toString().trim().length());
    }

    @Override
    public void initListener() {
        mEtAlias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBtnToolbarSend.setEnabled(mEtAlias.getText().toString().trim().length() > 0 ? true : false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBtnToolbarSend.setOnClickListener(v -> {
            String displayName = mEtAlias.getText().toString().trim();
            if (TextUtils.isEmpty(displayName)) {
                UIUtils.showToast(UIUtils.getString(R.string.alias_no_empty));
                return;
            }

            showWaitingDialog(UIUtils.getString(R.string.please_wait));
            ApiRetrofit.getInstance().setFriendDisplayName(mFriendId, displayName)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(setFriendDisplayNameResponse -> {
                        if (setFriendDisplayNameResponse.getCode() == 200) {
                            UIUtils.showToast(UIUtils.getString(R.string.change_success));

                            //更新本地好友数据库
                            mFriend.setDisplayName(displayName);
                            mFriend.setDisplayNameSpelling(PinyinUtils.getPinyin(displayName));
                            DBManager.getInstance().saveOrUpdateFriend(mFriend);
                            BroadcastManager.getInstance(SetAliasActivity.this).sendBroadcast(AppConst.UPDATE_FRIEND);
                            BroadcastManager.getInstance(SetAliasActivity.this).sendBroadcast(AppConst.CHANGE_INFO_FOR_USER_INFO);

                            finish();

                        } else {
                            UIUtils.showToast(UIUtils.getString(R.string.change_fail));
                        }
                        hideWaitingDialog();
                    }, this::changeError);
        });
    }

    private void changeError(Throwable throwable) {
        hideWaitingDialog();
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_set_alias;
    }
}
