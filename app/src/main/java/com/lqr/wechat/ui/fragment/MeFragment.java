package com.lqr.wechat.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lqr.optionitemview.OptionItemView;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.ui.activity.MainActivity;
import com.lqr.wechat.ui.activity.MyInfoActivity;
import com.lqr.wechat.ui.activity.SettingActivity;
import com.lqr.wechat.ui.base.BaseFragment;
import com.lqr.wechat.ui.presenter.MeFgPresenter;
import com.lqr.wechat.ui.view.IMeFgView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.widget.CustomDialog;

import butterknife.Bind;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @创建者 CSDN_LQR
 * @描述 我界面
 */
public class MeFragment extends BaseFragment<IMeFgView, MeFgPresenter> implements IMeFgView {

    private CustomDialog mQrCardDialog;

    @Bind(R.id.llMyInfo)
    LinearLayout mLlMyInfo;
    @Bind(R.id.ivHeader)
    ImageView mIvHeader;
    @Bind(R.id.tvName)
    TextView mTvName;
    @Bind(R.id.tvAccount)
    TextView mTvAccount;
    @Bind(R.id.ivQRCordCard)
    ImageView mIvQRCordCard;

    @Bind(R.id.oivAlbum)
    OptionItemView mOivAlbum;
    @Bind(R.id.oivCollection)
    OptionItemView mOivCollection;
    @Bind(R.id.oivWallet)
    OptionItemView mOivWallet;
    @Bind(R.id.oivCardPaket)
    OptionItemView mOivCardPaket;

    @Bind(R.id.oivSetting)
    OptionItemView mOivSetting;

    @Override
    public void init() {
        registerBR();
    }

    @Override
    public void initData() {
        mPresenter.loadUserInfo();
    }

    @Override
    public void initView(View rootView) {
        mIvQRCordCard.setOnClickListener(v -> showQRCard());
        mOivAlbum.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.MY_JIAN_SHU));
        mOivCollection.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.MY_CSDN));
        mOivWallet.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.MY_OSCHINA));
        mOivCardPaket.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToWebViewActivity(AppConst.WeChatUrl.MY_GITHUB));
    }

    @Override
    public void initListener() {
        mLlMyInfo.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(MyInfoActivity.class));
        mOivSetting.setOnClickListener(v -> ((MainActivity) getActivity()).jumpToActivityAndClearTop(SettingActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBR();
    }

    private void showQRCard() {
        if (mQrCardDialog == null) {
            View qrCardView = View.inflate(getActivity(), R.layout.include_qrcode_card, null);
            ImageView ivHeader = (ImageView) qrCardView.findViewById(R.id.ivHeader);
            TextView tvName = (TextView) qrCardView.findViewById(R.id.tvName);
            ImageView ivCard = (ImageView) qrCardView.findViewById(R.id.ivCard);
            TextView tvTip = (TextView) qrCardView.findViewById(R.id.tvTip);
            tvTip.setText(UIUtils.getString(R.string.qr_code_card_tip));

            UserInfo userInfo = mPresenter.getUserInfo();
            if (userInfo != null) {
                Glide.with(getActivity()).load(userInfo.getPortraitUri()).centerCrop().into(ivHeader);
                tvName.setText(userInfo.getName());
                Observable.just(QRCodeEncoder.syncEncodeQRCode(AppConst.QrCodeCommon.ADD + userInfo.getUserId(), UIUtils.dip2Px(100)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(bitmap -> ivCard.setImageBitmap(bitmap), this::loadQRCardError);
            }

            mQrCardDialog = new CustomDialog(getActivity(), 300, 400, qrCardView, R.style.MyDialog);
        }
        mQrCardDialog.show();
    }

    private void loadQRCardError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    private void registerBR() {
        BroadcastManager.getInstance(getActivity()).register(AppConst.CHANGE_INFO_FOR_ME, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.loadUserInfo();
            }
        });
    }

    private void unregisterBR() {
        BroadcastManager.getInstance(getActivity()).unregister(AppConst.CHANGE_INFO_FOR_ME);
    }

    @Override
    protected MeFgPresenter createPresenter() {
        return new MeFgPresenter((MainActivity) getActivity());
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_me;
    }

    @Override
    public ImageView getIvHeader() {
        return mIvHeader;
    }

    @Override
    public TextView getTvName() {
        return mTvName;
    }

    @Override
    public TextView getTvAccount() {
        return mTvAccount;
    }
}
