package com.lqr.wechat.db;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

import com.lqr.wechat.api.ApiRetrofit;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.db.model.Friend;
import com.lqr.wechat.db.model.GroupMember;
import com.lqr.wechat.db.model.Groups;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.model.response.GetGroupInfoResponse;
import com.lqr.wechat.model.response.GetGroupMemberResponse;
import com.lqr.wechat.model.response.GetGroupResponse;
import com.lqr.wechat.model.response.UserRelationshipResponse;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.NetUtils;
import com.lqr.wechat.util.PinyinUtils;
import com.lqr.wechat.util.RongGenerate;
import com.lqr.wechat.util.UIUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import io.rong.imlib.model.UserInfo;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * @创建者 GitLqr
 * @描述 数据库管理器
 */
public class DBManager {

    private static DBManager mInstance;
    //Written on io threads and read from wherever getGroups/getGroupMember are called
    private volatile boolean mHasFetchedFriends = false;
    private volatile boolean mHasFetchedGroups = false;
    private volatile boolean mHasFetchedGroupMembers = false;
    private LinkedHashMap<String, UserInfo> mUserInfoCache;
    private Subscription mFetchSubscription;

    public DBManager() {
        mUserInfoCache = new LinkedHashMap<>();
    }

    public static DBManager getInstance() {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 登录时同步好友，群组，群组成员，黑名单数据
     */
    public void getAllUserInfo() {
        if (!NetUtils.isNetworkAvailable(UIUtils.getContext())) {
            //MainActivity waits for FETCH_COMPLETE behind a non-cancelable dialog
            onFetchComplete(false);
            return;
        }
        mHasFetchedFriends = false;
        mHasFetchedGroups = false;
        mHasFetchedGroupMembers = false;
        //zip waits for both parts, and the group part only ends after every group's members are saved,
        //so FETCH_COMPLETE goes out exactly once
        mFetchSubscription = Observable.zip(fetchFriends().subscribeOn(Schedulers.io()),
                fetchGroups().subscribeOn(Schedulers.io()),
                (friendsOk, groupsOk) -> friendsOk && groupsOk)
                .subscribe(this::onFetchComplete, throwable -> {
                    loadError(throwable);
                    onFetchComplete(false);
                });
    }

    /**
     * 同步朋友信息
     *
     * @return emits whether the sync succeeded; errors are turned into false
     */
    private Observable<Boolean> fetchFriends() {
        return ApiRetrofit.getInstance().getAllUserRelationship()
                .map(userRelationshipResponse -> {
                    if (userRelationshipResponse == null || userRelationshipResponse.getCode() != 200) {
                        return false;
                    }
                    List<UserRelationshipResponse.ResultEntity> list = userRelationshipResponse.getResult();
                    if (list != null && list.size() > 0) {
                        deleteFriends();
                        saveFriends(list);
                    }
                    return true;
                })
                .onErrorReturn(this::fetchError)
                .doOnNext(success -> mHasFetchedFriends = true);
    }

    /**
     * Sync the groups, then the members of every group
     *
     * @return emits whether the sync succeeded; errors are turned into false
     */
    private Observable<Boolean> fetchGroups() {
        return ApiRetrofit.getInstance().getGroups()
                .flatMap(getGroupResponse -> {
                    if (getGroupResponse == null || getGroupResponse.getCode() != 200) {
                        return Observable.just(false);
                    }
                    List<GetGroupResponse.ResultEntity> list = getGroupResponse.getResult();
                    if (list == null || list.size() == 0) {
                        return Observable.just(true);
                    }
                    deleteGroups();
                    saveGroups(list);
                    //Set before the members are done, so a group notice during the member sync
                    //only refreshes that group instead of starting another full group sync
                    mHasFetchedGroups = true;
                    //同步群组成员信息
                    return fetchGroupMembers();
                })
                .onErrorReturn(this::fetchError)
                .doOnNext(success -> {
                    mHasFetchedGroups = true;
                    mHasFetchedGroupMembers = true;
                });
    }

    /**
     * 同步群组成员信息
     *
     * @return emits once after all groups are done, true only if every group succeeded
     */
    private Observable<Boolean> fetchGroupMembers() {
        //defer: read the groups from the db when subscribed (on io), not when the chain is built
        return Observable.defer(() -> Observable.from(getGroups()))
                .concatMap(groups -> ApiRetrofit.getInstance().getGroupMember(groups.getGroupId())
                        .map(getGroupMemberResponse -> {
                            if (getGroupMemberResponse == null || getGroupMemberResponse.getCode() != 200) {
                                return false;
                            }
                            List<GetGroupMemberResponse.ResultEntity> list = getGroupMemberResponse.getResult();
                            if (list != null && list.size() > 0) {
                                deleteGroupMembersByGroupId(groups.getGroupId());
                                saveGroupMembers(list, groups.getGroupId());
                            }
                            return true;
                        })
                        .onErrorReturn(this::fetchError))
                .toList()
                .map(results -> !results.contains(false))
                .doOnNext(success -> mHasFetchedGroupMembers = true);
    }

    private boolean fetchError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
        return false;
    }

