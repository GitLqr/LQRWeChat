package com.lqr.wechat.ui.presenter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRHeaderAndFooterAdapter;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.db.model.Friend;
import com.lqr.wechat.db.model.Groups;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.response.CreateGroupResponse;
import com.lqr.wechat.ui.activity.CreateGroupActivity;
import com.lqr.wechat.ui.activity.SessionActivity;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.ui.view.ICreateGroupAtView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.SortUtils;
import com.lqr.wechat.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CreateGroupAtPresenter extends BasePresenter<ICreateGroupAtView> {

    private String mGroupName = "";
    private List<Friend> mData = new ArrayList<>();
    private List<Friend> mSelectedData = new ArrayList<>();
    private LQRHeaderAndFooterAdapter mAdapter;
    private LQRAdapterForRecyclerView<Friend> mSelectedAdapter;

    public CreateGroupAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadContacts() {
        loadData();
        setAdapter();
        setSelectedAdapter();
    }

    private void loadData() {
        Observable.just(DBManager.getInstance().getFriends())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friends -> {
                    if (friends != null && friends.size() > 0) {
                        mData.clear();
                        mData.addAll(friends);
                        //整理排序
                        SortUtils.sortContacts(mData);
                        if (mAdapter != null)
                            mAdapter.notifyDataSetChanged();
                    }
                }, this::loadError);
    }

    private void setAdapter() {
        if (mAdapter == null) {
            LQRAdapterForRecyclerView adapter = new LQRAdapterForRecyclerView<Friend>(mContext, mData, R.layout.item_contact) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, Friend item, int position) {
                    helper.setText(R.id.tvName, item.getDisplayName()).setViewVisibility(R.id.cb, View.VISIBLE);
                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                    Glide.with(mContext).load(item.getPortraitUri()).centerCrop().into(ivHeader);
                    CheckBox cb = helper.getView(R.id.cb);

                    //如果添加群成员的话，需要判断是否已经在群中
                    if (((CreateGroupActivity) mContext).mSelectedTeamMemberAccounts != null &&
                            ((CreateGroupActivity) mContext).mSelectedTeamMemberAccounts.contains(item.getUserId())) {
                        cb.setChecked(true);
                        helper.setEnabled(R.id.cb, false).setEnabled(R.id.root, false);
                    } else {
                        helper.setEnabled(R.id.cb, true).setEnabled(R.id.root, true);
                        //没有在已有群中的联系人，根据当前的选中结果判断
                        cb.setChecked(mSelectedData.contains(item) ? true : false);
                    }

                    String str = "";
                    //得到当前字母
                    String currentLetter = item.getDisplayNameSpelling().charAt(0) + "";
                    if (position == 0) {
                        str = currentLetter;
                    } else {
                        //得到上一个字母
                        String preLetter = mData.get(position - 1).getDisplayNameSpelling().charAt(0) + "";
                        //如果和上一个字母的首字母不同则显示字母栏
                        if (!preLetter.equalsIgnoreCase(currentLetter)) {
                            str = currentLetter;
                        }

                        int nextIndex = position + 1;
                        if (nextIndex < mData.size() - 1) {
                            //得到下一个字母
                            String nextLetter = mData.get(nextIndex).getDisplayNameSpelling().charAt(0) + "";
                            //如果和下一个字母的首字母不同则隐藏下划线
                            if (!nextLetter.equalsIgnoreCase(currentLetter)) {
                                helper.setViewVisibility(R.id.vLine, View.VISIBLE);
                            } else {
                                helper.setViewVisibility(R.id.vLine, View.VISIBLE);
                            }
                        } else {
                            helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
                        }
                    }
                    if (position == mData.size() - 1) {
                        helper.setViewVisibility(R.id.vLine, View.GONE);
                    }

                    //根据str是否为空决定字母栏是否显示
                    if (TextUtils.isEmpty(str)) {
                        helper.setViewVisibility(R.id.tvIndex, View.GONE);
                    } else {
                        helper.setViewVisibility(R.id.tvIndex, View.VISIBLE);
                        helper.setText(R.id.tvIndex, str);
                    }
                }
            };
            adapter.addHeaderView(getView().getHeaderView());
            mAdapter = adapter.getHeaderAndFooterAdapter();
            getView().getRvContacts().setAdapter(mAdapter);

            ((LQRAdapterForRecyclerView) mAdapter.getInnerAdapter()).setOnItemClickListener((lqrViewHolder, viewGroup, view, i) -> {
                //选中或反选
                Friend friend = mData.get(i - 1);
                if (mSelectedData.contains(friend)) {
                    mSelectedData.remove(friend);
                } else {
                    mSelectedData.add(friend);
                }
                mSelectedAdapter.notifyDataSetChangedWrapper();
                mAdapter.notifyDataSetChanged();
                if (mSelectedData.size() > 0) {
                    getView().getBtnToolbarSend().setEnabled(true);
                    getView().getBtnToolbarSend().setText(UIUtils.getString(R.string.sure_with_count, mSelectedData.size()));
                } else {
                    getView().getBtnToolbarSend().setEnabled(false);
                    getView().getBtnToolbarSend().setText(UIUtils.getString(R.string.sure));
                }
            });
        }
    }

    private void setSelectedAdapter() {
        if (mSelectedAdapter == null) {
            mSelectedAdapter = new LQRAdapterForRecyclerView<Friend>(mContext, mSelectedData, R.layout.item_selected_contact) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, Friend item, int position) {
                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                    Glide.with(mContext).load(item.getPortraitUri()).centerCrop().into(ivHeader);
                }
            };
            getView().getRvSelectedContacts().setAdapter(mSelectedAdapter);
        }
    }

    public void addGroupMembers() {
        ArrayList<String> selectedIds = new ArrayList<>(mSelectedData.size());
        for (int i = 0; i < mSelectedData.size(); i++) {
            Friend friend = mSelectedData.get(i);
            selectedIds.add(friend.getUserId());
        }
        Intent data = new Intent();
        data.putStringArrayListExtra("selectedIds", selectedIds);
        mContext.setResult(Activity.RESULT_OK, data);
        mContext.finish();
    }

    public void createGroup() {
        mSelectedData.add(0, DBManager.getInstance().getFriendById(UserCache.getId()));
        int size = mSelectedData.size();
        if (size == 0)
            return;

        List<String> selectedIds = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Friend friend = mSelectedData.get(i);
            selectedIds.add(friend.getUserId());
        }
        mGroupName = "";
        if (size > 3) {
            for (int i = 0; i < 3; i++) {
                Friend friend = mSelectedData.get(i);
                mGroupName += friend.getName() + "、";
            }
        } else {
            for (Friend friend : mSelectedData) {
                mGroupName += friend.getName() + "、";
            }
        }
        mGroupName = mGroupName.substring(0, mGroupName.length() - 1);

        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
        ApiRetrofit.getInstance().createGroup(mGroupName, selectedIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createGroupResponse -> {
                    mContext.hideWaitingDialog();
                    if (createGroupResponse.getCode() == 200) {
                        UIUtils.showToast(UIUtils.getString(R.string.create_group_success));
                        CreateGroupResponse.ResultEntity resultEntity = createGroupResponse.getResult();
                        DBManager.getInstance().saveOrUpdateGroup(new Groups(resultEntity.getId(), mGroupName, null, String.valueOf(0)));
                        Intent intent = new Intent(mContext, SessionActivity.class);
                        intent.putExtra("sessionId", resultEntity.getId());
                        intent.putExtra("sessionType", SessionActivity.SESSION_TYPE_GROUP);
                        mContext.jumpToActivity(intent);
                        mContext.finish();
                    } else {
                        UIUtils.showToast(UIUtils.getString(R.string.create_group_fail));
                    }
                }, this::loadError);
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        mContext.hideWaitingDialog();
    }
}
