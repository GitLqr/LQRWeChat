package com.lqr.wechat.ui.base;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseFragmentPresenter<V> {

    /*================== 以下是网络请求接口 ==================*/

    public BaseFragmentActivity mContext;

    public BaseFragmentPresenter(BaseFragmentActivity context) {
        mContext = context;
    }

    protected Reference<V> mViewRef;
    private final CompositeSubscription mSubscriptions = new CompositeSubscription();

    public void attachView(V view) {
        mViewRef = new WeakReference<V>(view);
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    //Cancelled in detachView, so a response arriving after the page is gone does not touch it
    protected void addSubscription(Subscription subscription) {
        mSubscriptions.add(subscription);
    }

    public void detachView() {
        //clear() rather than unsubscribe(), which would make later add() calls cancel immediately
        mSubscriptions.clear();
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    public V getView() {
        return mViewRef != null ? mViewRef.get() : null;
    }

}
