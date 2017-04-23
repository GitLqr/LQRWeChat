package com.lqr.wechat.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRHeaderAndFooterAdapter;
import com.lqr.recyclerview.LQRRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.db.model.Friend;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.presenter.CreateGroupAtPresenter;
import com.lqr.wechat.ui.view.ICreateGroupAtView;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.widget.QuickIndexBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @创建者 CSDN_LQR
 * @描述 发起群聊
 */
public class CreateGroupActivity extends BaseActivity<ICreateGroupAtView, CreateGroupAtPresenter> implements ICreateGroupAtView {

    public ArrayList<String> mSelectedTeamMemberAccounts;

    @Bind(R.id.btnToolbarSend)
    Button mBtnToolbarSend;

    @Bind(R.id.rvSelectedContacts)
    LQRRecyclerView mRvSelectedContacts;
    @Bind(R.id.etKey)
    EditText mEtKey;

    private View mHeaderView;
    @Bind(R.id.rvContacts)
    LQRRecyclerView mRvContacts;
    @Bind(R.id.qib)
    QuickIndexBar mQib;
    @Bind(R.id.tvLetter)
    TextView mTvLetter;

    @Override
    public void init() {
        mSelectedTeamMemberAccounts = getIntent().getStringArrayListExtra("selectedMember");
    }

    @Override
    public void initView() {
        mBtnToolbarSend.setVisibility(View.VISIBLE);
        mBtnToolbarSend.setText(UIUtils.getString(R.string.sure));
        mBtnToolbarSend.setEnabled(false);
        mHeaderView = View.inflate(this, R.layout.header_group_cheat, null);
    }

    @Override
    public void initData() {
        mPresenter.loadContacts();
    }

    @Override
    public void initListener() {
        mBtnToolbarSend.setOnClickListener(v -> {
            if (mSelectedTeamMemberAccounts == null) {
                mPresenter.createGroup();
            } else {
                //添加群成员
                mPresenter.addGroupMembers();
            }
        });
        mHeaderView.findViewById(R.id.tvSelectOneGroup).setOnClickListener(v -> UIUtils.showToast("选择一个群"));
        mQib.setOnLetterUpdateListener(new QuickIndexBar.OnLetterUpdateListener() {
            @Override
            public void onLetterUpdate(String letter) {
                //显示对话框
                showLetter(letter);
                //滑动到第一个对应字母开头的联系人
                if ("↑".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else if ("☆".equalsIgnoreCase(letter)) {
                    mRvContacts.moveToPosition(0);
                } else {
                    List<Friend> data = ((LQRAdapterForRecyclerView) ((LQRHeaderAndFooterAdapter) mRvContacts.getAdapter()).getInnerAdapter()).getData();
                    for (int i = 0; i < data.size(); i++) {
                        Friend friend = data.get(i);
                        String c = friend.getDisplayNameSpelling().charAt(0) + "";
                        if (c.equalsIgnoreCase(letter)) {
                            mRvContacts.moveToPosition(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onLetterCancel() {
                //隐藏对话框
                hideLetter();
            }
        });
    }

    private void showLetter(String letter) {
        mTvLetter.setVisibility(View.VISIBLE);
        mTvLetter.setText(letter);
    }

    private void hideLetter() {
        mTvLetter.setVisibility(View.GONE);
    }

    @Override
    protected CreateGroupAtPresenter createPresenter() {
        return new CreateGroupAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_create_group;
    }

    @Override
    public Button getBtnToolbarSend() {
        return mBtnToolbarSend;
    }

    @Override
    public LQRRecyclerView getRvContacts() {
        return mRvContacts;
    }

    @Override
    public LQRRecyclerView getRvSelectedContacts() {
        return mRvSelectedContacts;
    }

    @Override
    public EditText getEtKey() {
        return mEtKey;
    }

    @Override
    public View getHeaderView() {
        return mHeaderView;
    }
}
