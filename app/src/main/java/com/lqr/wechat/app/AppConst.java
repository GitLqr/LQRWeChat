package com.lqr.wechat.app;


import com.lqr.wechat.util.FileUtils;
import com.lqr.wechat.util.LogUtils;

/**
 * @创建者 CSDN_LQR
 * @描述 全局常量类
 */
public class AppConst {

    public static final String TAG = "CSDN_LQR";
    public static final int DEBUGLEVEL = LogUtils.LEVEL_ALL;//日志输出级别

    public static final String REGION = "86";

    /*================== 广播Action begin ==================*/
    //全局数据获取
    public static final String FETCH_COMPLETE = "fetch_complete";
    //好友
    public static final String UPDATE_FRIEND = "update_friend";
    public static final String UPDATE_RED_DOT = "update_red_dot";
    //群组
    public static final String UPDATE_GROUP_NAME = "update_group_name";
    public static final String GROUP_LIST_UPDATE = "group_list_update";
    public static final String UPDATE_GROUP = "update_group";
    public static final String UPDATE_GROUP_MEMBER = "update_group_member";
    public static final String GROUP_DISMISS = "group_dismiss";
    //个人信息
    public static final String CHANGE_INFO_FOR_ME = "change_info_for_me";
    public static final String CHANGE_INFO_FOR_CHANGE_NAME = "change_info_for_change_name";
    public static final String CHANGE_INFO_FOR_USER_INFO = "change_info_for_user_info";
    //会话
    public static final String UPDATE_CONVERSATIONS = "update_conversations";
    public static final String UPDATE_CURRENT_SESSION = "update_current_session";
    public static final String UPDATE_CURRENT_SESSION_NAME = "update_current_session_name";
    public static final String REFRESH_CURRENT_SESSION = "refresh_current_session";
    public static final String CLOSE_CURRENT_SESSION = "close_current_session";
    /*================== 广播Action end ==================*/


    public static final class User {
        public static final String ID = "id";
        public static final String PHONE = "phone";
        //        public static final String ACCOUNT = "account";
        public static final String TOKEN = "token";
    }


    public static final class WeChatUrl {
        public static final String HELP_FEED_BACK = "https://kf.qq.com/touch/product/wechat_app.html?scene_id=kf338&code=001ls8gj1IuCnz0kiUfj15uIfj1ls8ga&state=123";
        public static final String JD = "https://m.jd.com/";
        public static final String GAME = "http://h.4399.com/android/?301";
        public static final String MY_JIAN_SHU = "http://www.jianshu.com/u/f9de259236a3";
        public static final String MY_CSDN = "http://blog.csdn.net/csdn_lqr";
        public static final String MY_OSCHINA = "https://git.oschina.net/CSDNLQR";
        public static final String MY_GITHUB = "https://github.com/GitLqr";
    }

    public static final class QrCodeCommon {
        public static final String ADD = "add:";//加好友
        public static final String JOIN = "join:";//入群
    }

    //语音存放位置
    public static final String AUDIO_SAVE_DIR = FileUtils.getDir("audio");
    public static final int DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND = 120;
    //视频存放位置
    public static final String VIDEO_SAVE_DIR = FileUtils.getDir("video");
    //照片存放位置
    public static final String PHOTO_SAVE_DIR = FileUtils.getDir("photo");
    //头像保存位置
    public static final String HEADER_SAVE_DIR = FileUtils.getDir("header");
}
