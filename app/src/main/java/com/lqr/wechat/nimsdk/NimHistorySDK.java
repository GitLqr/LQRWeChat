package com.lqr.wechat.nimsdk;

import com.netease.nimlib.sdk.InvocationFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;

import java.util.List;

import static com.netease.nimlib.sdk.NIMClient.getService;

/**
 * @创建者 CSDN_LQR
 * @描述 历史记录相关
 */
public class NimHistorySDK {

    /**
     * SDK 提供了一个根据锚点查询本地消息历史记录的接口，根据提供的方向 (direct)，查询比 anchor 更老 (QUERY_OLD) 或更新 (QUERY_NEW) 的最靠近anchor 的 limit 条数据。调用者可使用 asc 参数指定结果排序规则，结果使用 time 作为排序字段。
     * 当进行首次查询时，锚点可以用使用 MessageBuilder#createEmptyMessage 接口生成。查询结果不包含锚点。
     *
     * @param anchor    IMMessage 查询锚点
     * @param direction QueryDirectionEnum 查询方向
     * @param limit     int 查询结果的条数限制
     * @param asc       boolean 查询结果的排序规则，如果为 true，结果按照时间升级排列，如果为 false，按照时间降序排列
     * @return 调用跟踪，可设置回调函数，接收查询结果
     */
    public static InvocationFuture<List<IMMessage>> queryMessageListEx(IMMessage anchor, QueryDirectionEnum direction, int limit, boolean asc) {
        return getService(MsgService.class).queryMessageListEx(anchor, direction, limit, asc);
    }

    /**
     * 根据起始、截止时间点以及查询方向从本地消息数据库中查询消息历史。<br>
     * 根据提供的方向 (direction)，以 anchor 为起始点，往前或往后查询由 anchor 到 toTime 之间靠近 anchor 的 limit 条消息。<br>
     * 查询范围由 toTime 和 limit 共同决定，以先到为准。如果到 toTime 之间消息大于 limit 条，返回 limit 条记录，如果小于 limit 条，返回实际条数。<br>
     * 查询结果排序规则：direction 为 QUERY_OLD 时 按时间降序排列，direction 为 QUERY_NEW 时按照时间升序排列。<br>
     * 注意：查询结果不包含锚点。
     *
     * @param anchor    查询锚点
     * @param toTime    查询截止时间，若方向为 QUERY_OLD，toTime 应小于 anchor.getTime()。方向为 QUERY_NEW，toTime 应大于 anchor.getTime() <br>
     * @param direction 查询方向
     * @param limit     查询结果的条数限制
     * @return 调用跟踪，可设置回调函数，接收查询结果
     */
    public static InvocationFuture<List<IMMessage>> queryMessageListExTime(IMMessage anchor, long toTime, QueryDirectionEnum direction, int limit) {
        return getService(MsgService.class).queryMessageListExTime(anchor, toTime, direction, limit);
    }

    /**
     * 通过uuid批量获取IMMessage(异步版本)
     */
    public static InvocationFuture<List<IMMessage>> queryMessageListByUuidAsync(List<String> uuids) {
        return getService(MsgService.class).queryMessageListByUuid(uuids);
    }

    /**
     * 通过uuid批量获取IMMessage(同步版本)
     */
    public static List<IMMessage> queryMessageListByUuidSync(List<String> uuids) {
        return getService(MsgService.class).queryMessageListByUuidBlock(uuids);
    }

    /**
     * 通过消息类型从本地消息数据库中查询消息历史。查询范围由 msgTypeEnum 参数和 anchor 的 sessionId 决定。该接口查询方向为从后往前。以锚点 anchor 作为起始点（不包含锚点），往前查询最多 limit 条消息。
     *
     * @param msgTypeEnum MsgTypeEnum 消息类型
     * @param anchor      IMMessage        搜索的消息锚点
     * @param limit       int               搜索结果的条数限制
     * @return
     */
    public static InvocationFuture<List<IMMessage>> queryMessageListByType(MsgTypeEnum msgTypeEnum, IMMessage anchor, int limit) {
        return NIMClient.getService(MsgService.class).queryMessageListByType(msgTypeEnum, anchor, limit);
    }

