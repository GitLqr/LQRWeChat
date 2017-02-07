package com.lqr.wechat.activity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lqr.wechat.R;
import com.lqr.wechat.nimsdk.NimTeamSDK;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * @创建者 CSDN_LQR
 * @描述 群名片界面
 */
public class TeamNameSetActivity extends BaseActivity {

    public static final String TEAM_ID = "teamId";

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.btnOk)
    Button mBtnOk;

    @InjectView(R.id.etName)
    EditText mEtName;
    private String mTeamId;
    private Team mTeam;

    @OnClick(R.id.btnOk)
    public void click() {
        final String teamName = mEtName.getText().toString().trim();
//        if (!TextUtils.isEmpty(teamName)) {
            showWaitingDialog("修改群名片");
            Map<TeamFieldEnum, Serializable> fields = new HashMap<>(1);
            fields.put(TeamFieldEnum.Name, teamName);
            InvocationFuture<Void> invocationFuture = NimTeamSDK.updateTeamFields(mTeamId, fields);
            invocationFuture.setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void param) {
                    hideWaitingDialog();
                    finish();
                }

                @Override
                public void onFailed(int code) {
                    UIUtils.showToast("修改群名片失败" + code);
                    hideWaitingDialog();
                }

                @Override
                public void onException(Throwable exception) {
                    exception.printStackTrace();
                    hideWaitingDialog();
                }
            });
//        }
    }

    @Override
    public void init() {
        mTeamId = getIntent().getStringExtra(TEAM_ID);
        if (TextUtils.isEmpty(mTeamId)) {
            interrupt();
            return;
        }

        mTeam = NimTeamSDK.queryTeamBlock(mTeamId);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_team_name_set);
        ButterKnife.inject(this);

        initToolbar();
        mEtName.setText(mTeam.getName());
        mEtName.setSelection(mTeam.getName().length());
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
        getSupportActionBar().setTitle("群名片");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);

        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setText("保存");
    }
}
