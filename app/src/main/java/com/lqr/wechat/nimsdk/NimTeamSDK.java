package com.lqr.wechat.nimsdk;

import android.text.TextUtils;

import com.lqr.wechat.AppConst;
import com.lqr.wechat.model.UserCache;
import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.TeamServiceObserver;
import com.netease.nimlib.sdk.team.constant.TeamAllMuteModeEnum;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.MuteMemberAttachment;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建者 CSDN_LQR
 * @描述 群相关SDK
 * <p>
 * 1、普通群
 * <p>
 * 开发手册中所提及的普通群都等同于 Demo 中的讨论组。普通群没有权限操作，适用于快速创建多人会话的场景。每个普通群只有一个管理员。管理员可以对普通群进行增减员操作，普通成员只能对普通群进行增员操作。在添加新成员的时候，并不需要经过对方同意。
 * <p>
 * 2、高级群
 * <p>
 * 高级群在权限上有更多的限制，权限分为群主、管理员、以及群成员。2.4.0之前版本在添加成员的时候需要对方接受邀请；2.4.0版本之后，可以设定被邀请模式（是否需要对方同意）。高级群可以覆盖所有普通群的能力，建议开发者创建时选用高级群。
 */
public class NimTeamSDK {

    /**
     * 关闭群聊消息提醒
     * <p>
     * 群聊消息提醒可以单独打开或关闭，关闭提醒之后，用户仍然会收到这个群的消息。
     * 如果开发者使用的是云信内建的消息提醒，用户收到新消息后不会再用通知栏提醒，如果用户使用的 iOS 客户端，则他将收不到该群聊消息的 APNS 推送。
     * 如果开发者自行实现状态栏提醒，可通过 Team 的 mute 接口获取提醒配置，并决定是不是要显示通知。
     * 群聊消息提醒设置可以漫游。 开发者可通过调用一下接口打开或关闭群聊消息提醒：
     */
    public static void muteTeam(String teamId, boolean mute) {
        NIMClient.getService(TeamService.class).muteTeam(teamId, mute);
    }

    /**
     * 获取所有我加入的群(异步)
     * <p>
     * 这里获取的是所有我加入的群列表（退群、被移除群后，将不在返回列表中）
     */
    public static void queryTeamList(RequestCallbackWrapper<List<Team>> callback) {
        NIMClient.getService(TeamService.class).queryTeamList()
                .setCallback(callback);
    }

    /**
     * 获取所有我加入的群(同步)
     * <p>
     */
    public static List<Team> queryTeamListBlock() {
        List<Team> teams = NIMClient.getService(TeamService.class).queryTeamListBlock();
        return teams;
    }

    /**
     * 按照类型获取自己加入的群列表
     */
    public static void queryTeamListByType(TeamTypeEnum type, RequestCallback<List<Team>> callback) {
        NIMClient.getService(TeamService.class).queryTeamListByType(type)
                .setCallback(callback);
    }

    /**
     * 根据群ID查询群资料(异步)
     * <p>
     * 如果本地没有群组资料，则去服务器查询。如果自己不在这个群中，该接口返回的可能是过期资料，如需最新的，请调用 searchTeam 接口去服务器查询。
     */
    public static void queryTeam(String teamId, RequestCallbackWrapper<Team> callback) {
        NIMClient.getService(TeamService.class).queryTeam(teamId).setCallback(callback);
    }

    /**
     * 根据群ID查询群资料(同步)
     * <p>
     * 如果本地没有群组资料，则去服务器查询。如果自己不在这个群中，该接口返回的可能是过期资料，如需最新的，请调用 searchTeam 接口去服务器查询。
     */
    public static Team queryTeamBlock(String teamId) {
        Team team = NIMClient.getService(TeamService.class).queryTeamBlock(teamId);
        return team;
    }

