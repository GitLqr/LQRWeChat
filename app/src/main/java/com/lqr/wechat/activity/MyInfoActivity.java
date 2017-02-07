package com.lqr.wechat.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.optionitemview.OptionItemView;
import com.lqr.wechat.R;
import com.lqr.wechat.imageloader.ImageLoaderManager;
import com.lqr.wechat.model.UserCache;
import com.lqr.wechat.nimsdk.NimUserInfoSDK;
import com.lqr.wechat.utils.UIUtils;
import com.lqr.wechat.view.CustomDialog;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.lqr.wechat.activity.SessionActivity.IMAGE_PICKER;

/**
 * @创建者 CSDN_LQR
 * @描述 个人信息
 */
public class MyInfoActivity extends BaseActivity {

    Intent mIntent;
    private NimUserInfo mNimUserInfo;

    private View mGenderDialogView;
    private CustomDialog mDialog;
    private TextView mTvMale;
    private TextView mTvFemale;
    private Drawable mSelectedDrawable;
    private Drawable mUnSelectedDrawable;

    Observer<List<NimUserInfo>> userInfoUpdateObserver = new Observer<List<NimUserInfo>>() {
        @Override
        public void onEvent(List<NimUserInfo> nimUserInfos) {
            initData();
        }
    };

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.llHeader)
    LinearLayout mLlHeader;
    @InjectView(R.id.ivHeader)
    ImageView mIvHeader;
    @InjectView(R.id.oivName)
    OptionItemView mOivName;
    @InjectView(R.id.oivQRCordCard)
    OptionItemView mOivQRCordCard;
    @InjectView(R.id.oivAccount)
    OptionItemView mOivAccount;
    @InjectView(R.id.oivGender)
    OptionItemView mOivGender;
    @InjectView(R.id.oivSignature)
    OptionItemView mOivSignature;

    @OnClick({R.id.llHeader, R.id.ivHeader, R.id.oivName, R.id.oivQRCordCard, R.id.oivGender, R.id.oivSignature})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.llHeader:
                mIntent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(mIntent, IMAGE_PICKER);
                break;
            case R.id.ivHeader:
                if (mNimUserInfo == null)
                    return;
                mIntent = new Intent(this, ShowBigImageActivity.class);
                mIntent.putExtra("url", mNimUserInfo.getAvatar());
                startActivity(mIntent);
                break;
            case R.id.oivName:
                mIntent = new Intent(this, ChangeNameActivity.class);
                mIntent.putExtra("name", mNimUserInfo.getName());
                startActivity(mIntent);
                break;
            case R.id.oivQRCordCard:
                mIntent = new Intent(this, QRCodeCardActivity.class);
                mIntent.putExtra(QRCodeCardActivity.QRCODE_USER, mNimUserInfo);
                startActivity(mIntent);
                break;
            case R.id.oivGender:
                if (mGenderDialogView == null) {
                    mGenderDialogView = View.inflate(this, R.layout.dialog_gender, null);
                    mTvMale = (TextView) mGenderDialogView.findViewById(R.id.tvMale);
                    mTvFemale = (TextView) mGenderDialogView.findViewById(R.id.tvFemale);
                    mDialog = new CustomDialog(this, mGenderDialogView, R.style.dialog);
                    mTvMale.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateGender(GenderEnum.MALE);
                        }
                    });
                    mTvFemale.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateGender(GenderEnum.FEMALE);
                        }
                    });
                }
                updateGenderView(mNimUserInfo.getGenderEnum());
                mDialog.show();
                break;
            case R.id.oivSignature:
                mIntent = new Intent(this, ChangeSignatureActivity.class);
                mIntent.putExtra("signature", mNimUserInfo.getSignature());
                startActivity(mIntent);
                break;