    /**
     * 按照关键字搜索聊天记录
     *
     * @param keyword      String 文本消息的搜索关键字
     * @param fromAccounts List<String> 消息说话者帐号列表，如果消息说话在该列表中，
     *                     那么无需匹配 keyword，对应的消息记录会直接加入搜索结果中。
     * @param anchor       IMMessage 搜索的消息锚点
     * @param limit        int 搜索结果的条数限制
     * @return 调用跟踪，可设置回调函数，接收查询结果
     */
    public static void searchMessageHistory(String keyword, List<String> fromAccounts, IMMessage anchor, int limit, RequestCallbackWrapper<List<IMMessage>> callback) {
        NIMClient.getService(MsgService.class).searchMessageHistory(keyword, fromAccounts, anchor, limit)
                .setCallback(callback);
    }

    /**
     * 根据时间点搜索消息历史
     *
     * @param keyword      文本消息的搜索关键字
     * @param fromAccounts 消息说话者帐号列表，如果消息说话在该列表中，那么无需匹配keyword，对应的消息记录会直接加入搜索结果集中。
     * @param time         查询范围时间点，比time小（从后往前查）
     * @param limit        搜索结果的条数限制
     * @return InvocationFuture
     */
    public static void searchAllMessageHistory(String keyword, List<String> fromAccounts, long time, int limit, RequestCallbackWrapper<List<IMMessage>> callback) {
        NIMClient.getService(MsgService.class).searchAllMessageHistory(keyword, fromAccounts, time, limit)
                .setCallback(callback);
    }

    /**
     * 删除单条消息
     */
    public static void deleteChattingHistory(IMMessage message) {
        NIMClient.getService(MsgService.class).deleteChattingHistory(message);
    }

    /**
     * 删除与某个聊天对象的全部消息记录
     */
    public static void clearChattingHistory(String account, SessionTypeEnum sessionType) {
        NIMClient.getService(MsgService.class).clearChattingHistory(account, sessionType);
    }

    /**
     * 从服务器拉取消息历史记录。
     *
     * @param anchor    IMMessage 起始时间点的消息，不能为 null。
     * @param toTime    long 结束时间点单位毫秒
     * @param limit     int 本次查询的消息条数上限(最多 100 条)
     * @param direction QueryDirectionEnum 查询方向，
     *                  QUERY_OLD 按结束时间点逆序查询，逆序排列；
     *                  QUERY_NEW 按起始时间点正序起查，正序排列
     * @param persist   boolean 通过该接口获取的漫游消息记录，要不要保存到本地消息数据库。
     * @return InvocationFuture
     */
    public static InvocationFuture<List<IMMessage>> pullMessageHistoryEx(IMMessage anchor, long toTime, int limit, QueryDirectionEnum direction, boolean persist) {
        return NIMClient.getService(MsgService.class).pullMessageHistoryEx(anchor, toTime, limit, direction, persist);
    }

    /**
     * 从服务器拉取消息历史记录。该接口查询方向为从后往前。以锚点 anchor 作为起始点（不包含锚点），
     * 往前查询最多 limit 条消息。
     *
     * @param anchor  IMMessage 查询锚点。
     * @param limit   int 本次查询的消息条数上限(最多 100 条)
     * @param persist boolean 通过该接口获取的漫游消息记录，要不要保存到本地消息数据库。
     * @return InvocationFuture
     */
    public static InvocationFuture<List<IMMessage>> pullMessageHistory(IMMessage anchor, int limit, boolean persist) {
        return NIMClient.getService(MsgService.class).pullMessageHistory(anchor, limit, persist);
    }

}