    /**
     * 创建群组
     * <p>
     * 网易云信群组分为两类：普通群和高级群，两种群组的消息功能都是相同的，区别在于管理功能。普通群所有人都可以拉人入群，除群主外，其他人都不能踢人；固定群则拥有完善的成员权限体系及管理功能。创建群的接口相同，传入不同的类型参数即可。
     * <p>
     * 注意：群扩展字段最大长度为1024字节，若超限，服务器将返回414。
     *
     * @param fields   创建时可以预设群组的一些相关属性，如果是普通群，仅群名有效。fields 中，key 为数据字段，value 对对应的值，该值类型必须和 field 中定义的 fieldType 一致
     * @param type     TeamTypeEnum.Advanced,TeamTypeEnum.Normal
     * @param accounts 要入群的账号
     * @param callback
     */
    public static void createTeam(HashMap<TeamFieldEnum, Serializable> fields, TeamTypeEnum type, List<String> accounts, RequestCallback<Team> callback) {
        NIMClient.getService(TeamService.class).createTeam(fields, type, "", accounts)
                .setCallback(callback);
    }

    /**
     * 加入群组
     */
    public static void applyJoinTeam(String teamId, String reason, RequestCallback<Team> callback) {
        NIMClient.getService(TeamService.class)
                .applyJoinTeam(teamId, reason)
                .setCallback(callback);
    }

