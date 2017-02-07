package com.lqr.wechat.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.optionitemview.OptionItemView;
import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.model.UserCache;
import com.lqr.wechat.nimsdk.NimHistorySDK;
import com.lqr.wechat.nimsdk.NimRecentContactSDK;
import com.lqr.wechat.nimsdk.NimTeamSDK;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.StringUtils;
import com.lqr.wechat.utils.UIUtils;
import com.lqr.wechat.view.CustomDialog;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @创建者 CSDN_LQR
 * @描述 群聊信息界面
 */
public class TeamCheatInfoActivity extends BaseActivity {

    public static final String GROUP_CHEAT_INFO_TEAMID = "teamId";
    public static final int REQ_ADD_MEMBERS = 1000;
    public static final int REQ_REMOVE_MEMBERS = 1001;
    public static final int REQ_CHANGE_NAME = 1002;
    public static final int REQ_WATCH_USER_INFO = 1003;
    public static final int RESP_QUIT_TEAM = 10000;
    public static final int RESP_CHEAT_SINGLE = 10001;
    public static final int RESP_CLEAR_CHATTING_RECORD_HISTORY = 10002;

    private Intent mIntent;
    private String mTeamId;
    private Team mTeam;
    private boolean mIsClearChattingHistory = false;
    private boolean mIsManager;//标记当前用户是不是管理员

    private Observer<TeamMember> memberRemoveObserver;
    private Observer<List<TeamMember>> memberUpdateObserver;

    private List<TeamMember> mTeamMemberList = new ArrayList<>();
    private LQRAdapterForRecyclerView mAdapter;
    private CustomDialog mDialog;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.rvMember)
    LQRRecyclerView mRvMember;
    @InjectView(R.id.oivTeamName)
    OptionItemView mOivTeamName;
    @InjectView(R.id.tvAnnouncement)
    TextView mTvAnnouncement;
    @InjectView(R.id.vLineTeamManage)
    View mVLineTeamManage;
    @InjectView(R.id.oivTeamManage)
    OptionItemView mOivTeamManage;
    @InjectView(R.id.oivNickNameInTeam)
    OptionItemView mOivNickNameInTeam;
    @InjectView(R.id.llShowNickName)
    LinearLayout mLlShowNickName;
    @InjectView(R.id.sbShowNickName)
    SwitchButton mSbShowNickName;

    @OnClick({R.id.oivTeamName, R.id.oivQRCordCard, R.id.llAnnouncement, R.id.oivNickNameInTeam, R.id.btnQuitTeam, R.id.oivClearMsgRecord})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.oivTeamName:
                mIntent = new Intent(this, TeamNameSetActivity.class);
                mIntent.putExtra(TeamNameSetActivity.TEAM_ID, mTeamId);
                startActivity(mIntent);
                break;
            case R.id.oivQRCordCard:
                mIntent = new Intent(this, QRCodeCardActivity.class);
                mIntent.putExtra(QRCodeCardActivity.QRCODE_TEAM, mTeam);
                startActivity(mIntent);
                break;
            case R.id.llAnnouncement:
                if (mIsManager) {
                    //编辑公告
                    mIntent = new Intent(this, TeamAnnouncementEditActivity.class);
                    mIntent.putExtra(TeamAnnouncementEditActivity.TEAM, mTeam);
                    startActivity(mIntent);
                } else {
                    showMaterialDialog("", "只有群主可以编辑群公告", "知道了", "", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideMaterialDialog();
                        }
                    }, null);
                }
                break;
            case R.id.oivNickNameInTeam:
                showChangeNickNameDialog();
                break;
            case R.id.btnQuitTeam:
                showWaitingDialog("请稍等");
                NimTeamSDK.quitTeam(mTeamId, new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        hideWaitingDialog();
                        //退出群聊时顺便清除本地最后联系人中的群消息
                        NimRecentContactSDK.deleteRecentContactAndNotify(mTeamId, SessionTypeEnum.Team);
                        setResult(RESP_QUIT_TEAM);
                        onBackPressed();
                    }

                    @Override
                    public void onFailed(int code) {
                        hideWaitingDialog();
                        UIUtils.showToast("退群失败" + code);
                    }

                    @Override
                    public void onException(Throwable exception) {
                        hideWaitingDialog();
                        UIUtils.showToast("退群失败");
                        exception.printStackTrace();
                    }
                });
                break;
            case R.id.oivClearMsgRecord:
                showMaterialDialog("", "确定删除群的聊天记录吗?", "清空", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMaterialDialog();
                        NimHistorySDK.clearChattingHistory(mTeamId, SessionTypeEnum.Team);
                        mIsClearChattingHistory = true;
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMaterialDialog();
                    }
                });
                break;
        }
    }

    @Override
    public void init() {
        mTeamId = getIntent().getStringExtra(TeamCheatInfoActivity.GROUP_CHEAT_INFO_TEAMID);
        if (TextUtils.isEmpty(mTeamId)) {
            interrupt();
            return;
        }

        mTeam = NimTeamSDK.queryTeamBlock(mTeamId);
//        mIsManager = UserCache.getAccount().equals(mTeam.getCreator());
        TeamMemberType myMemberType = NimTeamSDK.queryTeamMemberBlock(mTeamId, UserCache.getAccount()).getType();
        if (myMemberType == TeamMemberType.Manager || myMemberType == TeamMemberType.Owner) {
            mIsManager = true;
        } else {
            mIsManager = false;
        }

        //监听群成员变化
        observeMemberUpdate();
        observeMemberRemove();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_team_cheat_info);
        ButterKnife.inject(this);

        initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTeam = NimTeamSDK.queryTeamBlock(mTeamId);
        getSupportActionBar().setTitle("群聊信息(" + mTeam.getMemberCount() + ")");
        mOivTeamName.setRightText(TextUtils.isEmpty(mTeam.getName()) ? "未命名" : mTeam.getName());
        mOivNickNameInTeam.setRightText(NimTeamSDK.getTeamMemberDisplayNameWithoutMe(mTeamId, UserCache.getAccount()));
        if (!TextUtils.isEmpty(mTeam.getAnnouncement())) {
            mTvAnnouncement.setVisibility(View.VISIBLE);
            mTvAnnouncement.setText(mTeam.getAnnouncement());
        } else {
            mTvAnnouncement.setVisibility(View.GONE);
        }