    private void onFetchComplete(boolean success) {
        BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.FETCH_COMPLETE, success ? "" : AppConst.FETCH_FAILED);
        notifyFetchedDataChanged();
    }

    private void notifyFetchedDataChanged() {
        BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_FRIEND);
        BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_GROUP);
        BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
    }

    public void getGroups(String groupId) {
        if (!mHasFetchedGroups) {
            fetchGroups()
                    .subscribeOn(Schedulers.io())
                    .subscribe(success -> notifyFetchedDataChanged(), this::loadError);
        } else {
            ApiRetrofit.getInstance().getGroupInfo(groupId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(getGroupInfoResponse -> {
                        if (getGroupInfoResponse != null && getGroupInfoResponse.getCode() == 200) {
                            GetGroupInfoResponse.ResultEntity groupInfo = getGroupInfoResponse.getResult();
                            if (groupInfo != null) {
                                String role = groupInfo.getCreatorId().equalsIgnoreCase(UserCache.getId()) ? "0" : "1";
                                saveOrUpdateGroup(new Groups(groupId, groupInfo.getName(), groupInfo.getPortraitUri(), role));
                            }
                        }
                    }, this::loadError);
        }
    }


    public void getGroupMember(String groupId) {
        if (!mHasFetchedGroupMembers) {
            deleteGroupMembers();
            fetchGroupMembers()
                    .subscribeOn(Schedulers.io())
                    .subscribe(success -> notifyFetchedDataChanged(), this::loadError);
        } else {
            ApiRetrofit.getInstance().getGroupMember(groupId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(getGroupMemberResponse -> {
                        if (getGroupMemberResponse != null && getGroupMemberResponse.getCode() == 200) {
                            List<GetGroupMemberResponse.ResultEntity> list = getGroupMemberResponse.getResult();
                            if (list != null && list.size() > 0) {
                                deleteGroupMembersByGroupId(groupId);
                                saveGroupMembers(list, groupId);
                                BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_GROUP_MEMBER, groupId);
                                BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                            }
                        }
                    }, this::loadError);
        }

    }

    private void loadError(Throwable throwable) {
        LogUtils.sf(throwable.getLocalizedMessage());
    }

    /**
     * 查询本地用户信息
     * 1、查缓存
     * 2、查Friend表
     * 3、查GroupMember表
     *
     * @param userid
     * @return
     */
    public UserInfo getUserInfo(String userid) {
        if (TextUtils.isEmpty(userid))
            return null;

        if (mUserInfoCache != null) {
            UserInfo userInfo = mUserInfoCache.get(userid);
            if (userInfo != null) {
                return userInfo;
            }
        }

        UserInfo userInfo;
        Friend friend = getFriendById(userid);
        if (friend != null) {
            String name = friend.getName();
            if (friend.isExitsDisplayName()) {
                name = friend.getDisplayName();
            }
            userInfo = new UserInfo(friend.getUserId(), name, parsePortraitUri(friend.getPortraitUri()));
            return userInfo;
        }

        List<GroupMember> groupMembers = getGroupMembersWithUserId(userid);
        if (groupMembers != null && groupMembers.size() > 0) {
            GroupMember groupMember = groupMembers.get(0);
            userInfo = new UserInfo(groupMember.getUserId(), groupMember.getName(), parsePortraitUri(groupMember.getPortraitUri()));
            return userInfo;
        }

        return null;
    }

    /**
     * 清除所有用户数据
     */
    public synchronized void deleteAllUserInfo() {
        //Stop a running sync first so it does not write this account's data back after the delete;
        //unsubscribing also cancels the in-flight Retrofit call (a save already running may still finish)
        if (mFetchSubscription != null) {
            mFetchSubscription.unsubscribe();
            mFetchSubscription = null;
        }
//        deleteFriends();
//        deleteGroups();
//        deleteGroupMembers();
        DataSupport.deleteAll(Friend.class);
        DataSupport.deleteAll(GroupMember.class);
        DataSupport.deleteAll(Groups.class);
        //TODO:删除黑名单数据
        //The in-memory state outlives the account, so the next account would still see this one's data
        mUserInfoCache.clear();
        mHasFetchedFriends = false;
        mHasFetchedGroups = false;
        mHasFetchedGroupMembers = false;
    }

    public boolean isMyFriend(String userid) {
        Friend friend = getFriendById(userid);
        if (friend != null) {
            return true;
        }
        return false;
    }

    public boolean isMe(String userid) {
        if (UserCache.getId().equalsIgnoreCase(userid)) {
            return true;
        }
        return false;
    }

    public boolean isInThisGroup(String groupId) {
        Groups groups = getGroupsById(groupId);
        return groups != null;
    }
    /*================== Friend begin ==================*/

    public synchronized void saveOrUpdateFriend(Friend friend) {
        if (friend != null) {
            String portrait = friend.getPortraitUri();
            if (TextUtils.isEmpty(portrait)) {
                portrait = RongGenerate.generateDefaultAvatar(friend.getName(), friend.getUserId());
                friend.setPortraitUri(portrait);
            }
            friend.saveOrUpdate("userid = ?", friend.getUserId());
            //更新过本地好友数据后，清空内存中对应用户信息缓存
            if (mUserInfoCache != null && mUserInfoCache.containsKey(friend.getUserId())) {
                mUserInfoCache.remove(friend.getUserId());
            }
        }
    }

    public synchronized void deleteFriend(Friend friend) {
        DataSupport.deleteAll(Friend.class, "userid = ?", friend.getUserId());
    }

    public synchronized Friend getFriendById(String userid) {
        if (!TextUtils.isEmpty(userid)) {
            List<Friend> friends = DataSupport.where("userid = ?", userid).find(Friend.class);
            if (friends != null && friends.size() > 0)
                return friends.get(0);
        }
        return null;
    }

    public synchronized List<Friend> getFriends() {
//        return DataSupport.findAll(Friend.class);
        return DataSupport.where("userid != ?", UserCache.getId()).find(Friend.class);
    }

    public synchronized void saveFriends(List<UserRelationshipResponse.ResultEntity> list) {
        List<Friend> friends = new ArrayList<>();
        for (UserRelationshipResponse.ResultEntity entity : list) {
            if (entity.getStatus() == 20) {//已经是好友
                Friend friend = new Friend(
                        entity.getUser().getId(),
                        entity.getUser().getNickname(),
                        entity.getUser().getPortraitUri(),
                        TextUtils.isEmpty(entity.getDisplayName()) ? entity.getUser().getNickname() : entity.getDisplayName(),
                        null, null, null, null,
                        PinyinUtils.getPinyin(entity.getUser().getNickname()),
                        PinyinUtils.getPinyin(TextUtils.isEmpty(entity.getDisplayName()) ? entity.getUser().getNickname() : entity.getDisplayName())
                );
                if (TextUtils.isEmpty(friend.getPortraitUri())) {
                    friend.setPortraitUri(getPortrait(friend));
                }
                friends.add(friend);
            }
        }
        if (friends != null && friends.size() > 0)
            DataSupport.saveAll(friends);
    }

    public synchronized void deleteFriends() {
        List<Friend> friends = getFriends();
        for (Friend friend : friends) {
            friend.delete();
        }
    }

    public synchronized void deleteFriendById(String friendId) {
        DataSupport.deleteAll(Friend.class, "userid = ?", friendId);
    }

    /*==================Friend end ==================*/

    /*================== Groups start ==================*/
    public synchronized void saveOrUpdateGroup(Groups groups) {
        if (groups != null) {
            String portrait = groups.getPortraitUri();
            if (TextUtils.isEmpty(portrait)) {
                portrait = RongGenerate.generateDefaultAvatar(groups.getName(), groups.getGroupId());
                groups.setPortraitUri(portrait);
            }
            groups.saveOrUpdate("groupid = ?", groups.getGroupId());
        }
    }

    public synchronized void deleteGroup(Groups groups) {
        DataSupport.deleteAll(Groups.class, "groupid = ?", groups.getGroupId());
    }

    public synchronized Groups getGroupsById(String groupId) {
        if (!TextUtils.isEmpty(groupId)) {
            List<Groups> groupses = DataSupport.where("groupid = ?", groupId).find(Groups.class);
            if (groupses != null && groupses.size() > 0) {
                return groupses.get(0);
            }
        }
        return null;
    }

    public synchronized List<Groups> getGroups() {
        return DataSupport.findAll(Groups.class);
    }

    public synchronized void saveGroups(List<GetGroupResponse.ResultEntity> list) {
        List<Groups> groupsList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (GetGroupResponse.ResultEntity groups : list) {
                String portrait = groups.getGroup().getPortraitUri();
                if (TextUtils.isEmpty(portrait)) {
                    portrait = RongGenerate.generateDefaultAvatar(groups.getGroup().getName(), groups.getGroup().getId());
                }
                groupsList.add(new Groups(groups.getGroup().getId(), groups.getGroup().getName(), portrait, String.valueOf(groups.getRole())));
            }
        }
        if (groupsList.size() > 0)
            DataSupport.saveAll(groupsList);
    }

    public synchronized void deleteGroups() {
        DataSupport.deleteAll(Groups.class);
    }

    public synchronized void deleteGroupsById(String groupId) {
        DataSupport.deleteAll(Groups.class, "groupid = ?", groupId);
    }

    /*================== Groups end ==================*/
    /*================== GroupMember start ==================*/
    public synchronized void saveOrUpdateGroupMember(GroupMember groupMember) {
        if (groupMember != null) {
            String portrait = groupMember.getPortraitUri();
            if (TextUtils.isEmpty(portrait)) {
                portrait = RongGenerate.generateDefaultAvatar(groupMember.getName(), groupMember.getUserId());
                groupMember.setPortraitUri(portrait);
            }
            groupMember.saveOrUpdate("groupid = ? and userid = ?", groupMember.getGroupId(), groupMember.getUserId());
        }
    }

    public synchronized void updateGroupMemberPortraitUri(String userId, String portraitUri) {
        if (TextUtils.isEmpty(portraitUri))
            return;
        ContentValues values = new ContentValues();
        values.put("portraituri", portraitUri);
        DataSupport.updateAll(GroupMember.class, values, "userid = ?", userId);
    }

    public synchronized List<GroupMember> getGroupMembers(String groupId) {
        return DataSupport.where("groupid = ?", groupId).find(GroupMember.class);
    }

    public synchronized List<GroupMember> getGroupMembersWithUserId(String userId) {
        if (TextUtils.isEmpty(userId))
            return null;
        return DataSupport.where("userid = ?", userId).find(GroupMember.class);
    }

    public synchronized void saveGroupMembers(List<GetGroupMemberResponse.ResultEntity> list, String groupId) {
        if (list != null && list.size() > 0) {
            List<GroupMember> groupMembers = setCreatedToTop(list, groupId);
            if (groupMembers != null && groupMembers.size() > 0) {
                for (GroupMember groupMember : groupMembers) {
                    if (groupMember != null && TextUtils.isEmpty(groupMember.getPortraitUri())) {
                        String portrait = getPortrait(groupMember);
                        groupMember.setPortraitUri(portrait);
                    }
                }
                if (groupMembers.size() > 0) {
                    for (GroupMember groupMember : groupMembers) {
                        saveOrUpdateGroupMember(groupMember);
                    }
                }
            }
        }
    }

    public synchronized void updateGroupsName(String groupId, String groupName) {
        Groups groups = getGroupsById(groupId);
        if (groups != null) {
            groups.setName(groupName);
            saveOrUpdateGroup(groups);
        }
    }

    public synchronized void deleteGroupMembers() {
        DataSupport.deleteAll(GroupMember.class);
    }

    public synchronized void deleteGroupMembers(String groupId, List<String> kickedUserIds) {
        if (kickedUserIds != null && kickedUserIds.size() > 0) {
            for (String userId : kickedUserIds) {
                DataSupport.deleteAll(GroupMember.class, "groupid = ? and userid = ?", groupId, userId);
            }
            BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_GROUP_MEMBER, groupId);
            BroadcastManager.getInstance(UIUtils.getContext()).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
        }
    }

    public synchronized void deleteGroupMembersByGroupId(String groupId) {
        DataSupport.deleteAll(GroupMember.class, "groupid = ?", groupId);
    }

    private synchronized List<GroupMember> setCreatedToTop(List<GetGroupMemberResponse.ResultEntity> groupMember, String groupId) {
        List<GroupMember> newList = new ArrayList<>();
        GroupMember created = null;
        for (GetGroupMemberResponse.ResultEntity group : groupMember) {
            String groupName = null;
            String groupPortraitUri = null;
            Groups groups = getGroupsById(groupId);
            if (groups != null) {
                groupName = groups.getName();
                groupPortraitUri = groups.getPortraitUri();
            }
            GroupMember newMember = new GroupMember(groupId,
                    group.getUser().getId(),
                    group.getUser().getNickname(),
                    group.getUser().getPortraitUri(),
                    group.getDisplayName(),
                    PinyinUtils.getPinyin(group.getUser().getNickname()),
                    PinyinUtils.getPinyin(group.getDisplayName()),
                    groupName,
                    PinyinUtils.getPinyin(groupName),
                    groupPortraitUri);
            if (group.getRole() == 0) {
                created = newMember;
            } else {
                newList.add(newMember);
            }
        }
        if (created != null) {
            newList.add(created);
        }
        Collections.reverse(newList);
        return newList;
    }

    /*================== GroupMember end ==================*/

    /**
     * app中获取用户头像的接口
     * 这个方法不涉及读数据库,头像空时直接生成默认头像
     */
    public String getPortraitUri(UserInfo userInfo) {
        if (userInfo != null) {
            if (userInfo.getPortraitUri() != null) {
                if (TextUtils.isEmpty(userInfo.getPortraitUri().toString())) {
                    if (userInfo.getName() != null) {
                        return RongGenerate.generateDefaultAvatar(userInfo);
                    } else {
                        return null;
                    }
                } else {
                    return userInfo.getPortraitUri().toString();
                }
            } else {
                if (userInfo.getName() != null) {
                    return RongGenerate.generateDefaultAvatar(userInfo);
                } else {
                    return null;
                }
            }

        }
        return null;
    }

    public String getPortraitUri(String name, String userId) {
        return RongGenerate.generateDefaultAvatar(name, userId);
    }

