package com.lqr.wechat.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lqr.wechat.R;
import com.lqr.wechat.view.CustomDialog;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * @创建者 CSDN_LQR
 * @描述 Fragment的基类
 */
public abstract class BaseFragment extends Fragment {

    private CustomDialog mDialogWaiting;
    private MaterialDialog mMaterialDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return initView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 初始化fview，而且是必须实现，但是不知道具体实现，定义成抽象方法
     *
     * @return
     */
    public abstract View initView();

    public void init() {

    }

    public void initData() {

    }

    public void initListener() {

    }

    /**
     * 显示等待提示框
     */
    public Dialog showWaitingDialog(String tip) {
        hideWaitingDialog();
        View view = View.inflate(getActivity(), R.layout.dialog_waiting, null);
        if (!TextUtils.isEmpty(tip))
            ((TextView) view.findViewById(R.id.tvTip)).setText(tip);
        mDialogWaiting = new CustomDialog(getActivity(), view, R.style.dialog);
        mDialogWaiting.show();
        return mDialogWaiting;
    }

    /**
     * 隐藏等待提示框
     */
    public void hideWaitingDialog() {
        if (mDialogWaiting != null) {
            mDialogWaiting.dismiss();
            mDialogWaiting = null;
        }
    }

    /**
     * 显示MaterialDialog
     */
    public MaterialDialog showMaterialDialog(String tip, String message, String positiveText, String negativeText, View.OnClickListener positiveButtonClickListener, View.OnClickListener negativeButtonClickListener) {
        hideMaterialDialog();
        mMaterialDialog = new MaterialDialog(getActivity());
        if (!TextUtils.isEmpty(tip)) {
            mMaterialDialog.setTitle(tip);
        }
        if (!TextUtils.isEmpty(message)) {
            mMaterialDialog.setMessage(message);
        }
        if (!TextUtils.isEmpty(positiveText)) {
            mMaterialDialog.setPositiveButton(positiveText, positiveButtonClickListener);
        }
        if (!TextUtils.isEmpty(negativeText)) {
            mMaterialDialog.setNegativeButton(negativeText, negativeButtonClickListener);
        }
        mMaterialDialog.show();
        return mMaterialDialog;
    }

    /**
     * 隐藏MaterialDialog
     */
    public void hideMaterialDialog() {
        if (mMaterialDialog != null) {
            mMaterialDialog.dismiss();
            mMaterialDialog = null;
        }
    }

}
