package com.lqr.wechat.ui.presenter;

import com.lqr.wechat.R;
import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.ui.view.IPostScriptAtView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.UIUtils;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class PostScriptAtPresenter extends BasePresenter<IPostScriptAtView> {

    public PostScriptAtPresenter(BaseActivity context) {
        super(context);
    }

    public void addFriend(String userId) {
        String msg = getView().getEtMsg().getText().toString().trim();
        ApiRetrofit.getInstance().sendFriendInvitation(userId, msg)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(friendInvitationResponse -> {
                    if (friendInvitationResponse.getCode() == 200) {
                        UIUtils.showToast(UIUtils.getString(R.string.rquest_sent_success));
                        mContext.finish();
                    } else {
                        UIUtils.showToast(UIUtils.getString(R.string.rquest_sent_fail));
                    }
                }, this::loadError);
    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }
}
