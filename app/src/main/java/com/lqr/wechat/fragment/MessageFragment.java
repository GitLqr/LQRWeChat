package com.lqr.wechat.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ImageView;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.emoji.MoonUtil;
import com.lqr.ninegridimageview.LQRNineGridImageView;
import com.lqr.ninegridimageview.LQRNineGridImageViewAdapter;
import com.lqr.wechat.R;
import com.lqr.wechat.activity.MainActivity;
import com.lqr.wechat.activity.SessionActivity;
import com.lqr.wechat.factory.ThreadPoolFactory;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.model.Contact;
import com.lqr.wechat.nimsdk.NimFriendSDK;
import com.lqr.wechat.nimsdk.NimRecentContactSDK;
import com.lqr.wechat.nimsdk.NimTeamSDK;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.TimeUtils;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @创建者 CSDN_LQR
 * @描述 微信
 */
public class MessageFragment extends BaseFragment {

    private List<RecentContact> mRecentContactList = new ArrayList<>();//最近联系人列表
    private Observer<List<RecentContact>> mMessageObserver;

    private LQRAdapterForRecyclerView<RecentContact> mAdapter;
    private View mHeaderView;
    private LQRNineGridImageViewAdapter<NimUserInfo> mNineGridAdapter;

    @InjectView(R.id.cvMessage)
    RecyclerView mCvMessage;
    MainActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void init() {
        //监听最近联系人
        observeRecentContact();
        //初始化总未读数
        updateTotalUnReadCount();
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_message, null);
        ButterKnife.inject(this, view);

//        mHeaderView = View.inflate(getActivity(), R.layout.header_message_rv, null);
//        mHeaderView.setVisibility(View.GONE);