//        if (mIsManager) {
//            mVLineTeamManage.setVisibility(View.VISIBLE);
//            mOivTeamManage.setVisibility(View.VISIBLE);
//        } else {
//            mVLineTeamManage.setVisibility(View.GONE);
//            mOivTeamManage.setVisibility(View.GONE);
//        }

        //是否显示群昵称
        mSbShowNickName.setChecked(NimTeamSDK.shouldShowNickName(mTeamId));
    }

    @Override
    public void initData() {
        //查询本群中成员
        NimTeamSDK.queryMemberList(mTeamId, new RequestCallback<List<TeamMember>>() {
            @Override
            public void onSuccess(List<TeamMember> param) {
                if (!StringUtils.isEmpty(param)) {
                    mTeamMemberList.clear();
                    mTeamMemberList.addAll(param);
                    if (mIsManager) {
                        mTeamMemberList.add(null);
                        mTeamMemberList.add(null);
                    } else {
                        mTeamMemberList.add(null);
                    }
                    //更新本地群成员资料
                    List<String> accountList = new ArrayList<>(param.size());
                    for (TeamMember tm : param) {
                        accountList.add(tm.getAccount());
                    }
                    if (!StringUtils.isEmpty(accountList)) {
                        NimUserInfoSDK.getUserInfosFormServer(accountList, new RequestCallback<List<NimUserInfo>>() {
                            @Override
                            public void onSuccess(List<NimUserInfo> param) {
                                setAdapter();
                            }

                            @Override
                            public void onFailed(int code) {
                                UIUtils.showToast("获取群成员信息失败" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                exception.printStackTrace();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailed(int code) {
                UIUtils.showToast("查询成员列表失败" + code);
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void initListener() {
        mLlShowNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSbShowNickName.setChecked(!mSbShowNickName.isChecked());
            }
        });
        mSbShowNickName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showWaitingDialog("请稍等");
                NimTeamSDK.setShouldShowNickName(mTeamId, isChecked, new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        hideWaitingDialog();
                    }

                    @Override
                    public void onFailed(int code) {
                        UIUtils.showToast("设置失败" + code);
                        hideWaitingDialog();
                    }

                    @Override
                    public void onException(Throwable exception) {
                        exception.printStackTrace();
                        hideWaitingDialog();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_ADD_MEMBERS:
                if (resultCode == RESULT_OK) {
                    showWaitingDialog("请稍等");
                    //拉人入群
                    ArrayList<String> accounts = data.getStringArrayListExtra(TeamCheatCreateActvitiy.ADD_TEAM_MEMBER);
                    NimTeamSDK.addMembers(mTeamId, accounts, new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            hideWaitingDialog();
                        }

                        @Override
                        public void onFailed(int code) {
                            UIUtils.showToast("拉人入群失败" + code);
                            hideWaitingDialog();
                        }

                        @Override
                        public void onException(Throwable exception) {
                            exception.printStackTrace();
                            UIUtils.showToast("拉人入群失败");
                            hideWaitingDialog();
                        }
                    });
                }
                break;
            case REQ_REMOVE_MEMBERS:
                if (resultCode == RESULT_OK) {
                    showWaitingDialog("请稍等");
                    ArrayList<String> accounts = data.getStringArrayListExtra(TeamCheatRemoveMemberActivity.REMOVE_TEAM_MEMBER);
                    InvocationFuture<Void> invocationFuture = NimTeamSDK.removeMembers(mTeamId, accounts);
                    invocationFuture.setCallback(new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            hideWaitingDialog();
                        }

                        @Override
                        public void onFailed(int code) {
                            UIUtils.showToast("踢人出群失败" + code);
                            hideWaitingDialog();
                        }

                        @Override
                        public void onException(Throwable exception) {
                            exception.printStackTrace();
                            UIUtils.showToast("踢人出群失败");
                            hideWaitingDialog();
                        }
                    });
                }
                break;
            case REQ_WATCH_USER_INFO:
                if (resultCode == RESULT_OK) {
                    setResult(RESP_CHEAT_SINGLE);
                    onBackPressed();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NimTeamSDK.observeMemberUpdate(memberUpdateObserver, false);
        NimTeamSDK.observeMemberRemove(memberRemoveObserver, false);
    }

    @Override
    public void onBackPressed() {
        if (mIsClearChattingHistory)
            setResult(RESP_CLEAR_CHATTING_RECORD_HISTORY);
        super.onBackPressed();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView(this, R.layout.item_member_info_group_cheat_rv, mTeamMemberList) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, Object obj, int position) {
                    final ImageView ivHeader = helper.getView(R.id.ivHeader);
                    if (mIsManager && position >= mTeamMemberList.size() - 2) {//+和-
                        if (position == mTeamMemberList.size() - 2) {//+
                            ivHeader.setImageResource(R.mipmap.ic_add_team_member);
                            helper.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //拉人入群
                                    addMembers();
                                }
                            });
                        } else {//-
                            helper.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //踢人出群
                                    removeMember();
                                }
                            });
                            ivHeader.setImageResource(R.mipmap.ic_remove_team_member);
                        }
                        helper.setText(R.id.tvName, "");
                    } else if (!mIsManager && position >= mTeamMemberList.size() - 1) {//+
                        helper.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //拉人入群
                                addMembers();
                            }
                        });
                        ivHeader.setImageResource(R.mipmap.ic_add_team_member);
                        helper.setText(R.id.tvName, "");
                    } else {
                        final TeamMember item = (TeamMember) obj;
                        helper.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //查看对方资料
                                Intent intent = new Intent(TeamCheatInfoActivity.this, UserInfoActivity.class);
                                intent.putExtra(UserInfoActivity.USER_INFO_ACCOUNT, item.getAccount());
                                startActivityForResult(intent, REQ_WATCH_USER_INFO);
                            }
                        });
                        helper.setText(R.id.tvName, NimTeamSDK.getTeamMemberDisplayNameWithoutMe(item.getTid(), item.getAccount()));
                        String account = item.getAccount();
                        NimUserInfo userInfo = NimUserInfoSDK.getUser(account);
                        if (userInfo == null) {
                            NimUserInfoSDK.getUserInfoFromServer(account, new RequestCallback<List<NimUserInfo>>() {
                                @Override
                                public void onSuccess(List<NimUserInfo> param) {
                                    if (!StringUtils.isEmpty(param)) {
                                        NimUserInfo userInfo = param.get(0);
                                        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                                            ImageLoaderManager.LoadNetImage(userInfo.getAvatar(), ivHeader);
                                        } else {
                                            ivHeader.setImageResource(R.mipmap.default_header);
                                        }
                                    }
                                }

                                @Override
                                public void onFailed(int code) {
                                    ivHeader.setImageResource(R.mipmap.default_header);
                                }

                                @Override
                                public void onException(Throwable exception) {
                                    ivHeader.setImageResource(R.mipmap.default_header);
                                }
                            });
                        } else {
                            if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                                ImageLoaderManager.LoadNetImage(userInfo.getAvatar(), ivHeader);
                            } else {
                                ivHeader.setImageResource(R.mipmap.default_header);
                            }
                        }
                    }
                }
            };
            mRvMember.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 拉人入群
     */
    private void addMembers() {
        //得到群成员账号集合
        ArrayList<String> selectedTeamMemberAccounts = new ArrayList<>();
        for (int i = 0; i < mTeam.getMemberCount(); i++) {
            selectedTeamMemberAccounts.add(mTeamMemberList.get(i).getAccount());
        }

        Intent intent = new Intent(this, TeamCheatCreateActvitiy.class);
        intent.putStringArrayListExtra(TeamCheatCreateActvitiy.ADD_TEAM_MEMBER, selectedTeamMemberAccounts);
        startActivityForResult(intent, REQ_ADD_MEMBERS);
    }

    /**
     * 踢人出群
     */
    private void removeMember() {
        Intent intent = new Intent(this, TeamCheatRemoveMemberActivity.class);
        intent.putExtra(TeamCheatRemoveMemberActivity.TEAMID, mTeamId);
        startActivityForResult(intent, REQ_REMOVE_MEMBERS);
    }

    private void observeMemberUpdate() {
        memberUpdateObserver = new Observer<List<TeamMember>>() {
            @Override
            public void onEvent(List<TeamMember> teamMembers) {
                initData();
                onResume();
            }
        };
        NimTeamSDK.observeMemberUpdate(memberUpdateObserver, true);
    }

    private void observeMemberRemove() {
        memberRemoveObserver = new Observer<TeamMember>() {
            @Override
            public void onEvent(TeamMember teamMember) {
                initData();
                onResume();
            }
        };
        NimTeamSDK.observeMemberRemove(memberRemoveObserver, true);
    }

    private void showChangeNickNameDialog() {
        View view = View.inflate(this, R.layout.dialog_team_nick_change, null);
        mDialog = new CustomDialog(this, view, R.style.dialog);
        mDialog.setCancelable(false);
        mDialog.show();
        final EditText etName = (EditText) view.findViewById(R.id.etName);
//        String nickName = NimTeamSDK.getTeamNick(mTeamId, UserCache.getAccount());
        String nickName = NimTeamSDK.getTeamMemberDisplayNameWithoutMe(mTeamId, UserCache.getAccount());
        etName.setText(nickName);
        if (!TextUtils.isEmpty(nickName) && nickName.length() > 0)
            etName.setSelection(nickName.length());
        view.findViewById(R.id.tvCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                mDialog = null;
            }
        });
        view.findViewById(R.id.tvOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改我在群中的昵称
                String newNickName = etName.getText().toString().trim();
                if (!TextUtils.isEmpty(newNickName)) {
                    NimTeamSDK.updateMyTeamNick(mTeamId, newNickName, new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            UIUtils.showToast("修改成功");
                            mDialog.dismiss();
                            mDialog = null;
                            onResume();
                        }

                        @Override
                        public void onFailed(int code) {
                            switch (code) {
                                case 805:
                                    UIUtils.showToast("网易云信的普通群不支持修改自己的群昵称");
                                    break;
                                default:
                                    UIUtils.showToast("修改失败" + code);
                                    break;
                            }
                        }

                        @Override
                        public void onException(Throwable exception) {
                            UIUtils.showToast("修改失败");
                            exception.printStackTrace();
                        }
                    });
                }
            }
        });
    }
}
