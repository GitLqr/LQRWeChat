package com.lqr.wechat.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqr.imagepicker.ui.ImageGridActivity;
import com.lqr.wechat.R;
import com.lqr.wechat.activity.RedPacketActivity;
import com.lqr.wechat.activity.SessionActivity;
import com.lqr.wechat.activity.TransferActivity;
import com.lqr.wechat.view.CustomDialog;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.lqr.wechat.R.id.tvOne;
import static com.lqr.wechat.R.id.tvTwo;
import static com.lqr.wechat.activity.SessionActivity.IMAGE_PICKER;

/**
 * @创建者 CSDN_LQR
 * @描述 聊天界面功能页面1
 */
public class Func1Fragment extends BaseFragment {

    private View mContentView;
    private CustomDialog mDialog;
    private TextView mTvOne;
    private TextView mTvTwo;

    @InjectView(R.id.llPic)
    LinearLayout mLlPic;
    @InjectView(R.id.llRecord)
    LinearLayout mLlRecord;
    @InjectView(R.id.llRedPacket)
    LinearLayout mLlRedPacket;
    @InjectView(R.id.llTransfer)
    LinearLayout mLlTransfer;

    @InjectView(R.id.llCollection)
    LinearLayout mLlCollection;
    @InjectView(R.id.llLocation)
    LinearLayout mLlLocation;
    @InjectView(R.id.llVideo)
    LinearLayout mLlVideo;
    @InjectView(R.id.llBusinessCard)
    LinearLayout mLlBusinessCard;

    Intent mIntent;

    @OnClick({R.id.llPic, R.id.llRecord, R.id.llRedPacket, R.id.llTransfer, R.id.llLocation, R.id.llVideo})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.llPic:
                mIntent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(mIntent, IMAGE_PICKER);
                break;
            case R.id.llRecord:
                ((SessionActivity)getActivity()).showPlayVideo();
                break;
            case R.id.llRedPacket:
                mIntent = new Intent(getActivity(), RedPacketActivity.class);
                startActivity(mIntent);
                break;
            case R.id.llTransfer:
                mIntent = new Intent(getActivity(), TransferActivity.class);
                startActivity(mIntent);
                break;
            case R.id.llLocation:
                mContentView = View.inflate(getActivity(), R.layout.dialog_menu_two_session, null);
                mDialog = new CustomDialog(getActivity(), mContentView, R.style.dialog);
                mDialog.show();
                mTvOne = (TextView) mContentView.findViewById(tvOne);
                mTvTwo = (TextView) mContentView.findViewById(tvTwo);
                mTvOne.setText("发送位置");
                mTvTwo.setText("共享实时位置");
                mTvOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mTvTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });

                break;
            case R.id.llVideo:
                mContentView = View.inflate(getActivity(), R.layout.dialog_menu_two_session, null);
                mDialog = new CustomDialog(getActivity(), mContentView, R.style.dialog);
                mDialog.show();
                mTvOne = (TextView) mContentView.findViewById(tvOne);
                mTvTwo = (TextView) mContentView.findViewById(tvTwo);
                mTvOne.setText("视频聊天");
                mTvTwo.setText("语音聊天");
                mTvOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                mTvTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                break;

        }
    }

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_func_page1, null);
        ButterKnife.inject(this, view);
        return view;
    }

}
