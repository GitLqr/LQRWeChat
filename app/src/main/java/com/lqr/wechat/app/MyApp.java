package com.lqr.wechat.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONException;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lqr.emoji.LQREmotionKit;
import com.lqr.imagepicker.ImagePicker;
import com.lqr.imagepicker.loader.ImageLoader;
import com.lqr.imagepicker.view.CropImageView;
import com.lqr.wechat.api.redpacket.SignService;
import com.lqr.wechat.app.base.BaseApp;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.db.model.Friend;
import com.lqr.wechat.db.model.Groups;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.manager.JsonMananger;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.data.GroupNotificationMessageData;
import com.lqr.wechat.model.message.DeleteContactMessage;
import com.lqr.wechat.model.message.RedPacketMessage;
import com.lqr.wechat.model.redpacket.SignModel;
import com.lqr.wechat.model.response.ContactNotificationMessageData;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.PinyinUtils;
import com.lqr.wechat.util.RedPacketUtil;
import com.lqr.wechat.util.UIUtils;
import com.yunzhanghu.redpacketsdk.RPInitRedPacketCallback;
import com.yunzhanghu.redpacketsdk.RPValueCallback;
import com.yunzhanghu.redpacketsdk.RedPacket;
import com.yunzhanghu.redpacketsdk.bean.RedPacketInfo;
import com.yunzhanghu.redpacketsdk.bean.TokenData;
import com.yunzhanghu.redpacketsdk.constant.RPConstant;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.GroupNotificationMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ━━━━━━神兽出没━━━━━━
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃  神兽保佑
 * 　　　　┃　　　┃  代码无bug
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━感觉萌萌哒━━━━━━
 *
 * @创建者 CSDN_LQR
 * @描述 BaseApp的拓展，用于设置其他第三方的初始化
 */
