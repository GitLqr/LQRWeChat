package com.lqr.wechat;


import com.lqr.wechat.utils.LogUtils;

/**
 * @创建者 CSDN_LQR
 * @描述 全局常量类
 */
public class AppConst {

    public static final String TAG = "CSDN_LQR";
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;//日志输出级别
    public static final int CACHELTIMEOUT = 10 * 60 * 1000;// 10分钟(缓存过期时间)

    public static final String NETWORK_CHANGE_RECEIVED_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public static final String SERVER_ADDRESS = "http://xxx.com/client";

    public static final class Account {
        public static final String KEY_USER_ACCOUNT = "account";
        public static final String KEY_USER_TOKEN = "token";
    }

    //二维码扫码指令前缀
    public static final class QRCodeCommend {
        public static final String ACCOUNT = "account:";
        public static final String TEAMID = "teamId:";
    }

    //用户
    public static final class User {
        private static final String USER = SERVER_ADDRESS + "/user";
        public static final String LOGIN = USER + "/login";//登录
        public static final String REGISTER = USER + "/insertOrUpdate";//注册
        public static final String WX_LOGIN = USER + "/androidWXLogin";//微信登录
    }

    public static final class Url {
        //帮助与反馈
        public static final String HELP_FEEDBACK = "https://kf.qq.com/touch/product/wechat_app.html?scene_id=kf338&code=021njRdi0RdQfk1Khybi0kEQdi0njRde&state=123";
        //购物
        public static final String SHOP = "http://wqs.jd.com/portal/wx/portal_indexV4.shtml?PTAG=17007.13.1&ptype=1";
        //游戏
        public static final String GAME = "http://h.4399.com/android";
    }

    //用户拓展信息字段
    public static final class UserInfoExt {
        public static final String AREA = "area";
        public static final String PHONE = "phone";
    }

    //我的群成员信息拓展字段
    public static final class MyTeamMemberExt {
        public static final String SHOULD_SHOW_NICK_NAME = "shouldShowNickName";
    }

}
