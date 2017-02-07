package com.lqr.wechat.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqr.wechat.AppConst;
import com.lqr.wechat.R;
import com.lqr.wechat.activity.CardPaketActivity;
import com.lqr.wechat.activity.MyInfoActivity;
import com.lqr.wechat.activity.SettingActivity;
import com.lqr.wechat.factory.ThreadPoolFactory;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.model.UserCache;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.UIUtils;
import com.lqr.wechat.view.CustomDialog;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * @创建者 CSDN_LQR
 * @描述 我
 */
public class MeFragment extends BaseFragment {

    private NimUserInfo mNimUserInfo;
    private View mQRCodeCardView;
    private CustomDialog mQRCodeCardDialog;
    private ImageView mIvHeaderQRCodeCard;
    private TextView mTvNameQRCodeCard;
    private ImageView mIvGenderQRCodeCard;
    private ImageView mIvCardQRCodeCard;

    @InjectView(R.id.ivHeader)
    ImageView mIvHeader;
    @InjectView(R.id.tvName)
    TextView mTvName;
    @InjectView(R.id.tvAccount)
    TextView mTvAccount;


    @OnClick({R.id.llMyInfo, R.id.ivQRCordCard, R.id.oivCardPaket, R.id.oivSetting})
    public void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.llMyInfo:
                intent = new Intent(getActivity(), MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.ivQRCordCard:
                if (mQRCodeCardView == null) {
                    mQRCodeCardView = View.inflate(getActivity(), R.layout.include_qrcode_card, null);
                    mQRCodeCardView.setBackgroundResource(R.drawable.shape_corner_rect_solid_white);
                    mIvHeaderQRCodeCard = (ImageView) mQRCodeCardView.findViewById(R.id.ivHeader);
                    mTvNameQRCodeCard = (TextView) mQRCodeCardView.findViewById(R.id.tvName);
                    mIvGenderQRCodeCard = (ImageView) mQRCodeCardView.findViewById(R.id.ivGender);
                    mIvCardQRCodeCard = (ImageView) mQRCodeCardView.findViewById(R.id.ivCard);
                    mQRCodeCardDialog = new CustomDialog(getActivity(), 300, 400, mQRCodeCardView, R.style.dialog);
                }

                String avatar = mNimUserInfo.getAvatar();
                if (!TextUtils.isEmpty(avatar))
                    ImageLoaderManager.LoadNetImage(avatar, mIvHeaderQRCodeCard);
                else
                    mIvHeaderQRCodeCard.setImageResource(R.mipmap.default_header);
                mTvNameQRCodeCard.setText(mNimUserInfo.getName());
                if (mNimUserInfo.getGenderEnum() == GenderEnum.FEMALE) {
                    mIvGenderQRCodeCard.setImageResource(R.mipmap.ic_gender_female);
                } else if (mNimUserInfo.getGenderEnum() == GenderEnum.MALE) {
                    mIvGenderQRCodeCard.setImageResource(R.mipmap.ic_gender_male);
                } else {
                    mIvGenderQRCodeCard.setVisibility(View.GONE);
                }
                Bitmap bitmap = ((BitmapDrawable) mIvHeader.getDrawable()).getBitmap();
                showQRCordCard(bitmap);
                mQRCodeCardDialog.show();
                break;
            case R.id.oivCardPaket:
                intent = new Intent(getActivity(), CardPaketActivity.class);
                startActivity(intent);
                break;
            case R.id.oivSetting:
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_me, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        mNimUserInfo = NimUserInfoSDK.getUser(UserCache.getAccount());
        if (mNimUserInfo == null) {
            getUserInfoFromRemote();
        } else {
            //头像
            if (!TextUtils.isEmpty(mNimUserInfo.getAvatar()) && mIvHeader != null) {
                ImageLoaderManager.LoadNetImage(mNimUserInfo.getAvatar(), mIvHeader);
            }
            //用户名、账号
            if (mTvName != null)
                mTvName.setText(mNimUserInfo.getName());
            if (mTvAccount != null)
                mTvAccount.setText(mNimUserInfo.getAccount());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mQRCodeCardDialog != null)
            mQRCodeCardDialog.dismiss();
    }

    private void getUserInfoFromRemote() {
        List<String> accountList = new ArrayList<>();
        accountList.add(UserCache.getAccount());
        NimUserInfoSDK.getUserInfosFormServer(accountList, new RequestCallback<List<NimUserInfo>>() {
            @Override
            public void onSuccess(List<NimUserInfo> param) {
                initData();
            }

            @Override
            public void onFailed(int code) {
                UIUtils.showToast("获取用户信息失败" + code);
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
            }
        });
    }

    private void showQRCordCard(final Bitmap bitmap) {
        ThreadPoolFactory.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
//                final Bitmap codeWithLogo5 = QRCodeEncoder.syncEncodeQRCode(AppConst.QRCodeCommend.ACCOUNT + mNimUserInfo.getAccount(), UIUtils.dip2Px(200), UIUtils.getColor(R.color.transparent), UIUtils.getColor(R.color.black0), bitmap);
                final Bitmap codeWithLogo5 = QRCodeEncoder.syncEncodeQRCode(AppConst.QRCodeCommend.ACCOUNT + mNimUserInfo.getAccount(), UIUtils.dip2Px(200));
                UIUtils.postTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        mIvCardQRCodeCard.setImageBitmap(codeWithLogo5);
                    }
                });
            }
        });
    }
}
