package com.lqr.wechat.ui.presenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.db.model.GroupMember;
import com.lqr.wechat.db.model.Groups;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.response.QuitGroupResponse;
import com.lqr.wechat.ui.activity.CreateGroupActivity;
import com.lqr.wechat.ui.activity.RemoveGroupMemberActivity;
import com.lqr.wechat.ui.activity.SessionInfoActivity;
import com.lqr.wechat.ui.activity.UserInfoActivity;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.ui.view.ISessionInfoAtView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.PinyinUtils;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lqr.wechat.ui.activity.SessionActivity.SESSION_TYPE_GROUP;
import static com.lqr.wechat.ui.activity.SessionActivity.SESSION_TYPE_PRIVATE;


public class SessionInfoAtPresenter extends BasePresenter<ISessionInfoAtView> {


    private Conversation.ConversationType mConversationType;
    private String mSessionId;
    private List<GroupMember> mData = new ArrayList<>();
    private LQRAdapterForRecyclerView<GroupMember> mAdapter;
    private boolean mIsManager = false;
    public boolean mIsCreateNewGroup = false;
    public String mDisplayName = "";
    private CustomDialog mSetDisplayNameDialog;
    private Groups mGroups;
    private Observable<QuitGroupResponse> quitGroupResponseObservable = null;

    public SessionInfoAtPresenter(BaseActivity context, String sessionId, Conversation.ConversationType conversationType) {
        super(context);
        mSessionId = sessionId;
        mConversationType = conversationType;
    }

    public void loadMembers() {
        loadData();
        setAdapter();
    }

