package com.lqr.wechat.ui.presenter;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lqr.adapter.LQRAdapterForRecyclerView;
import com.lqr.adapter.LQRViewHolderForRecyclerView;
import com.lqr.wechat.R;
import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.db.model.Friend;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.exception.ServerException;
import com.lqr.wechat.model.response.AgreeFriendsResponse;
import com.lqr.wechat.model.response.GetUserInfoByIdResponse;
import com.lqr.wechat.model.response.UserRelationshipResponse;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.ui.view.INewFriendAtView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.NetUtils;
import com.lqr.wechat.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class NewFriendAtPresenter extends BasePresenter<INewFriendAtView> {

    private List<UserRelationshipResponse.ResultEntity> mData = new ArrayList<>();
    private LQRAdapterForRecyclerView<UserRelationshipResponse.ResultEntity> mAdapter;

    public NewFriendAtPresenter(BaseActivity context) {
        super(context);
    }

    public void loadNewFriendData() {
        if (!NetUtils.isNetworkAvailable(mContext)) {
            UIUtils.showToast(UIUtils.getString(R.string.please_check_net));
            return;
        }

        loadData();
        setAdapter();
    }

    private void loadData() {
        ApiRetrofit.getInstance().getAllUserRelationship()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userRelationshipResponse -> {
                    if (userRelationshipResponse.getCode() == 200) {
                        List<UserRelationshipResponse.ResultEntity> result = userRelationshipResponse.getResult();

                        if (result != null && result.size() > 0) {
                            for (int i = 0; i < result.size(); i++) {
                                UserRelationshipResponse.ResultEntity re = result.get(i);
                                if (re.getStatus() == 10) {//是我发起的添加好友请求
                                    result.remove(re);
                                    i--;
                                }
                            }
                        }

                        if (result != null && result.size() > 0) {
                            getView().getLlHasNewFriend().setVisibility(View.VISIBLE);
                            mData.clear();
                            mData.addAll(result);
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChangedWrapper();
                        } else {
                            getView().getLlNoNewFriend().setVisibility(View.VISIBLE);
                        }
                    } else {
                        Observable.error(new ServerException(UIUtils.getString(R.string.load_error)));
                    }
                }, this::loadError);
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new LQRAdapterForRecyclerView<UserRelationshipResponse.ResultEntity>(mContext, mData, R.layout.item_new_friends) {
                @Override
                public void convert(LQRViewHolderForRecyclerView helper, UserRelationshipResponse.ResultEntity item, int position) {

                    ImageView ivHeader = helper.getView(R.id.ivHeader);
                    helper.setText(R.id.tvName, item.getUser().getNickname())
                            .setText(R.id.tvMsg, item.getMessage());

                    if (item.getStatus() == 20) {//已经是好友
                        helper.setViewVisibility(R.id.tvAdded, View.VISIBLE)
                                .setViewVisibility(R.id.tvWait, View.GONE)
                                .setViewVisibility(R.id.btnAck, View.GONE);
                    } else if (item.getStatus() == 11) {//别人发来的添加好友请求
                        helper.setViewVisibility(R.id.tvAdded, View.GONE)
                                .setViewVisibility(R.id.tvWait, View.GONE)
                                .setViewVisibility(R.id.btnAck, View.VISIBLE);
                    } else if (item.getStatus() == 10) {//我发起的添加好友请求
                        helper.setViewVisibility(R.id.tvAdded, View.GONE)
                                .setViewVisibility(R.id.tvWait, View.VISIBLE)
                                .setViewVisibility(R.id.btnAck, View.GONE);
                    }

                    String portraitUri = item.getUser().getPortraitUri();
                    if (TextUtils.isEmpty(portraitUri)) {
                        portraitUri = DBManager.getInstance().getPortraitUri(item.getUser().getNickname(), item.getUser().getId());
                    }
                    Glide.with(mContext).load(portraitUri).centerCrop().into(ivHeader);
                    helper.getView(R.id.btnAck).setOnClickListener(v -> agreeFriends(item.getUser().getId(), helper));

                }
            };
        }
        getView().getRvNewFriend().setAdapter(mAdapter);
    }

    private void agreeFriends(String friendId, LQRViewHolderForRecyclerView helper) {
        if (!NetUtils.isNetworkAvailable(mContext)) {
            UIUtils.showToast(UIUtils.getString(R.string.please_check_net));
            return;
        }
        ApiRetrofit.getInstance().agreeFriends(friendId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<AgreeFriendsResponse, Observable<GetUserInfoByIdResponse>>() {
                    @Override
                    public Observable<GetUserInfoByIdResponse> call(AgreeFriendsResponse agreeFriendsResponse) {
                        if (agreeFriendsResponse != null && agreeFriendsResponse.getCode() == 200) {
                            helper.setViewVisibility(R.id.tvAdded, View.VISIBLE)
                                    .setViewVisibility(R.id.btnAck, View.GONE);
                            return ApiRetrofit.getInstance().getUserInfoById(friendId);
                        }
                        return Observable.error(new ServerException(UIUtils.getString(R.string.agree_friend_fail)));
                    }
                })
                .subscribe(getUserInfoByIdResponse -> {
                    if (getUserInfoByIdResponse != null && getUserInfoByIdResponse.getCode() == 200) {
                        GetUserInfoByIdResponse.ResultEntity result = getUserInfoByIdResponse.getResult();
                        UserInfo userInfo = new UserInfo(UserCache.getId(), result.getNickname(), Uri.parse(result.getPortraitUri()));
                        if (TextUtils.isEmpty(userInfo.getPortraitUri().toString())) {
                            userInfo.setPortraitUri(Uri.parse(DBManager.getInstance().getPortraitUri(userInfo)));
                        }
                        Friend friend = new Friend(userInfo.getUserId(), userInfo.getName(), userInfo.getPortraitUri().toString());
                        DBManager.getInstance().saveOrUpdateFriend(friend);
                        UIUtils.postTaskDelay(() -> {
                            BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_FRIEND);
                            BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                        }, 1000);
                    }
                }, this::loadError);
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

}