//            case R.id.llHeader:
//                break;
        }
    }

    @Override
    public void init() {
        // 监听用户信息更新
        NimUserInfoSDK.observeUserInfoUpdate(userInfoUpdateObserver, true);

        mSelectedDrawable = UIUtils.getResource().getDrawable(R.mipmap.list_selected);
        mUnSelectedDrawable = UIUtils.getResource().getDrawable(R.mipmap.list_unselected);
        mSelectedDrawable.setBounds(0, 0, mSelectedDrawable.getMinimumWidth(), mSelectedDrawable.getMinimumHeight());
        mUnSelectedDrawable.setBounds(0, 0, mUnSelectedDrawable.getMinimumWidth(), mUnSelectedDrawable.getMinimumHeight());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁用户信息更新监听
        NimUserInfoSDK.observeUserInfoUpdate(userInfoUpdateObserver, false);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_my_info);
        ButterKnife.inject(this);
        initToolbar();
    }

    @Override
    public void initData() {
        mNimUserInfo = NimUserInfoSDK.getUser(UserCache.getAccount());
        if (mNimUserInfo == null) {
            getUserInfoFromRemote();
        } else {
            //头像
            if (!TextUtils.isEmpty(mNimUserInfo.getAvatar())) {
                ImageLoaderManager.LoadNetImage(mNimUserInfo.getAvatar(), mIvHeader);
            }
            //用户名、账号、签名、性别
            mOivName.setRightText(mNimUserInfo.getName());
            mOivAccount.setRightText(mNimUserInfo.getAccount());
            mOivSignature.setRightText(TextUtils.isEmpty(mNimUserInfo.getSignature()) ? "未填写" : mNimUserInfo.getSignature());
            mOivGender.setRightText(mNimUserInfo.getGenderEnum() == GenderEnum.FEMALE ? "女" : mNimUserInfo.getGenderEnum() == GenderEnum.MALE ? "男" : "");
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                //是否发送原图
//                boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                showWaitingDialog("上传头像...");
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    //取第一张照片
                    File file = new File(images.get(0).path);
                    NimUserInfoSDK.uploadFile(file, "image/jpeg", new RequestCallbackWrapper<String>() {
                        @Override
                        public void onResult(int code, String url, Throwable exception) {

                            if (code == ResponseCode.RES_SUCCESS
                                    && !TextUtils.isEmpty(url)) {// 上传成功得到Url
                                Map<UserInfoFieldEnum, Object> fields = new HashMap<UserInfoFieldEnum, Object>(
                                        1);
                                fields.put(UserInfoFieldEnum.AVATAR, url);
                            }

                            Map<UserInfoFieldEnum, Object> fields = new HashMap(1);
                            fields.put(UserInfoFieldEnum.AVATAR, url);
                            NimUserInfoSDK.updateUserInfo(fields, new RequestCallbackWrapper<Void>() {
                                @Override
                                public void onResult(int code, Void result, Throwable exception) {
                                    if (code == ResponseCode.RES_SUCCESS) {// 修改成功
                                        UIUtils.showToast("修改成功");
                                        getUserInfoFromRemote();// 重新加载个人资料
                                    } else {// 修改失败
                                        UIUtils.showToast("修改失败，请重试");
                                    }
                                    hideWaitingDialog();
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("个人信息");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
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

    private void updateGender(final GenderEnum gender) {
        updateGenderView(gender);
        showWaitingDialog("请稍等");
        Map<UserInfoFieldEnum, Object> fields = new HashMap(1);
        fields.put(UserInfoFieldEnum.GENDER, gender.getValue());
        NimUserInfoSDK.updateUserInfo(fields, new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int code, Void result, Throwable exception) {
                hideWaitingDialog();
                if (code == ResponseCode.RES_SUCCESS) {
                    UIUtils.showToast("修改成功");
                    mDialog.dismiss();
                } else {
                    UIUtils.showToast("修改失败");
                }
            }
        });
    }

    private void updateGenderView(GenderEnum gender) {
        if (gender == GenderEnum.MALE) {
            mTvMale.setCompoundDrawables(null, null, mSelectedDrawable, null);
            mTvFemale.setCompoundDrawables(null, null, mUnSelectedDrawable, null);
        } else if (gender == GenderEnum.FEMALE) {
            mTvMale.setCompoundDrawables(null, null, mUnSelectedDrawable, null);
            mTvFemale.setCompoundDrawables(null, null, mSelectedDrawable, null);
        } else {
            mTvMale.setCompoundDrawables(null, null, mUnSelectedDrawable, null);
            mTvFemale.setCompoundDrawables(null, null, mUnSelectedDrawable, null);
        }
    }
}