public class MyApp extends BaseApp implements RongIMClient.OnReceiveMessageListener {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        //初始化融云
        initRongCloud();
        //初始化红包
        initRedPacket();
        //初始化仿微信控件ImagePicker
        initImagePicker();
        //初始化表情控件
        LQREmotionKit.init(this, (context, path, imageView) -> Glide.with(context).load(path).centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView));
        //初始化ShareSDK
        ShareSDK.initSDK(getContext());
    }

    @Override
    public boolean onReceived(Message message, int i) {
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof ContactNotificationMessage) {
            ContactNotificationMessage contactNotificationMessage = (ContactNotificationMessage) messageContent;
            if (contactNotificationMessage.getOperation().equals(ContactNotificationMessage.CONTACT_OPERATION_REQUEST)) {
                //对方发来好友邀请
                BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_RED_DOT);
            } else {
                //对方同意我的好友请求
                ContactNotificationMessageData c = null;
                try {
                    c = JsonMananger.jsonToBean(contactNotificationMessage.getExtra(), ContactNotificationMessageData.class);
                } catch (HttpException e) {
                    e.printStackTrace();
                    return false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                if (c != null) {
                    if (DBManager.getInstance().isMyFriend(contactNotificationMessage.getSourceUserId()))
                        return false;
                    DBManager.getInstance().saveOrUpdateFriend(
                            new Friend(contactNotificationMessage.getSourceUserId(),
                                    c.getSourceUserNickname(),
                                    null, c.getSourceUserNickname(), null, null,
                                    null, null,
                                    PinyinUtils.getPinyin(c.getSourceUserNickname()),
                                    PinyinUtils.getPinyin(c.getSourceUserNickname())
                            )
                    );
                    BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_FRIEND);
                    BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_RED_DOT);
                }
            }
        } else if (messageContent instanceof DeleteContactMessage) {
            DeleteContactMessage deleteContactMessage = (DeleteContactMessage) messageContent;
            String contact_id = deleteContactMessage.getContact_id();
            RongIMClient.getInstance().getConversation(Conversation.ConversationType.PRIVATE, contact_id, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    RongIMClient.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, contact_id, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            RongIMClient.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, contact_id, null);
                            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {

                        }
                    });
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
            DBManager.getInstance().deleteFriendById(contact_id);
            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_FRIEND);
        } else if (messageContent instanceof GroupNotificationMessage) {
            GroupNotificationMessage groupNotificationMessage = (GroupNotificationMessage) messageContent;
            String groupId = message.getTargetId();
            GroupNotificationMessageData data = null;
            try {
                String curUserId = UserCache.getId();
                try {
                    data = jsonToBean(groupNotificationMessage.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (groupNotificationMessage.getOperation().equals(GroupNotificationMessage.GROUP_OPERATION_CREATE)) {
                    DBManager.getInstance().getGroups(groupId);
                    DBManager.getInstance().getGroupMember(groupId);
                } else if (groupNotificationMessage.getOperation().equals(GroupNotificationMessage.GROUP_OPERATION_DISMISS)) {
                    handleGroupDismiss(groupId);
                } else if (groupNotificationMessage.getOperation().equals(GroupNotificationMessage.GROUP_OPERATION_KICKED)) {
                    if (data != null) {
                        List<String> memberIdList = data.getTargetUserIds();
                        if (memberIdList != null) {
                            for (String userId : memberIdList) {
                                if (curUserId.equals(userId)) {
                                    RongIMClient.getInstance().removeConversation(Conversation.ConversationType.GROUP, message.getTargetId(), new RongIMClient.ResultCallback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                            LogUtils.sf("Conversation remove successfully.");
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode e) {

                                        }
                                    });
                                }
                            }
                        }
                        List<String> kickedUserIDs = data.getTargetUserIds();
                        DBManager.getInstance().deleteGroupMembers(groupId, kickedUserIDs);
                        //因为操作存在异步，故不在这里发送广播
//                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_GROUP_MEMBER, groupId);
//                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                    }
                } else if (groupNotificationMessage.getOperation().equals(GroupNotificationMessage.GROUP_OPERATION_ADD)) {
                    DBManager.getInstance().getGroups(groupId);
                    DBManager.getInstance().getGroupMember(groupId);
                    //因为操作存在异步，故不在这里发送广播
//                    BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_GROUP_MEMBER, groupId);
                } else if (groupNotificationMessage.getOperation().equals(GroupNotificationMessage.GROUP_OPERATION_QUIT)) {
                    if (data != null) {
                        List<String> quitUserIDs = data.getTargetUserIds();
                        DBManager.getInstance().deleteGroupMembers(groupId, quitUserIDs);
                        //因为操作存在异步，故不在这里发送广播
//                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_GROUP_MEMBER, groupId);
//                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                    }
                } else if (groupNotificationMessage.getOperation().equals(GroupNotificationMessage.GROUP_OPERATION_RENAME)) {
                    if (data != null) {
                        String targetGroupName = data.getTargetGroupName();
                        DBManager.getInstance().updateGroupsName(groupId, targetGroupName);
                        //更新群名
                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CURRENT_SESSION_NAME);
                        BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
        } else {
            //TODO:还有其他类型的信息
            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
            BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CURRENT_SESSION, message);
        }
        return false;
    }

    private void handleGroupDismiss(final String groupId) {
        RongIMClient.getInstance().getConversation(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Conversation>() {
            @Override
            public void onSuccess(Conversation conversation) {
                RongIMClient.getInstance().clearMessages(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        RongIMClient.getInstance().removeConversation(Conversation.ConversationType.GROUP, groupId, new RongIMClient.ResultCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                DBManager.getInstance().deleteGroup(new Groups(groupId));
                                DBManager.getInstance().deleteGroupMembersByGroupId(groupId);
                                BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                                BroadcastManager.getInstance(getContext()).sendBroadcast(AppConst.GROUP_LIST_UPDATE);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {

                            }
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    private GroupNotificationMessageData jsonToBean(String data) {
        GroupNotificationMessageData dataEntity = new GroupNotificationMessageData();
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.has("operatorNickname")) {
                dataEntity.setOperatorNickname(jsonObject.getString("operatorNickname"));
            }
            if (jsonObject.has("targetGroupName")) {
                dataEntity.setTargetGroupName(jsonObject.getString("targetGroupName"));
            }
            if (jsonObject.has("timestamp")) {
                dataEntity.setTimestamp(jsonObject.getLong("timestamp"));
            }
            if (jsonObject.has("targetUserIds")) {
                JSONArray jsonArray = jsonObject.getJSONArray("targetUserIds");
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataEntity.getTargetUserIds().add(jsonArray.getString(i));
                }
            }
            if (jsonObject.has("targetUserDisplayNames")) {
                JSONArray jsonArray = jsonObject.getJSONArray("targetUserDisplayNames");
                for (int i = 0; i < jsonArray.length(); i++) {
                    dataEntity.getTargetUserDisplayNames().add(jsonArray.getString(i));
                }
            }
            if (jsonObject.has("oldCreatorId")) {
                dataEntity.setOldCreatorId(jsonObject.getString("oldCreatorId"));
            }
            if (jsonObject.has("oldCreatorName")) {
                dataEntity.setOldCreatorName(jsonObject.getString("oldCreatorName"));
            }
            if (jsonObject.has("newCreatorId")) {
                dataEntity.setNewCreatorId(jsonObject.getString("newCreatorId"));
            }
            if (jsonObject.has("newCreatorName")) {
                dataEntity.setNewCreatorName(jsonObject.getString("newCreatorName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataEntity;
    }

    private void initRongCloud() {
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIMClient 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
            RongIMClient.init(this);
        }

        //监听接收到的消息
        RongIMClient.setOnReceiveMessageListener(this);
        try {
            RongIMClient.registerMessageType(RedPacketMessage.class);
            RongIMClient.registerMessageType(DeleteContactMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initRedPacket() {
        //初始化红包SDK
        RedPacket.getInstance().initRedPacket(this, RPConstant.AUTH_METHOD_SIGN, new RPInitRedPacketCallback() {
            //设置初始化TokenData的回调函数
            //说明 ：该回调函数在红包token不存在、切换用户、红包token过期、签名过期的情况下触发。
            //注意 ：以上情况不需要开发者维护，由红包SDK在请求红包相关服务时进行处理。
            @Override
            public void initTokenData(final RPValueCallback<TokenData> callback) {
                //异步向App Server获取签名参数
                //这里使用随机生成的UUID代替App中的userId,生产环境需要传入App的userId
                String token = "tempValue";
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        //Demo用URL,生产环境需要替换成APP Server提供的签名URL
                        .baseUrl("https://rpv2.yunzhanghu.com/")
                        .build();
                SignService signService = retrofit.create(SignService.class);
                Call<SignModel> call = signService.getSignInfo(UserCache.getId(), token);
                call.enqueue(new Callback<SignModel>() {
                    @Override
                    public void onResponse(Call<SignModel> call, Response<SignModel> response) {
                        if (response.isSuccessful()) {
                            SignModel signModel = response.body();
                            LogUtils.sf(signModel.toString());
                            //赋值返回参数给TokenData
                            TokenData tokenData = new TokenData();
                            tokenData.authPartner = signModel.partner;
                            tokenData.authSign = signModel.sign;
                            tokenData.appUserId = signModel.user_id;
                            tokenData.timestamp = signModel.timestamp;
                            //回传签名参数给红包SDK
                            callback.onSuccess(tokenData);
                        } else {
                            String statusCode = response.code() + "";
                            callback.onError(statusCode, response.errorBody().toString());
                            LogUtils.d("StatusCode : " + statusCode + " Message : " + response.errorBody().toString());
                        }

                    }

                    @Override
                    public void onFailure(Call<SignModel> call, Throwable t) {
                        LogUtils.d("onFailure :" + t.getMessage());
                        callback.onError("onFailure", t.getMessage());
                    }
                });
            }

            @Override
            public RedPacketInfo initCurrentUserSync() {
                return RedPacketUtil.getCurrentUserInfo();
            }
        });
        //开启红包相关日志输出
        RedPacket.getInstance().setDebugMode(true);
    }

    /**
     * 初始化仿微信控件ImagePicker
     */
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
                Glide.with(getContext()).load(Uri.parse("file://" + path).toString()).centerCrop().into(imageView);
            }

            @Override
            public void clearMemoryCache() {

            }
        });   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
