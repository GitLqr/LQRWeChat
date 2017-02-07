package com.lqr.wechat.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.activity.AllTagActvitiy;
import com.lqr.wechat.activity.MainActivity;
import com.lqr.wechat.activity.NewFriendActivity;
import com.lqr.wechat.activity.TeamCheatListActivity;
import com.lqr.wechat.activity.UserInfoActivity;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.model.Contact;
import com.lqr.wechat.nimsdk.NimFriendSDK;
import com.lqr.wechat.nimsdk.NimRecentContactSDK;
import com.lqr.wechat.nimsdk.NimSystemSDK;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.SortUtils;
import com.lqr.wechat.utils.StringUtils;
import com.lqr.wechat.utils.UIUtils;
import com.lqr.wechat.view.QuickIndexBar;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.netease.nimlib.sdk.msg.constant.SystemMessageType.TeamInvite;

/**
 * @创建者 CSDN_LQR
 * @描述 通讯录
 */
public class ContactsFragment extends BaseFragment {

    private List<Contact> mContacts = new ArrayList<>();
    private LQRAdapterForRecyclerView<Contact> mAdapter;
    private int i;
    private List<Friend> mFriends = new ArrayList<>();

    @InjectView(R.id.rvContacts)
    LQRRecyclerView mRvContacts;
    @InjectView(R.id.quickIndexBar)
    QuickIndexBar mQuickIndexBar;
    @InjectView(R.id.tvLetter)
    TextView mTvLetter;

    //列表首尾布局
    View mHeaderView;
    TextView mFooterTv;

