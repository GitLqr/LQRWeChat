package com.lqr.wechat.ui.presenter;

import android.net.Uri;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.lqr.wechat.R;
import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.db.model.Friend;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.response.GetUserInfoByIdResponse;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.ui.view.IMeFgView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.UIUtils;

import io.rong.imlib.model.UserInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MeFgPresenter extends BasePresenter<IMeFgView> {

    private UserInfo mUserInfo;
    private boolean isFirst = true;

    public MeFgPresenter(BaseActivity context) {
        super(context);
    }

    public void loadUserInfo() {
        mUserInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        if (mUserInfo == null || isFirst) {
            isFirst = false;
            ApiRetrofit.getInstance().getUserInfoById(UserCache.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getUserInfoByIdResponse -> {
                        if (getUserInfoByIdResponse != null && getUserInfoByIdResponse.getCode() == 200) {
                            GetUserInfoByIdResponse.ResultEntity result = getUserInfoByIdResponse.getResult();

                            mUserInfo = new UserInfo(UserCache.getId(), result.getNickname(), Uri.parse(result.getPortraitUri()));
                            if (TextUtils.isEmpty(mUserInfo.getPortraitUri().toString())) {
                                mUserInfo.setPortraitUri(Uri.parse(DBManager.getInstance().getPortraitUri(mUserInfo)));
                            }

                            DBManager.getInstance().saveOrUpdateFriend(new Friend(mUserInfo.getUserId(), mUserInfo.getName(), mUserInfo.getPortraitUri().toString()));
                            fillView();
                        }
                    }, this::loadError);
        } else {
            fillView();
        }
    }

    public void refreshUserInfo() {
        UserInfo userInfo = DBManager.getInstance().getUserInfo(UserCache.getId());
        if (userInfo == null) {
            loadUserInfo();
        } else {
            mUserInfo = userInfo;
        }
    }

    public void fillView() {
        if (mUserInfo != null) {
            Glide.with(mContext).load(mUserInfo.getPortraitUri()).centerCrop().into(getView().getIvHeader());
            getView().getTvAccount().setText(UIUtils.getString(R.string.my_chat_account, mUserInfo.getUserId()));
            getView().getTvName().setText(mUserInfo.getName());
        }
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}
