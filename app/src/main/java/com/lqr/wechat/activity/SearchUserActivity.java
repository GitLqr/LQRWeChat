package com.lqr.wechat.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqr.wechat.R;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.KeyBoardUtils;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @创建者 CSDN_LQR
 * @描述 搜索用户（本地、网上）
 */
public class SearchUserActivity extends BaseActivity {

    private NimUserInfo mUser;

    public static final String SEARCH_TYPE = "search_type";
    public boolean isSearchUserLocal = SEARCH_USER_LOCAL;
    public static final boolean SEARCH_USER_LOCAL = true;
    public static final boolean SEARCH_USER_REMOTE = false;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.etSearch)
    EditText mEtSearch;

    @InjectView(R.id.rlNoResultTip)
    RelativeLayout mRlNoResultTip;
    @InjectView(R.id.llSearch)
    LinearLayout mLlSearch;
    @InjectView(R.id.tvMsg)
    TextView mTvMsg;

    @Override
    public void init() {
        isSearchUserLocal = getIntent().getBooleanExtra(SEARCH_TYPE, SEARCH_USER_LOCAL);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_search_user);
        ButterKnife.inject(this);
        initToolbar();
    }

    @Override
    public void initListener() {
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRlNoResultTip.setVisibility(View.GONE);
                if (TextUtils.isEmpty(mEtSearch.getText().toString().trim())) {
                    mLlSearch.setVisibility(View.GONE);
                } else {
                    mLlSearch.setVisibility(View.VISIBLE);
                    mTvMsg.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //监听键盘回车或搜索
        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (TextUtils.isEmpty(mEtSearch.getText().toString().trim())) {
                        KeyBoardUtils.closeKeybord(mEtSearch, SearchUserActivity.this);
                    } else {
                        doSearch();
                    }
                    return true;
                }
                return false;
            }
        });

        mLlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });
    }

    private void doSearch() {
        showWaitingDialog("请稍等");
        String account = mEtSearch.getText().toString().trim();
        if (isSearchUserLocal) {
            mUser = NimUserInfoSDK.getUser(account);
            searchDone();
        } else {
            NimUserInfoSDK.getUserInfoFromServer(account, new RequestCallback<List<NimUserInfo>>() {
                @Override
                public void onSuccess(List<NimUserInfo> param) {
                    if (param != null && param.size() > 0) {
                        mUser = param.get(0);
                        searchDone();
                    }
                }

                @Override
                public void onFailed(int code) {
                    UIUtils.showToast("搜索失败" + code);
                    hideWaitingDialog();
                }

                @Override
                public void onException(Throwable exception) {
                    exception.printStackTrace();
                    hideWaitingDialog();
                }
            });
        }
    }

    private void searchDone() {
        hideWaitingDialog();
        if (mUser == null) {
            mRlNoResultTip.setVisibility(View.VISIBLE);
        } else {
            mRlNoResultTip.setVisibility(View.GONE);
            //跳转到用户信息界面
            Intent intent = new Intent(this, UserInfoActivity.class);
            intent.putExtra("account", mUser.getAccount());
            startActivity(intent);
        }
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
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mEtSearch.setVisibility(View.VISIBLE);
        mEtSearch.setHintTextColor(UIUtils.getColor(R.color.gray2));
        mEtSearch.setTextColor(UIUtils.getColor(R.color.white));
    }

}