//    public String getPortraitUri(UserInfoBean bean) {
//        if (bean != null) {
//            if (bean.getPortraitUri() != null) {
//                if (TextUtils.isEmpty(bean.getPortraitUri().toString())) {
//                    if (bean.getName() != null) {
//                        return RongGenerate.generateDefaultAvatar(bean.getName(), bean.getUserId());
//                    } else {
//                        return null;
//                    }
//                } else {
//                    return bean.getPortraitUri().toString();
//                }
//            } else {
//                if (bean.getName() != null) {
//                    return RongGenerate.generateDefaultAvatar(bean.getName(), bean.getUserId());
//                } else {
//                    return null;
//                }
//            }
//
//        }
//        return null;
//    }
//
//    public String getPortraitUri(GetGroupInfoResponse groupInfoResponse) {
//        if (groupInfoResponse.getResult() != null) {
//            Groups groups = new Groups(groupInfoResponse.getResult().getId(),
//                    groupInfoResponse.getResult().getName(),
//                    groupInfoResponse.getResult().getPortraitUri());
//            return getPortraitUri(groups);
//        }
//        return null;
//    }
//

    /**
     * Uri.parse(null) throws; an empty Uri keeps callers' getPortraitUri().toString() working
     * and lets getPortraitUri(UserInfo) generate the default avatar
     */
    private static Uri parsePortraitUri(String portraitUri) {
        return Uri.parse(portraitUri == null ? "" : portraitUri);
    }

    private static String portraitUriToString(UserInfo userInfo) {
        return userInfo.getPortraitUri() == null ? "" : userInfo.getPortraitUri().toString();
    }

    /**
     * 获取用户头像,头像为空时会生成默认的头像,此默认头像可能已经存在数据库中,不重新生成
     * 先从缓存读,再从数据库读
     */
    private String getPortrait(Friend friend) {
        if (friend != null) {
            if (TextUtils.isEmpty(friend.getPortraitUri())) {
                if (TextUtils.isEmpty(friend.getUserId())) {
                    return null;
                } else {
                    UserInfo userInfo = mUserInfoCache.get(friend.getUserId());
                    if (userInfo != null) {
                        String cachedPortrait = portraitUriToString(userInfo);
                        if (!TextUtils.isEmpty(cachedPortrait)) {
                            return cachedPortrait;
                        } else {
                            mUserInfoCache.remove(friend.getUserId());
                        }
                    }
//                    List<GroupMember> groupMemberList = getGroupMembersWithUserId(friend.getUserId());
//                    if (groupMemberList != null && groupMemberList.size() > 0) {
//                        GroupMember groupMember = groupMemberList.get(0);
//                        if (!TextUtils.isEmpty(groupMember.getPortraitUri().toString()))
//                            return groupMember.getPortraitUri().toString();
//                    }
                    String portrait = RongGenerate.generateDefaultAvatar(friend.getName(), friend.getUserId());
                    //缓存信息kit会使用,备注名存在时需要缓存displayName
                    String name = friend.getName();
                    if (friend.isExitsDisplayName()) {
                        name = friend.getDisplayName();
                    }
                    userInfo = new UserInfo(friend.getUserId(), name, Uri.parse(portrait));
                    mUserInfoCache.put(friend.getUserId(), userInfo);
                    return portrait;
                }
            } else {
                return friend.getPortraitUri();
            }
        }
        return null;
    }

    private String getPortrait(GroupMember groupMember) {
        if (groupMember != null) {
            if (TextUtils.isEmpty(groupMember.getPortraitUri())) {
                if (TextUtils.isEmpty(groupMember.getUserId())) {
                    return null;
                } else {
                    UserInfo userInfo = mUserInfoCache.get(groupMember.getUserId());
                    if (userInfo != null) {
                        String cachedPortrait = portraitUriToString(userInfo);
                        if (!TextUtils.isEmpty(cachedPortrait)) {
                            return cachedPortrait;
                        } else {
                            mUserInfoCache.remove(groupMember.getUserId());
                        }
                    }
                    Friend friend = getFriendById(groupMember.getUserId());
                    if (friend != null) {
                        if (!TextUtils.isEmpty(friend.getPortraitUri())) {
                            return friend.getPortraitUri();
                        }
                    }
                    List<GroupMember> groupMemberList = getGroupMembersWithUserId(groupMember.getUserId());
                    if (groupMemberList != null && groupMemberList.size() > 0) {
                        GroupMember member = groupMemberList.get(0);
                        if (!TextUtils.isEmpty(member.getPortraitUri())) {
                            return member.getPortraitUri();
                        }
                    }
                    String portrait = RongGenerate.generateDefaultAvatar(groupMember.getName(), groupMember.getUserId());
                    userInfo = new UserInfo(groupMember.getUserId(), groupMember.getName(), Uri.parse(portrait));
                    mUserInfoCache.put(groupMember.getUserId(), userInfo);
                    return portrait;
                }
            } else {
                return groupMember.getPortraitUri();
            }
        }
        return null;
    }

}