    //联系人列表最上条目
    LinearLayout mLlNewFriend;
    LinearLayout mLlGroupCheat;
    LinearLayout mLlTag;
    LinearLayout mLlOffical;
    private View mVNewFriendUnread;
    private View mVGroupCheatUnread;


    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_contacts, null);
        ButterKnife.inject(this, view);
        initHeaderViewAndFooterView();
        return view;
    }

    @Override
    public void initData() {

        try {
            mFriends.clear();
            mContacts.clear();

            //得到好友列表
            List<Friend> friends = NimFriendSDK.getFriends();
            if (!StringUtils.isEmpty(friends)) {
                mFriends.addAll(friends);

                //得到本地没有信息的账号
                List<String> accountList = new ArrayList<>();
                for (int i = 0; i < mFriends.size(); i++) {
                    String account = mFriends.get(i).getAccount();
                    if (NimUserInfoSDK.getUser(account) == null) {
                        accountList.add(account);
                    }
                }

                //从服务器上获取用户信息
                if (!StringUtils.isEmpty(accountList)) {
                    NimUserInfoSDK.getUserInfosFormServer(accountList, new RequestCallback<List<NimUserInfo>>() {
                        @Override
                        public void onSuccess(List<NimUserInfo> param) {
                            setDataAndUpdateView();
                        }

                        @Override
                        public void onFailed(int code) {
                            UIUtils.showToast("获取联系人信息失败" + code);
                        }

                        @Override
                        public void onException(Throwable exception) {
                            exception.printStackTrace();
                        }
                    });
                } else {
                    setDataAndUpdateView();
                }
            } else {
                setDataAndUpdateView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            initData();
        }
    }

    @Override
    public void initListener() {
        mQuickIndexBar.setListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                //显示字母提示
                showLetter(letter);

                //滑动对对应字母条目处
                if ("↑".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else if ("☆".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else {
                    //找出第一个对应字母的位置后，滑动到指定位置
                    for (i = 0; i < mContacts.size(); i++) {
                        Contact contact = mContacts.get(i);
                        String c = contact.getPinyin().charAt(0) + "";
                        if (c.equalsIgnoreCase(letter)) {
                            mRvContacts.moveToPosition(i);
                            break;
                        }
                    }
                }
            }
        });

        mLlNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), NewFriendActivity.class), MainActivity.REQ_CLEAR_UNREAD);
            }
        });

        mLlGroupCheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), TeamCheatListActivity.class));
            }
        });

        mLlTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), AllTagActvitiy.class));
            }
        });
        mLlOffical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.showToast("公众号");
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        updateHeaderViewUnreadCount();
//        ((MainActivity) getActivity()).updateContactCount();
    }

    private void initHeaderViewAndFooterView() {
        mHeaderView = View.inflate(getActivity(), R.layout.header_contacts_rv, null);

        mLlNewFriend = (LinearLayout) mHeaderView.findViewById(R.id.llNewFriend);
        mLlGroupCheat = (LinearLayout) mHeaderView.findViewById(R.id.llGroupCheat);
        mLlTag = (LinearLayout) mHeaderView.findViewById(R.id.llTag);
        mLlOffical = (LinearLayout) mHeaderView.findViewById(R.id.llOffical);

        mVNewFriendUnread = mHeaderView.findViewById(R.id.vNewFriendUnread);
        mVGroupCheatUnread = mHeaderView.findViewById(R.id.vGroupCheatUnread);

        mFooterTv = new TextView(getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2Px(50));
        mFooterTv.setLayoutParams(params);
        mFooterTv.setGravity(Gravity.CENTER);
    }

    /**
     * 更新列表头部中未读消息提示
     */
    public void updateHeaderViewUnreadCount() {
        List<SystemMessageType> types = new ArrayList<>(1);
        types.add(SystemMessageType.AddFriend);
        int unreadCountAddFriend = NimSystemSDK.querySystemMessageUnreadCountByType(types);
        mVNewFriendUnread.setVisibility(unreadCountAddFriend > 0 ? View.VISIBLE : View.GONE);

        types.clear();
        types.add(TeamInvite);
        int unreadCountTeamInvite = NimSystemSDK.querySystemMessageUnreadCountByType(types);
        mVGroupCheatUnread.setVisibility(unreadCountTeamInvite > 0 ? View.VISIBLE : View.GONE);
    }

    private void setDataAndUpdateView() {
        if (mFriends != null) {
            for (int i = 0; i < mFriends.size(); i++) {
                Friend friend = mFriends.get(i);
                NimUserInfo userInfo = NimUserInfoSDK.getUser(friend.getAccount());
                mContacts.add(new Contact(friend, userInfo));
            }

            //将自己也加入到联系人中
//            mContacts.add(new Contact(null, NimUserInfoSDK.getUser(UserCache.getAccount())));
            //整理排序
            SortUtils.sortContacts(mContacts);

            if (mFooterTv != null) {
                mFooterTv.setVisibility(View.VISIBLE);
                mFooterTv.setText(mContacts.size() + "位联系人");
            }
        } else {
            mFooterTv.setVisibility(View.GONE);
        }
        setAdapter();
    }


    private void setAdapter() {
        mAdapter = new LQRAdapterForRecyclerView<Contact>(getActivity(), R.layout.item_contact_cv, mContacts) {
            @Override
            public void convert(LQRViewHolderForRecyclerView helper, final Contact item, int position) {
                helper.setText(R.id.tvName, TextUtils.isEmpty(item.getAlias()) ? item.getName() : item.getAlias());
                if (!TextUtils.isEmpty(item.getAvatar())) {
                    ImageLoaderManager.LoadNetImage(item.getAvatar(), (ImageView) helper.getView(R.id.ivHeader));
                } else {
                    helper.setImageResource(R.id.ivHeader, R.mipmap.default_header);
                }

                String str = "";
                //得到当前字母
                String currentLetter = item.getPinyin().charAt(0) + "";

                if (position == 0) {
                    str = currentLetter;
                } else {
                    //得到上一个字母
                    String preLetter = mContacts.get(position - 1).getPinyin().charAt(0) + "";
                    //如果和上一个字母的首字母不同则显示字母栏
                    if (!preLetter.equalsIgnoreCase(currentLetter)) {
                        str = currentLetter;
                    }

                    int nextIndex = position + 1;
                    if (nextIndex < mContacts.size() - 1) {
                        //得到下一个字母
                        String nextLetter = mContacts.get(nextIndex).getPinyin().charAt(0) + "";
                        //如果和下一个字母的首字母不同则隐藏下划线
                        if (!nextLetter.equalsIgnoreCase(currentLetter)) {
                            helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
                        } else {
                            helper.setViewVisibility(R.id.vLine, View.VISIBLE);
                        }
                    } else {
                        helper.setViewVisibility(R.id.vLine, View.INVISIBLE);
                    }
                }
                if (position == mContacts.size() - 1) {
                    helper.setViewVisibility(R.id.vLine, View.GONE);
                }


                //根据str是否为空决定字母栏是否显示
                if (TextUtils.isEmpty(str)) {
                    helper.setViewVisibility(R.id.tvIndex, View.GONE);
                } else {
                    helper.setViewVisibility(R.id.tvIndex, View.VISIBLE);
                    helper.setText(R.id.tvIndex, currentLetter);
                }

                //条目点击跳转好友信息查看界面
                helper.getView(R.id.root).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                        intent.putExtra("account", item.getAccount());
                        startActivity(intent);
                        //清空该好友的消息未读数
                        NimRecentContactSDK.clearUnreadCount(item.getAccount(), SessionTypeEnum.P2P);
                    }
                });

            }
        };
        //加入头部
        mAdapter.addHeaderView(mHeaderView);
        //加入脚部
        mAdapter.addFooterView(mFooterTv);
        //设置适配器
        if (mRvContacts != null)
            mRvContacts.setAdapter(mAdapter.getHeaderAndFooterAdapter());
    }

    /**
     * 显示所触摸到的字母
     *
     * @param letter
     */
    protected void showLetter(String letter) {
        mTvLetter.setVisibility(View.VISIBLE);// 设置为可见
        mTvLetter.setText(letter);

        UIUtils.getMainThreadHandler().removeCallbacksAndMessages(null);
        UIUtils.postTaskDelay(new Runnable() {
            @Override
            public void run() {
                mTvLetter.setVisibility(View.GONE);
            }
        }, 500);
    }

    /**
     * 是否显示快速导航条
     *
     * @param show
     */
    public void showQuickIndexBar(boolean show) {
        mQuickIndexBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mQuickIndexBar.invalidate();
    }
}
