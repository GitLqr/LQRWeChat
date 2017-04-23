package com.lqr.wechat.ui.presenter;

import android.text.TextUtils;

import com.lqr.wechat.R;
import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.exception.ServerException;
import com.lqr.wechat.model.response.CheckPhoneResponse;
import com.lqr.wechat.model.response.LoginResponse;
import com.lqr.wechat.model.response.RegisterResponse;
import com.lqr.wechat.model.response.SendCodeResponse;
import com.lqr.wechat.model.response.VerifyCodeResponse;
import com.lqr.wechat.ui.activity.LoginActivity;
import com.lqr.wechat.ui.activity.MainActivity;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.ui.view.IRegisterAtView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.RegularUtils;
import com.lqr.wechat.util.UIUtils;

import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RegisterAtPresenter extends BasePresenter<IRegisterAtView> {

    int time = 0;
    private Timer mTimer;
    private Subscription mSubscription;

    public RegisterAtPresenter(BaseActivity context) {
        super(context);
    }

    public void sendCode() {
        String phone = getView().getEtPhone().getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            UIUtils.showToast(UIUtils.getString(R.string.phone_not_empty));
            return;
        }

        if (!RegularUtils.isMobile(phone)) {
            UIUtils.showToast(UIUtils.getString(R.string.phone_format_error));
            return;
        }

        mContext.showWaitingDialog(UIUtils.getString(R.string.please_wait));
        ApiRetrofit.getInstance().checkPhoneAvailable(AppConst.REGION, phone)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<CheckPhoneResponse, Observable<SendCodeResponse>>() {
                    @Override
                    public Observable<SendCodeResponse> call(CheckPhoneResponse checkPhoneResponse) {
                        int code = checkPhoneResponse.getCode();
                        if (code == 200) {
                            return ApiRetrofit.getInstance().sendCode(AppConst.REGION, phone);
                        } else {
                            return Observable.error(new ServerException(UIUtils.getString(R.string.phone_not_available)));
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sendCodeResponse -> {
                    mContext.hideWaitingDialog();
                    int code = sendCodeResponse.getCode();
                    if (code == 200) {
                        changeSendCodeBtn();
                    } else {
                        sendCodeError(new ServerException(UIUtils.getString(R.string.send_code_error)));
                    }
                }, this::sendCodeError);
    }

    private void sendCodeError(Throwable throwable) {
        mContext.hideWaitingDialog();
        LogUtils.e(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    private void changeSendCodeBtn() {
        //开始1分钟倒计时
        //每一秒执行一次Task
        mSubscription = Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
            time = 60;
            TimerTask mTask = new TimerTask() {
                @Override
                public void run() {
                    subscriber.onNext(--time);
                }
            };
            mTimer = new Timer();
            mTimer.schedule(mTask, 0, 1000);//每一秒执行一次Task
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(time -> {
                    if (getView().getBtnSendCode() != null) {
                        if (time >= 0) {
                            getView().getBtnSendCode().setEnabled(false);
                            getView().getBtnSendCode().setText(time + "");
                        } else {
                            getView().getBtnSendCode().setEnabled(true);
                            getView().getBtnSendCode().setText(UIUtils.getString(R.string.send_code_btn_normal_tip));
                        }
                    } else {
                        mTimer.cancel();
                    }
                }, throwable -> LogUtils.sf(throwable.getLocalizedMessage()));
    }

    public void register() {
        String phone = getView().getEtPhone().getText().toString().trim();
        String password = getView().getEtPwd().getText().toString().trim();
        String nickName = getView().getEtNickName().getText().toString().trim();
        String code = getView().getEtVerifyCode().getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            UIUtils.showToast(UIUtils.getString(R.string.phone_not_empty));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            UIUtils.showToast(UIUtils.getString(R.string.password_not_empty));
            return;
        }
        if (TextUtils.isEmpty(nickName)) {
            UIUtils.showToast(UIUtils.getString(R.string.nickname_not_empty));
            return;
        }
        if (TextUtils.isEmpty(code)) {
            UIUtils.showToast(UIUtils.getString(R.string.vertify_code_not_empty));
            return;
        }

        ApiRetrofit.getInstance().verifyCode(AppConst.REGION, phone, code)
                .flatMap(new Func1<VerifyCodeResponse, Observable<RegisterResponse>>() {
                    @Override
                    public Observable<RegisterResponse> call(VerifyCodeResponse verifyCodeResponse) {
                        int code = verifyCodeResponse.getCode();
                        if (code == 200) {
                            return ApiRetrofit.getInstance().register(nickName, password, verifyCodeResponse.getResult().getVerification_token());
                        } else {
                            return Observable.error(new ServerException(UIUtils.getString(R.string.vertify_code_error) + code));
                        }
                    }
                })
                .flatMap(new Func1<RegisterResponse, Observable<LoginResponse>>() {
                    @Override
                    public Observable<LoginResponse> call(RegisterResponse registerResponse) {
                        int code = registerResponse.getCode();
                        if (code == 200) {
                            return ApiRetrofit.getInstance().login(AppConst.REGION, phone, password);
                        } else {
                            return Observable.error(new ServerException(UIUtils.getString(R.string.register_error) + code));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    int responseCode = loginResponse.getCode();
                    if (responseCode == 200) {
                        UserCache.save(loginResponse.getResult().getId(), phone, loginResponse.getResult().getToken());
                        mContext.jumpToActivityAndClearTask(MainActivity.class);
                        mContext.finish();
                    } else {
                        UIUtils.showToast(UIUtils.getString(R.string.login_error));
                        mContext.jumpToActivity(LoginActivity.class);
                    }
                }, this::registerError);
    }

    private void registerError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        UIUtils.showToast(throwable.getLocalizedMessage());
    }

    public void unsubscribe() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

}
