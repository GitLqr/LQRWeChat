package com.lqr.wechat.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqr.wechat.AppConst;
import com.lqr.wechat.R;
import com.lqr.wechat.factory.ThreadPoolFactory;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.model.UserCache;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.UIUtils;
import com.lqr.wechat.view.CustomDialog;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * @创建者 CSDN_LQR
 * @描述 添加朋友界面
 */
public class AddFriendActivity extends BaseActivity {

    private Intent mIntent;

    private NimUserInfo mNimUserInfo;
    private View mQRCodeCardView;
    private CustomDialog mQRCodeCardDialog;
    private ImageView mIvHeaderQRCodeCard;
    private TextView mTvNameQRCodeCard;
    private ImageView mIvGenderQRCodeCard;
    private ImageView mIvCardQRCodeCard;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @OnClick({R.id.etContent, R.id.ivQRCordCard})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.etContent:
                mIntent = new Intent(this, SearchUserActivity.class);
                mIntent.putExtra(SearchUserActivity.SEARCH_TYPE, SearchUserActivity.SEARCH_USER_REMOTE);
                startActivity(mIntent);
                break;
            case R.id.ivQRCordCard:
                if (mQRCodeCardView == null) {
                    mQRCodeCardView = View.inflate(AddFriendActivity.this, R.layout.include_qrcode_card, null);
                    mQRCodeCardView.setBackgroundResource(R.drawable.shape_corner_rect_solid_white);
                    mIvHeaderQRCodeCard = (ImageView) mQRCodeCardView.findViewById(R.id.ivHeader);
                    mTvNameQRCodeCard = (TextView) mQRCodeCardView.findViewById(R.id.tvName);
                    mIvGenderQRCodeCard = (ImageView) mQRCodeCardView.findViewById(R.id.ivGender);
                    mIvCardQRCodeCard = (ImageView) mQRCodeCardView.findViewById(R.id.ivCard);
                    mQRCodeCardDialog = new CustomDialog(AddFriendActivity.this, 300, 400, mQRCodeCardView, R.style.dialog);
                }

                String avatar = mNimUserInfo.getAvatar();
                if (TextUtils.isEmpty(avatar)) {
                    mIvHeaderQRCodeCard.setImageResource(R.mipmap.default_header);
                } else {
                    ImageLoaderManager.LoadNetImage(avatar, mIvHeaderQRCodeCard);
                }
                mTvNameQRCodeCard.setText(mNimUserInfo.getName());
                if (mNimUserInfo.getGenderEnum() == GenderEnum.FEMALE) {
                    mIvGenderQRCodeCard.setImageResource(R.mipmap.ic_gender_female);
                } else if (mNimUserInfo.getGenderEnum() == GenderEnum.MALE) {
                    mIvGenderQRCodeCard.setImageResource(R.mipmap.ic_gender_male);
                } else {
                    mIvGenderQRCodeCard.setVisibility(View.GONE);
                }

                Bitmap bitmap = ((BitmapDrawable) mIvHeaderQRCodeCard.getDrawable()).getBitmap();
                showQRCordCard(bitmap);

//                ThreadPoolFactory.getNormalPool().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        OkHttpUtils.get().url(mNimUserInfo.getAvatar()).build().execute(new BitmapCallback() {
//                            @Override
//                            public void onError(Call call, Exception e, int id) {
//                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.default_header);
//                                showQRCordCard(bitmap);
//                            }
//
//                            @Override
//                            public void onResponse(Bitmap bitmap, int id) {
//                                showQRCordCard(bitmap);
//                            }
//                        });
//
//                    }
//                });

                mQRCodeCardDialog.show();
                break;
        }
    }

    @Override
    public void init() {
        mNimUserInfo = NimUserInfoSDK.getUser(UserCache.getAccount());
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_add_friend);
        ButterKnife.inject(this);

        initToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("添加朋友");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
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
