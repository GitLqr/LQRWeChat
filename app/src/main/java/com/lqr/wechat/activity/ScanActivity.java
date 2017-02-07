package com.lqr.wechat.activity;

import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.bean.ImageItem;
import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.wechat.AppConst;
import com.lqr.wechat.R;
import com.lqr.wechat.factory.PopupWindowFactory;
import com.lqr.wechat.factory.ThreadPoolFactory;
import com.lqr.wechat.model.UserCache;
import com.lqr.wechat.nimsdk.NimFriendSDK;
import com.lqr.wechat.nimsdk.NimTeamSDK;
import com.lqr.wechat.utils.LogUtils;
import com.lqr.wechat.utils.UIUtils;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

import static com.lqr.wechat.activity.SessionActivity.IMAGE_PICKER;

/**
 * @创建者 CSDN_LQR
 * @描述 扫一扫
 */
public class ScanActivity extends BaseActivity implements QRCodeView.Delegate {

    private FrameLayout mView;
    private PopupWindow mPopupWindow;

    @InjectView(R.id.zxingview)
    ZXingView mZxingview;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.llSaoma)
    LinearLayout mLlSaoma;
    @InjectView(R.id.llFengmian)
    LinearLayout mLlFengmian;
    @InjectView(R.id.llJiejing)
    LinearLayout mLlJiejing;
    @InjectView(R.id.llFanyi)
    LinearLayout mLlFanyi;

    @InjectView(R.id.ivSaomaPress)
    ImageView mIvSaomaPress;
    @InjectView(R.id.ivSaomaNormal)
    ImageView mIvSaomaNormal;
    @InjectView(R.id.ivFengmianPress)
    ImageView mIvFengmianPress;
    @InjectView(R.id.ivFengmianNormal)
    ImageView mIvFengmianNormal;
    @InjectView(R.id.ivJiejingPress)
    ImageView mIvJiejingPress;
    @InjectView(R.id.ivJiejingNormal)
    ImageView mIvJiejingNormal;
    @InjectView(R.id.ivFanyiPress)
    ImageView mIvFanyiPress;
    @InjectView(R.id.ivFanyiNormal)
    ImageView mIvFanyiNormal;

    @OnClick({R.id.llSaoma, R.id.llFengmian, R.id.llJiejing, R.id.llFanyi})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.llSaoma:
                selectBottomOne(0);
                break;
            case R.id.llFengmian:
                selectBottomOne(1);
                break;
            case R.id.llJiejing:
                selectBottomOne(2);
                break;
            case R.id.llFanyi:
                selectBottomOne(3);
                break;
        }
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_scan);
        ButterKnife.inject(this);
        initToolbar();
        selectBottomOne(0);
    }

    @Override
    public void initListener() {
        mZxingview.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZxingview.startCamera();
        mZxingview.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mZxingview.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mZxingview.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.itemMore:
                showPopupMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("二级码/条码");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
    }

    public void selectBottomOne(int switchItem) {
        mIvSaomaPress.setVisibility(View.GONE);
        mIvFengmianPress.setVisibility(View.GONE);
        mIvJiejingPress.setVisibility(View.GONE);
        mIvFanyiPress.setVisibility(View.GONE);
        switch (switchItem) {
            case 0:
                getSupportActionBar().setTitle("二级码/条码");
                mIvSaomaPress.setVisibility(View.VISIBLE);
                break;
            case 1:
                getSupportActionBar().setTitle("封面/电影海报");
                mIvFengmianPress.setVisibility(View.VISIBLE);
                break;
            case 2:
                getSupportActionBar().setTitle("街景");
                mIvJiejingPress.setVisibility(View.VISIBLE);
                break;
            case 3:
                getSupportActionBar().setTitle("翻译");
                mIvFanyiPress.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showPopupMenu() {
        if (mView == null) {
            mView = new FrameLayout(this);
            mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mView.setBackgroundColor(UIUtils.getColor(R.color.white));

            TextView tv = new TextView(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2Px(45));
            tv.setLayoutParams(params);
            tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            tv.setPadding(UIUtils.dip2Px(20), 0, 0, 0);
            tv.setTextColor(UIUtils.getColor(R.color.gray0));
            tv.setTextSize(14);
            tv.setText("从相册选取二维码");
            mView.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    Intent intent = new Intent(ScanActivity.this, ImageGridActivity.class);
                    startActivityForResult(intent, IMAGE_PICKER);
                }
            });
        }
        mPopupWindow = PopupWindowFactory.getPopupWindowAtLocation(mView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, getWindow().getDecorView().getRootView(), Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopupWindowFactory.makeWindowLight(ScanActivity.this);
            }
        });
        PopupWindowFactory.makeWindowDark(this);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LogUtils.sf(result);
        handleResult(result);
    }

    private void handleResult(String result) {
        vibrate();
        mZxingview.startSpot();
        //添加用户
        if (result.startsWith(AppConst.QRCodeCommend.ACCOUNT)) {
            String account = result.substring(AppConst.QRCodeCommend.ACCOUNT.length());
//            UIUtils.showToast("微信号：" + account);
            if (NimFriendSDK.isMyFriend(account)) {
                UIUtils.showToast("该用户已经是您的好友");
                return;
            }
            Intent intent = new Intent(ScanActivity.this, PostscriptActivity.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
        // 进群
        else if (result.startsWith(AppConst.QRCodeCommend.TEAMID)) {
            final String teamId = result.substring(AppConst.QRCodeCommend.TEAMID.length());
            NimTeamSDK.searchTeam(teamId, new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team team) {
                    if (team.isMyTeam()) {
                        UIUtils.showToast("您已经在群聊中");
                    } else {
                        List<String> accounts = new ArrayList<String>(1);
                        accounts.add(UserCache.getAccount());
                        NimTeamSDK.addMembers(teamId, accounts, new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                //跳转到群聊
                                Intent intent = new Intent(ScanActivity.this, SessionActivity.class);
                                intent.putExtra(SessionActivity.SESSION_ACCOUNT, teamId);
                                intent.putExtra(SessionActivity.SESSION_TYPE, SessionTypeEnum.Team);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailed(int code) {
                                UIUtils.showToast("加群失败" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                UIUtils.showToast("加群失败");
                                exception.printStackTrace();
                            }
                        });
                    }
                }

                @Override
                public void onFailed(int code) {
                    UIUtils.showToast("查不到群" + code);
                }

                @Override
                public void onException(Throwable exception) {
                    UIUtils.showToast("查不到群");
                    exception.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        UIUtils.showToast("打开相机出错");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {//返回多张照片
            if (data != null) {
                //是否发送原图
//                boolean isOrig = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
                final ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size() > 0) {
                    //取第一张照片
                    ThreadPoolFactory.getNormalPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            String result = QRCodeDecoder.syncDecodeQRCode(images.get(0).path);
                            if (TextUtils.isEmpty(result)) {
                                UIUtils.showToast("扫描失败");
                            } else {
                                handleResult(result);
                            }
                        }
                    });
                }
            }
        }
    }
}