    /**
     * 解散群组
     * <p>
     * 高级群的群主可以解散群
     */
    public static void dismissTeam(String teamId, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).dismissTeam(teamId)
                .setCallback(callback);
    }

    /**
     * 拉人入群
     * <p>
     * 普通群所有人都可以拉人入群，SDK 2.4.0之前版本高级群仅管理员和拥有者可以邀请人入群， SDK 2.4.0及以后版本高级群在创建时可以设置群邀请模式，支持仅管理员或者所有人均可拉人入群。
     * <p>
     * 普通群可直接将用户拉入群聊，拉人成功，直接返回onSuccess。
     * <p>
     * 高级群不能直接拉入，发出邀请成功会返回onFailed，并且返回码为810（这是一个特例，与其他接口成功直接返回 onSuccess 有所不同）。(详情请看官方文档)
     */
    public static void addMembers(String teamId, List<String> accounts, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).addMembers(teamId, accounts)
                .setCallback(callback);
    }

    /**
     * 踢人出群(单个)
     * <p>
     * 普通群仅拥有者可以踢人，高级群拥有者和管理员可以踢人，且管理员不能踢拥有者和其他管理员。
     * <p>
     * 踢人后，群内所有成员(包括被踢者)会收到一条消息类型为 notification 的 IMMessage，类型为 NotificationType#KickMember, 附件类型为 MemberChangeAttachment。
     * 可以通过MemberChangeAttachment#getExtension 获取服务器设置的扩展字段。
     */
    public static void removeMember(String teamId, String account, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).removeMember(teamId, account)
                .setCallback(callback);
    }

    /**
     * 踢人出群（多个）
     */
    public static InvocationFuture<Void> removeMembers(String teamId, List<String> members) {
        InvocationFuture<Void> invocationFuture = NIMClient.getService(TeamService.class).removeMembers(teamId, members);
        return invocationFuture;
    }

    /**
     * 主动退群
     * <p>
     * 普通群群主可以退群，若退群，该群没有群主。高级群除群主外，其他用户均可以主动退群：
     * <p>
     * 退群后，群内所有成员(包括退出者)会收到一条消息类型为 notification 的 IMMessage，附件类型为 MemberChangeAttachment。
     */
    public static void quitTeam(String teamId, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).quitTeam(teamId)
                .setCallback(callback);
    }

    /**
     * 转让群组
     * <p>
     * 高级群拥有者可以将群的拥有者权限转给群内的其他成员，转移后，被转让者变为新的拥有者，原拥有者变为普通成员。原拥有者还可以选择在转让的同时，直接退出该群。
     *
     * @param teamId   群ID
     * @param account  新任拥有者的用户帐号
     * @param quit     转移时是否要同时退出该群
     * @param callback
     */
    public static void transferTeam(String teamId, String account, boolean quit, RequestCallback<List<TeamMember>> callback) {
        NIMClient.getService(TeamService.class)
                .transferTeam(teamId, account, quit)
                .setCallback(callback);
    }

    /**
     * 接受邀请
     * <p>
     * 验证入群邀请
     * <p>
     * 收到入群邀请后，用户可在系统通知中看到该邀请，并选择接受或拒绝
     */
    public static void acceptInvite(SystemMessage message, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class)
                .acceptInvite(message.getTargetId(), message.getFromAccount())
                .setCallback(callback);
    }

    /**
     * 拒绝邀请,可带上拒绝理由
     * 邀请该用户的管理员会收到一条系统通知，类型为 SystemMessageType#DeclineTeamInvite
     * <p>
     * 验证入群邀请
     * <p>
     * 收到入群邀请后，用户可在系统通知中看到该邀请，并选择接受或拒绝
     */
    public static void declineInvite(SystemMessage message, RequestCallback<Void> callback, String reason) {
        NIMClient.getService(TeamService.class)
                .declineInvite(message.getTargetId(), message.getFromAccount(), reason)
                .setCallback(callback);
    }

    /**
     * 同意申请
     * 如果同意入群申请，群内所有成员(包括申请者)都会收到一条消息类型为 notification 的 IMMessage，附件类型为 MemberChangeAttachment。
     * <p>
     * 验证入群申请
     * <p>
     * 用户发出申请后，所有管理员都会收到一条系统通知，类型为 SystemMessageType#TeamApply。管理员可选择同意或拒绝
     */
    public static void passApply(SystemMessage message, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class)
                .passApply(message.getTargetId(), message.getFromAccount())
                .setCallback(callback);
    }

    /**
     * 拒绝申请，可填写理由
     * 如果拒绝申请，申请者会收到一条系统通知，类型为 SystemMessageType#RejectTeamApply。
     * <p>
     * 验证入群申请
     * <p>
     * 用户发出申请后，所有管理员都会收到一条系统通知，类型为 SystemMessageType#TeamApply。管理员可选择同意或拒绝
     */
    public static void rejectApply(SystemMessage message, String reason, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class)
                .rejectApply(message.getTargetId(), message.getFromAccount(), reason)
                .setCallback(callback);
    }

    /**
     * 每次仅修改群的一个属性，可修改的属性包括：群名，介绍，公告，验证类型等。
     */
    public static void updateTeamField(String teamId, TeamFieldEnum teamFieldEnum, Serializable value, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).updateTeam(teamId, teamFieldEnum, value)
                .setCallback(callback);
    }

    /**
     * 批量更新群组资料，可一次性更新多个字段的值
     */
    public static InvocationFuture<Void> updateTeamFields(String teamId, Map<TeamFieldEnum, Serializable> fields) {
        InvocationFuture<Void> voidInvocationFuture = NIMClient.getService(TeamService.class).updateTeamFields(teamId, fields);
        return voidInvocationFuture;
    }

    /**
     * 修改成员的群昵称
     * <p>
     * 普通群不支持修改成员的群昵称。
     * 对于高级群，群主和管理员修改群内其他成员的群昵称，仅群主和管理员拥有权限。
     * 群主可以修改所有人的群昵称。管理员只能修改普通群成员的群昵称。
     *
     * @param teamId  所在群组ID
     * @param account 要修改的群成员帐号
     * @param nick    新的群昵称
     * @return InvocationFuture 可以设置回调函数，监听操作结果
     */
    public static void updateMemberNick(String teamId, String account, String nick, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).updateMemberNick(teamId, account, nick).setCallback(callback);
    }

    /**
     * 修改自己的群昵称
     * <p>
     * 普通群不支持修改自己的群昵称。
     *
     * @param teamId 所在群组ID
     * @param nick   新的群昵称
     * @return InvocationFuture 可以设置回调函数，监听操作结果
     */
    public static void updateMyTeamNick(String teamId, String nick, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).updateMyTeamNick(teamId, nick).setCallback(callback);
    }

    /**
     * 修改自己的群成员扩展字段（自定义属性）
     * <p>
     * 修改后，群成员会收到群成员资料变更通知。
     *
     * @param teamId 所在群组ID
     * @param extMap 新的扩展字段（自定义属性）类型：Map<String,Object>
     * @return InvocationFuture 可以设置回调函数，监听操作结果
     */
    public static void updateMyMemberExtension(String teamId, Map<String, Object> extMap, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).updateMyMemberExtension(teamId, extMap).setCallback(callback);
    }

    /**
     * 注册/注销群组资料变动观察者
     */
    public static void observeTeamUpdate(Observer<List<Team>> teamUpdateObserver, boolean register) {
        NIMClient.getService(TeamServiceObserver.class).observeTeamUpdate(teamUpdateObserver, register);
    }

    /**
     * 注册/注销群组被移除的观察者。在退群，被踢，群被解散时会收到该通知。
     */
    public static void observeTeamRemove(Observer<Team> teamRemoveObserver, boolean register) {
        NIMClient.getService(TeamServiceObserver.class).observeTeamRemove(teamRemoveObserver, register);
    }

    /**
     * 注册/注销群成员资料变化观察者。群组添加新成员，成员资料变化会收到该通知。
     */
    public static void observeMemberUpdate(Observer<List<TeamMember>> memberUpdateObserver, boolean register) {
        NIMClient.getService(TeamServiceObserver.class).observeMemberUpdate(memberUpdateObserver, register);
    }

    /**
     * 注册/注销移除群成员的观察者
     */
    public static void observeMemberRemove(Observer<TeamMember> memberRemoveObserver, boolean register) {
        NIMClient.getService(TeamServiceObserver.class).observeMemberRemove(memberRemoveObserver, register);
    }

    /**
     * 拥有者添加管理员
     *
     * @param teamId   群 ID
     * @param accounts 待提升为管理员的用户帐号列表
     * @return InvocationFuture 可以设置回调函数,如果成功，参数为新增的群管理员列表
     */
    public static void addManagers(String teamId, List<String> accounts, RequestCallback<List<TeamMember>> callback) {
        NIMClient.getService(TeamService.class)
                .addManagers(teamId, accounts)
                .setCallback(callback);
    }

    /**
     * 拥有者撤销管理员权限 <br>
     *
     * @param teamId   群ID
     * @param accounts 待撤销的管理员的帐号列表
     * @return InvocationFuture 可以设置回调函数，如果成功，参数为被撤销的群成员列表(权限已被降为Normal)。
     */
    public static void removeManagers(String teamId, List<String> accounts, RequestCallback<List<TeamMember>> callback) {
        NIMClient.getService(TeamService.class)
                .removeManagers(teamId, accounts)
                .setCallback(callback);
    }

    /**
     * 禁言、解除禁言
     *
     * @param teamId  群组ID
     * @param account 被禁言、被解除禁言的账号
     * @param mute    true表示禁言，false表示解除禁言
     * @return InvocationFuture 可以设置回调函数，监听操作结果
     */
    public static void muteTeamMember(String teamId, String account, boolean mute, RequestCallback<Void> callback) {
        NIMClient.getService(TeamService.class).muteTeamMember(teamId, account, mute).setCallback(callback);
    }

    /**
     * 获取群组成员
     * <p>
     * 该操作有可能只是从本地数据库读取缓存数据，也有可能会从服务器同步新的数据，因此耗时可能会比较长。
     */
    public static void queryMemberList(String teamId, RequestCallback<List<TeamMember>> callback) {
        NIMClient.getService(TeamService.class).queryMemberList(teamId)
                .setCallback(callback);
    }

    /**
     * 根据群ID和账号查询群成员资料(异步)
     */
    public static void queryTeamMember(String teamId, String account, RequestCallbackWrapper<TeamMember> callback) {
        NIMClient.getService(TeamService.class).queryTeamMember(teamId, account)
                .setCallback(callback);
    }

    /**
     * 根据群ID和账号查询群成员资料(同步)
     */
    public static TeamMember queryTeamMemberBlock(String teamId, String account) {
        return NIMClient.getService(TeamService.class).queryTeamMemberBlock(teamId, account);
    }

    /**
     * 查询高级群资料
     */
    public static void searchTeam(String teamId, RequestCallback<Team> callback) {
        NIMClient.getService(TeamService.class).searchTeam(teamId)
                .setCallback(callback);
    }

    /**
     * 查询被禁言群成员列表
     * <p>
     * 调用该查询接口，只返回调用 TeamService#muteTeamMember 禁言的用户，不返回使用群全员禁言接口（服务器接口）禁言的用户。
     */
    public static List<TeamMember> queryMutedTeamMembers(String teamId) {
        List<TeamMember> members = NIMClient.getService(TeamService.class).queryMutedTeamMembers(teamId);
        return members;
    }

    /**
     * 得到除自己以外其他群成员名单
     */
    public static String buildMemberListString(List<String> members, String teamId, String fromAccount) {
        StringBuilder sb = new StringBuilder();
        for (String account : members) {
            if (!TextUtils.isEmpty(fromAccount) && fromAccount.equals(account)) {
                continue;
            }
            sb.append(getTeamMemberDisplayNameWithYou(teamId, account));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 获取显示名称。用户本人显示“你”
     */
    public static String getTeamMemberDisplayNameWithYou(String tid, String account) {
        //getTeamMemberDisplayNameYou
        if (account.equals(UserCache.getAccount())) {
            return "你";
        }

        return getTeamMemberDisplayNameWithoutMe(tid, account);
    }

    /**
     * 获取显示名称。用户本人也显示昵称
     * 高级群：首先返回群昵称。没有群昵称，则返回备注名。没有设置备注名，则返回用户昵称。
     * 讨论组：首先返回备注名。没有设置备注名，则返回用户昵称。
     */
    public static String getTeamMemberDisplayNameWithoutMe(String tid, String account) {
        String memberNick = getTeamNick(tid, account);
        if (!TextUtils.isEmpty(memberNick)) {
            return memberNick;
        }

        Friend friend = NimFriendSDK.getFriendByAccount(account);
        if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
            return friend.getAlias();
        }

        NimUserInfo userInfo = NimUserInfoSDK.getUser(account);
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getName())) {
            return userInfo.getName();
        }

        return account;
    }

    public static String getTeamNick(String tid, String account) {
        Team team = NimTeamSDK.queryTeamBlock(tid);
        if (team != null && team.getType() == TeamTypeEnum.Advanced) {
            TeamMember member = NimTeamSDK.queryTeamMemberBlock(tid, account);
            if (member != null && !TextUtils.isEmpty(member.getTeamNick())) {
                return member.getTeamNick();
            }
        }
        return null;
    }

    /*================== 通知相关 start ==================*/
    public static String buildMuteTeamNotification(MuteMemberAttachment na, String teamId) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildMemberListString(na.getTargets(), teamId, null));
        sb.append("被管理员");
        sb.append(na.isMute() ? "禁言" : "解除禁言");

        return sb.toString();
    }

    public static String buildAcceptInviteNotification(MemberChangeAttachment na, String teamId, String fromAccount) {
        StringBuilder sb = new StringBuilder();

        sb.append(getTeamMemberDisplayNameWithYou(teamId, fromAccount));
        sb.append(" 接受了 ").append(buildMemberListString(na.getTargets(), teamId, null)).append(" 的入群邀请");

        return sb.toString();
    }

    public static String buildRemoveTeamManagerNotification(MemberChangeAttachment na, String teamId) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildMemberListString(na.getTargets(), teamId, null));
        sb.append(" 被撤销管理员身份");

        return sb.toString();
    }

    public static String buildAddTeamManagerNotification(MemberChangeAttachment na, String teamId) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildMemberListString(na.getTargets(), teamId, null));
        sb.append(" 被任命为管理员");

        return sb.toString();
    }

    public static String buildTransferOwnerNotification(MemberChangeAttachment na, String teamId, String fromAccount) {
        StringBuilder sb = new StringBuilder();
        sb.append(getTeamMemberDisplayNameWithYou(teamId, fromAccount));
        sb.append(" 将群转移给 ");
        sb.append(buildMemberListString(na.getTargets(), teamId, null));

        return sb.toString();
    }

    public static String buildManagerPassTeamApplyNotification(MemberChangeAttachment na, String teamId) {
        StringBuilder sb = new StringBuilder();
        sb.append("管理员通过用户 ");
        sb.append(buildMemberListString(na.getTargets(), teamId, null));
        sb.append(" 的入群申请");

        return sb.toString();
    }

    public static String buildUpdateTeamNotification(UpdateTeamAttachment a, String tid, String account) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TeamFieldEnum, Object> field : a.getUpdatedFields().entrySet()) {
            if (field.getKey() == TeamFieldEnum.Name) {
                sb.append("名称被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.Introduce) {
                sb.append("群介绍被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.Announcement) {
                sb.append(getTeamMemberDisplayNameWithYou(tid, account) + " 修改了群公告");
            } else if (field.getKey() == TeamFieldEnum.VerifyType) {
                VerifyTypeEnum type = (VerifyTypeEnum) field.getValue();
                String authen = "群身份验证权限更新为";
                if (type == VerifyTypeEnum.Free) {
                    sb.append(authen + "允许任何人加入");
                } else if (type == VerifyTypeEnum.Apply) {
                    sb.append(authen + "需要身份验证");
                } else {
                    sb.append(authen + "不允许任何人申请加入");
                }
            } else if (field.getKey() == TeamFieldEnum.Extension) {
                sb.append("群扩展字段被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.Ext_Server) {
                sb.append("群扩展字段(服务器)被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.ICON) {
                sb.append("群头像已更新");
            } else if (field.getKey() == TeamFieldEnum.InviteMode) {
                sb.append("群邀请他人权限被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.TeamUpdateMode) {
                sb.append("群资料修改权限被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.BeInviteMode) {
                sb.append("群被邀请人身份验证权限被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.TeamExtensionUpdateMode) {
                sb.append("群扩展字段修改权限被更新为 " + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.AllMute) {
                TeamAllMuteModeEnum teamAllMuteModeEnum = (TeamAllMuteModeEnum) field.getValue();
                if (teamAllMuteModeEnum == TeamAllMuteModeEnum.Cancel) {
                    sb.append("取消群全员禁言");
                } else {
                    sb.append("群全员禁言");
                }
            } else {
                sb.append("群" + field.getKey() + "被更新为 " + field.getValue());
            }
            sb.append("\r\n");
        }
        if (sb.length() < 2) {
            return "未知通知";
        }
        return sb.delete(sb.length() - 2, sb.length()).toString();
    }

    public static String buildDismissTeamNotification(String teamId, String fromAccount) {
        return getTeamMemberDisplayNameWithYou(teamId, fromAccount) + " 解散了群";
    }

    public static String buildLeaveTeamNotification(String teamId, String fromAccount) {
        String tip;
        Team team = NimTeamSDK.queryTeamBlock(teamId);
        if (team.getType() == TeamTypeEnum.Advanced || team.getType() == TeamTypeEnum.Normal) {
            tip = " 离开了群";
        } else {
            tip = " 离开了讨论组";
        }
        return getTeamMemberDisplayNameWithYou(teamId, fromAccount) + tip;
    }

    public static String buildKickMemberNotification(MemberChangeAttachment na, String teamId, String fromAccount) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildMemberListString(na.getTargets(), teamId, null));
        Team team = NimTeamSDK.queryTeamBlock(teamId);
        if (team.getType() == TeamTypeEnum.Advanced || team.getType() == TeamTypeEnum.Normal) {
            sb.append(" 已被移出群");
        } else {
            sb.append(" 已被移出讨论组");
        }

        return sb.toString();
    }

    public static String buildInviteMemberNotification(MemberChangeAttachment na, String teamId, String fromAccount) {
        StringBuilder sb = new StringBuilder();
        String selfName = getTeamMemberDisplayNameWithYou(teamId, fromAccount);

        sb.append(selfName);
        sb.append(" 邀请 ");
        sb.append(buildMemberListString(na.getTargets(), teamId, fromAccount));
        Team team = NimTeamSDK.queryTeamBlock(teamId);
        if (team.getType() == TeamTypeEnum.Advanced || team.getType() == TeamTypeEnum.Normal) {
            sb.append(" 加入了群聊");
        } else {
            sb.append(" 加入讨论组");
        }
        return sb.toString();
    }
    /*================== 通知相关 end ==================*/


    /*================== 扩展字段解析 start ==================*/
    public static void setShouldShowNickName(String teamId, boolean shouldShowNickName, RequestCallback<Void> callback) {
        TeamMember member = queryTeamMemberBlock(teamId, UserCache.getAccount());
        if (member != null) {
            Map<String, Object> ext = member.getExtension();
            ext.put(AppConst.MyTeamMemberExt.SHOULD_SHOW_NICK_NAME, shouldShowNickName);
            NimTeamSDK.updateMyMemberExtension(teamId, ext, callback);
        }
    }

    public static boolean shouldShowNickName(String teamId) {
        TeamMember member = queryTeamMemberBlock(teamId, UserCache.getAccount());
        if (member == null)
            return false;
        Map<String, Object> ext = member.getExtension();
        Object o = ext.get(AppConst.MyTeamMemberExt.SHOULD_SHOW_NICK_NAME);
        if (o == null)
            return false;
        boolean shouldShowNickName = (boolean) o;
        return shouldShowNickName;
    }
    /*================== 扩展字段解析 end ==================*/
}