        mNineGridAdapter = new LQRNineGridImageViewAdapter<NimUserInfo>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, NimUserInfo userInfo) {
                if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                    ImageLoaderManager.LoadNetImage(userInfo.getAvatar(), imageView);
                } else {
                    imageView.setImageResource(R.mipmap.default_header);
                }
            }
        };

        return view;
    }

    @Override
    public void initData() {
        getLocalRecentData();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        setAdapter();
//    }

    private void setAdapter() {
//        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<RecentContact>(getActivity(), R.layout.item_message_rv, mRecentContactList) {
                @Override
                public void convert(final LQRViewHolderForRecyclerView helper, final RecentContact item, int position) {
                    final ImageView ivHeader = helper.getView(R.id.ivHeader);//单聊头像
                    final LQRNineGridImageView ngivHeader = helper.getView(R.id.ngiv);//群聊头像
                    if (item.getSessionType() == SessionTypeEnum.P2P) {
                        ivHeader.setVisibility(View.VISIBLE);
                        ngivHeader.setVisibility(View.GONE);
                        //设置条目的常规信息
                        Friend friend = NimFriendSDK.getFriendByAccount(item.getContactId());
                        NimUserInfo userInfo = NimUserInfoSDK.getUser(item.getContactId());
                        if (userInfo == null) {
                            return;
                        }
                        Contact contact = new Contact(friend, userInfo);

                        //设置单聊的头像
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvatar())) {
                            ImageLoaderManager.LoadNetImage(userInfo.getAvatar(), ivHeader);
                        } else {
                            (ivHeader).setImageResource(R.mipmap.default_header);
                        }

                        helper.setText(R.id.tvName, contact.getDisplayName());
                    } else {
                        ivHeader.setVisibility(View.GONE);
                        ngivHeader.setVisibility(View.VISIBLE);
                        ThreadPoolFactory.getNormalPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                final Team team = NimTeamSDK.queryTeamBlock(item.getContactId());
                                if (team == null)
                                    return;
                                UIUtils.postTaskSafely(new Runnable() {
                                    @Override
                                    public void run() {
                                        //如果我在群中，则显示群名，若不在，则除去该消息
                                        if (team.isMyTeam()) {
                                            if (team != null)
                                                helper.setText(R.id.tvName, TextUtils.isEmpty(team.getName()) ? "群聊(" + team.getMemberCount() + ")" : team.getName());
                                        } else {
                                            NimRecentContactSDK.deleteRecentContact(item);
                                            mAdapter.removeItem(item);
                                        }

                                        //设置群聊的头像
                                        NimTeamSDK.queryMemberList(team.getId(), new RequestCallback<List<TeamMember>>() {
                                            @Override
                                            public void onSuccess(List<TeamMember> memberList) {
                                                if (memberList != null && memberList.size() > 0) {
                                                    List<String> accounts = new ArrayList<>();
                                                    int count = memberList.size() > 9 ? 9 : memberList.size();
                                                    for (int i = 0; i < count; i++) {
                                                        accounts.add(memberList.get(i).getAccount());
                                                    }
                                                    NimUserInfoSDK.getUserInfosFormServer(accounts, new RequestCallback<List<NimUserInfo>>() {
                                                        @Override
                                                        public void onSuccess(List<NimUserInfo> result) {
                                                            ngivHeader.setAdapter(mNineGridAdapter);
                                                            ngivHeader.setImagesData(result);
                                                        }

                                                        @Override
                                                        public void onFailed(int code) {

                                                        }

                                                        @Override
                                                        public void onException(Throwable exception) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onFailed(int code) {

                                            }

                                            @Override
                                            public void onException(Throwable exception) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    helper.setText(R.id.tvMsg, item.getContent())
                            .setText(R.id.tvTime, TimeUtils.getMsgFormatTime(item.getTime()));
//                    MoonUtil.identifyFaceExpression(getActivity(), helper.getView(R.id.tvMsg), item.getContent(), ImageSpan.ALIGN_BOTTOM);
                    MoonUtil.identifyFaceExpressionAndTags(getActivity(), helper.getView(R.id.tvMsg), item.getContent(), ImageSpan.ALIGN_BOTTOM, 0.45f);

                    //判断是否有未读消息
                    helper.setViewVisibility(R.id.tvUnread, item.getUnreadCount() > 0 ? View.VISIBLE : View.GONE).setText(R.id.tvUnread, String.valueOf(item.getUnreadCount()));

                    //条目点击跳转至聊天界面
                    helper.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), SessionActivity.class);
                            intent.putExtra(SessionActivity.SESSION_ACCOUNT, item.getContactId());
                            intent.putExtra(SessionActivity.SESSION_TYPE, item.getSessionType());
                            startActivity(intent);
                            //清空当前联系人的未读数
                            NimRecentContactSDK.clearUnreadCount(item.getContactId(), item.getSessionType());
                        }
                    });
                }
            };
//            mAdapter.addHeaderView(mHeaderView);
//            mCvMessage.setAdapter(mAdapter.getHeaderAndFooterAdapter());
            mCvMessage.setAdapter(mAdapter);
//        } else {
//            mAdapter.notifyDataSetChanged();
//        }
    }

    /**
     * 加载本地联系人信息
     */
    private void getLocalRecentData() {
        //获取最近联系人
        NimRecentContactSDK.queryRecentContacts(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> result, Throwable exception) {
                if (code != ResponseCode.RES_SUCCESS || exception != null)
                    return;

                //如果我已经不是最近聊天中的群聊的成员，则不显示这个最近群聊项
                for (int i = 0; i < result.size(); i++) {
                    RecentContact rc = result.get(i);
                    if (rc.getSessionType() == SessionTypeEnum.Team) {
                        if (!NimTeamSDK.queryTeamBlock(rc.getContactId()).isMyTeam()) {
                            result.remove(i);
                            NimRecentContactSDK.deleteRecentContact(rc);
                            i--;
                        }
                    }
                }

                mRecentContactList.clear();
                mRecentContactList.addAll(result);
                setAdapter();

                updateRecentContactInfoFromServer();

            }
        });
    }

    /**
     * 更新最近联系人的本地信息
     */
    private void updateRecentContactInfoFromServer() {
        if (mRecentContactList != null && mRecentContactList.size() > 0) {
            List<String> accounts = new ArrayList<>();
            for (RecentContact rc : mRecentContactList) {
                accounts.add(rc.getFromAccount());
            }
            if (accounts != null && accounts.size() > 0) {
                NimUserInfoSDK.getUserInfosFormServer(accounts, new RequestCallback<List<NimUserInfo>>() {
                    @Override
                    public void onSuccess(List<NimUserInfo> param) {
                        setAdapter();
                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
        }
    }

    /**
     * 监听最近联系人
     */
    private void observeRecentContact() {
        mMessageObserver = new Observer<List<RecentContact>>() {
            @Override
            public void onEvent(List<RecentContact> recentContacts) {

                //遍历最近更新的联系人，如果在数据集合中有的话，去掉数据集合中原来的最近联系人，再最新的最近联系人添加到数据集合中
                if (recentContacts != null && recentContacts.size() > 0) {
                    if (mAdapter != null) {
                        int index;
                        for (RecentContact r : recentContacts) {
                            index = -1;
                            for (int i = 0; i < mAdapter.getData().size(); i++) {
                                if (r.getContactId().equals(mAdapter.getData().get(i).getContactId())
                                        && r.getSessionType() == (mAdapter.getData().get(i).getSessionType())) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index >= 0) {
                                mAdapter.removeItem(index);
                            }
                            mAdapter.addFirstItem(r);
                        }
                        updateTotalUnReadCount();
                    }
                }
            }
        };

        NimRecentContactSDK.observeRecentContact(mMessageObserver, true);
    }

    /**
     * 更新最近联系人中消息未读总数
     */
    private void updateTotalUnReadCount() {
        int totalUnreadCount = NimRecentContactSDK.getTotalUnreadCount();
        if (activity.mTvMessageCount != null)
            if (totalUnreadCount > 0) {
                activity.mTvMessageCount.setVisibility(View.VISIBLE);
                activity.mTvMessageCount.setText(String.valueOf(totalUnreadCount > 99 ? 99 : totalUnreadCount));
            } else {
                activity.mTvMessageCount.setVisibility(View.GONE);
            }
    }
}
