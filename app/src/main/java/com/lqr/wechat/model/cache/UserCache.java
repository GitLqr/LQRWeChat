package com.lqr.wechat.model.cache;

import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.util.SPUtils;
import com.lqr.wechat.util.UIUtils;

/**
 * @创建者 GitLqr
 * @描述 用户缓存
 */
public class UserCache {

    private static SPUtils sp() {
        return SPUtils.getInstance(UIUtils.getContext());
    }

    public static String getId() {
        return sp().getString(AppConst.User.ID, "");
    }

    public static String getPhone() {
        return sp().getString(AppConst.User.PHONE, "");
    }

    public static String getToken() {
        return sp().getString(AppConst.User.TOKEN, "");
    }

    //One write for the 3 fields, so a crash in between cannot leave a half-saved login
    public static void save(String id, String phone, String token) {
        sp().edit()
                .putString(AppConst.User.ID, id)
                .putString(AppConst.User.PHONE, phone)
                .putString(AppConst.User.TOKEN, token)
                .apply();
    }

    public static void clear() {
        sp().edit()
                .remove(AppConst.User.ID)
                .remove(AppConst.User.PHONE)
                .remove(AppConst.User.TOKEN)
                .apply();
        DBManager.getInstance().deleteAllUserInfo();
    }

}