    private void loadData() {
        if (mConversationType == Conversation.ConversationType.PRIVATE) {
            UserInfo userInfo = DBManager.getInstance().getUserInfo(mSessionId);
            if (userInfo != null) {
                mData.clear();
                GroupMember newMember = new GroupMember(mSessionId,
                        userInfo.getUserId(),
                        userInfo.getName(),
                        userInfo.getPortraitUri().toString(),
                        userInfo.getName(),
                        PinyinUtils.getPinyin(userInfo.getName()),
                        PinyinUtils.getPinyin(userInfo.getName()),
                        "",
                        "",
                        "");
                mData.add(newMember);
                mData.add(new GroupMember("", "", ""));//+
            }
            mIsCreateNewGroup = true;
        } else {
            List<GroupMember> groupMembers = DBManager.getInstance().getGroupMembers(mSessionId);
            if (groupMembers != null && groupMembers.size() > 0) {
                Groups groupsById = DBManager.getInstance().getGroupsById(mSessionId);
                if (groupsById != null && groupsById.getRole().equals("0")) {
                    mIsManager = true;
                }
                mData.clear();
                mData.addAll(groupMembers);
                mData.add(new GroupMember("", "", ""));//+
                if (mIsManager) {
                    mData.add(new GroupMember("", "", ""));//-
                }
            }
            mIsCreateNewGroup = false;
        }
        setAdapter();
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<GroupMember>(mContext, mData, R.layout.item_member_info) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, GroupMember item, int position) {
                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                    if (mIsManager && position >= mData.size() - 2) {//+和-
                        if (position == mData.size() - 2) {//+
                            ivHeader.setImageResource(R.mipmap.ic_add_team_member);
                        } else {//-
                            ivHeader.setImageResource(R.mipmap.ic_remove_team_member);
                        }
                        helper.setText(R.id.tvName, "");
                    } else if (!mIsManager && position >= mData.size() - 1) {//+
                        ivHeader.setImageResource(R.mipmap.ic_add_team_member);
                        helper.setText(R.id.tvName, "");
                    } else {
                        Glide.with(mContext).load(item.getPortraitUri()).centerCrop().into(ivHeader);
                        helper.setText(R.id.tvName, item.getName());
                    }
                }
            };
            mAdapter.setOnItemClickListener((helper, parent, itemView, position) -> {
                if (mIsManager && position >= mData.size() - 2) {//+和-
                    if (position == mData.size() - 2) {//+
                        addMember(mConversationType == Conversation.ConversationType.GROUP);
                    } else {//-
                        removeMember();
                    }
                } else if (!mIsManager && position >= mData.size() - 1) {//+
                    addMember(mConversationType == Conversation.ConversationType.GROUP);
                } else {
                    seeUserInfo(DBManager.getInstance().getUserInfo(mData.get(position).getUserId()));
                }
            });
            getView().getRvMember().setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChangedWrapper();
        }
    }

    private void addMember(boolean isAddMember) {

        Intent intent = new Intent(mContext, CreateGroupActivity.class);

        //如果是群组的话就把当前已经的群成员发过去
        if (isAddMember) {
            ArrayList<String> selectedTeamMemberAccounts = new ArrayList<>();
            for (int i = 0; i < mData.size(); i++) {
                selectedTeamMemberAccounts.add(mData.get(i).getUserId());
            }
            intent.putExtra("selectedMember", selectedTeamMemberAccounts);
        }

        mContext.startActivityForResult(intent, SessionInfoActivity.REQ_ADD_MEMBERS);
    }

    private void removeMember() {
        Intent intent = new Intent(mContext, RemoveGroupMemberActivity.class);
        intent.putExtra("sessionId", mSessionId);
        mContext.startActivityForResult(intent, SessionInfoActivity.REQ_REMOVE_MEMBERS);
    }

    private void seeUserInfo(UserInfo userInfo) {
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        intent.putExtra("userInfo", userInfo);
        mContext.jumpToActivity(intent);
    }

    public void addGroupMember(ArrayList<String> selectedIds) {
        LogUtils.sf("addGroupMember : " + selectedIds);
        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
        ApiRetrofit.getInstance().addGroupMember(mSessionId, selectedIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addGroupMemberResponse -> {
                    if (addGroupMemberResponse != null && addGroupMemberResponse.getCode() == 200) {
                        LogUtils.sf("网络请求成功，开始添加群成员：");
                        Groups groups = DBManager.getInstance().getGroupsById(mSessionId);
                        for (String groupMemberId : selectedIds) {
                            UserInfo userInfo = DBManager.getInstance().getUserInfo(groupMemberId);
                            if (userInfo != null) {
                                GroupMember newMember = new GroupMember(mSessionId,
                                        userInfo.getUserId(),
                                        userInfo.getName(),
                                        userInfo.getPortraitUri().toString(),
                                        userInfo.getName(),
                                        PinyinUtils.getPinyin(userInfo.getName()),
                                        PinyinUtils.getPinyin(userInfo.getName()),
                                        groups.getName(),
                                        PinyinUtils.getPinyin(groups.getName()),
                                        groups.getPortraitUri());
                                DBManager.getInstance().saveOrUpdateGroupMember(newMember);
                                LogUtils.sf("添加群成员成功");
                            }
                        }
                        LogUtils.sf("添加群成员结束");
                        mContext.hideWaitingDialog();
                        loadData();
                        LogUtils.sf("重新加载数据");
                        UIUtils.showToast(UIUtils.getString(R.string.add_member_success));
                    }
                }, this::addMembersError);
    }

    public void deleteGroupMembers(ArrayList<String> selectedIds) {
        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
        ApiRetrofit.getInstance().deleGroupMember(mSessionId, selectedIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(deleteGroupMemberResponse -> {
                    if (deleteGroupMemberResponse != null && deleteGroupMemberResponse.getCode() == 200) {
                        LogUtils.sf("网络请求成功，开始删除：");
                        for (int i = 0; i < mData.size(); i++) {
                            GroupMember member = mData.get(i);
                            if (selectedIds.contains(member.getUserId())) {
                                LogUtils.sf("删除用户：" + member.getUserId());
                                member.delete();
                                mData.remove(i);
                                i--;
                            }
                        }
                        LogUtils.sf("删除结束");
                        mContext.hideWaitingDialog();
                        setAdapter();
                        UIUtils.showToast(UIUtils.getString(R.string.del_member_success));
                    } else {
                        LogUtils.sf("网络请求失败");
                        mContext.hideWaitingDialog();
                        UIUtils.showToast(UIUtils.getString(R.string.del_member_fail));
                    }
                }, this::delMembersError);
    }

    private void addMembersError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        mContext.hideWaitingDialog();
        UIUtils.showToast(UIUtils.getString(R.string.add_member_fail));
    }

    private void delMembersError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        mContext.hideWaitingDialog();
        UIUtils.showToast(UIUtils.getString(R.string.del_member_fail));
    }

    public void loadOtherInfo(int sessionType, String sessionId) {
        setToTop();
        switch (sessionType) {
            case SESSION_TYPE_PRIVATE:

                break;
            case SESSION_TYPE_GROUP:
                Observable.just(DBManager.getInstance().getGroupsById(sessionId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(groups -> {
                            if (groups == null)
                                return;
                            mGroups = groups;
                            //设置群信息
                            getView().getOivGroupName().setRightText(groups.getName());
                            mDisplayName = TextUtils.isEmpty(groups.getDisplayName()) ?
                                    DBManager.getInstance().getUserInfo(UserCache.getId()).getName() :
                                    groups.getDisplayName();
                            getView().getOivNickNameInGroup().setRightText(mDisplayName);
                            getView().getBtnQuit().setText(groups.getRole().equals("0") ? UIUtils.getString(R.string.dismiss_this_group) :
                                    UIUtils.getString(R.string.delete_and_exit));
                        }, this::loadOtherError);
                break;
        }
    }

    private void setToTop() {
        Observable.just(RongIMClient.getInstance().getConversation(mConversationType, mSessionId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(conversation -> {
                    if (conversation != null) {
                        getView().getSbToTop().setChecked(conversation.isTop());
                    }
                });
    }

    private void loadOtherError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    public void quit() {
        if (mGroups == null)
            return;
        String tip = "";
        if (mGroups.getRole().equalsIgnoreCase("0")) {
            tip = UIUtils.getString(R.string.are_you_sure_to_dismiss_this_group);
            quitGroupResponseObservable = ApiRetrofit.getInstance().dissmissGroup(mSessionId);
        } else {
            tip = UIUtils.getString(R.string.you_will_never_receive_any_msg_after_quit);
            quitGroupResponseObservable = ApiRetrofit.getInstance().quitGroup(mSessionId);
        }
        mContext.showMaterialDialog(null, tip, UIUtils.getString(R.string.sure), UIUtils.getString(R.string.cancel)
                , v -> quitGroupResponseObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(quitGroupResponse -> {
                            mContext.hideMaterialDialog();
                            if (quitGroupResponse != null && quitGroupResponse.getCode() == 200) {
                                RongIMClient.getInstance().getConversation(mConversationType, mSessionId, new RongIMClient.ResultCallback<Conversation>() {
                                    @Override
                                    public void onSuccess(Conversation conversation) {
                                        RongIMClient.getInstance().clearMessages(Conversation.ConversationType.GROUP, mSessionId, new RongIMClient.ResultCallback<Boolean>() {
                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                RongIMClient.getInstance().removeConversation(mConversationType, mSessionId, null);
                                            }

                                            @Override
                                            public void onError(RongIMClient.ErrorCode errorCode) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {

                                    }
                                });
                                DBManager.getInstance().deleteGroupMembersByGroupId(mSessionId);
                                DBManager.getInstance().deleteGroupsById(mSessionId);
                                BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                                BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_GROUP);
                                BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.CLOSE_CURRENT_SESSION);
                                mContext.finish();
                            } else {
                                UIUtils.showToast(UIUtils.getString(R.string.exit_group_fail));
                            }
                        }, this::quitError)

                , v -> mContext.hideMaterialDialog());
    }

    private void quitError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(UIUtils.getString(R.string.exit_group_fail));
    }

    public void clearConversationMsg() {
        mContext.showMaterialDialog(null, UIUtils.getString(R.string.are_you_sure_to_clear_msg_record), UIUtils.getString(R.string.clear), UIUtils.getString(R.string.cancel)
                , v1 -> RongIMClient.getInstance().clearMessages(mConversationType, mSessionId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        mContext.hideMaterialDialog();
                        BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                        BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.REFRESH_CURRENT_SESSION);
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        mContext.hideMaterialDialog();
                    }
                }), v2 -> mContext.hideMaterialDialog());
    }

    public void setDisplayName() {
        View view = View.inflate(mContext, R.layout.dialog_group_display_name_change, null);
        mSetDisplayNameDialog = new CustomDialog(mContext, view, R.style.MyDialog);
        EditText etName = (EditText) view.findViewById(R.id.etName);
        etName.setText(mDisplayName);
        etName.setSelection(mDisplayName.length());
        view.findViewById(R.id.tvCancle).setOnClickListener(v -> mSetDisplayNameDialog.dismiss());
        view.findViewById(R.id.tvOk).setOnClickListener(v -> {
            String displayName = etName.getText().toString().trim();
            if (!TextUtils.isEmpty(displayName)) {
                ApiRetrofit.getInstance().setGroupDisplayName(mSessionId, displayName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(setGroupDisplayNameResponse -> {
                            if (setGroupDisplayNameResponse != null && setGroupDisplayNameResponse.getCode() == 200) {
                                Groups groups = DBManager.getInstance().getGroupsById(mSessionId);
                                if (groups != null) {
                                    groups.setDisplayName(displayName);
                                    groups.saveOrUpdate("groupid=?", groups.getGroupId());
                                    mDisplayName = displayName;
                                    getView().getOivNickNameInGroup().setRightText(mDisplayName);
                                }
                                UIUtils.showToast(UIUtils.getString(R.string.change_success));
                            } else {
                                UIUtils.showToast(UIUtils.getString(R.string.change_fail));
                            }
                            mSetDisplayNameDialog.dismiss();
                        }, this::setDisplayNameError);
            }
        });
        mSetDisplayNameDialog.show();
    }


    private void setDisplayNameError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(UIUtils.getString(R.string.change_fail));
        mSetDisplayNameDialog.dismiss();
    }
}
