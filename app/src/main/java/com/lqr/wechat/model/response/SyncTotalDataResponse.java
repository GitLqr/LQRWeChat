package com.lqr.wechat.model.response;

import java.util.List;

/**
 * Created by AMing on 16/9/1.
 * Company RongCloud
 */
public class SyncTotalDataResponse {


    /**
     * code : 200
     * result : {"version":1472796005679,"user":{"id":4725,"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP","timestamp":1471234299530},"blacklist":null,"friends":[{"friendId":16,"displayName":"","status":20,"timestamp":1471237611667},{"friendId":18,"displayName":"","status":10,"timestamp":1472116941839},{"friendId":19,"displayName":"","status":20,"timestamp":1472451436795},{"friendId":21,"displayName":"","status":20,"timestamp":1471856298683},{"friendId":32,"displayName":"","status":20,"timestamp":1471258732638},{"friendId":34,"displayName":"","status":20,"timestamp":1471273460917},{"friendId":38,"displayName":"","status":20,"timestamp":1471840668284},{"friendId":39,"displayName":"","status":11,"timestamp":1472551947837},{"friendId":42,"displayName":"","status":20,"timestamp":1472116024106},{"friendId":49,"displayName":"","status":20,"timestamp":1471420438165},{"friendId":668,"displayName":"","status":20,"timestamp":1471918471909},{"friendId":1771,"displayName":"","status":20,"timestamp":1471513368257},{"friendId":1939,"displayName":"","status":20,"timestamp":1471237906799},{"friendId":1949,"displayName":"","status":20,"timestamp":1471247596687},{"friendId":1986,"displayName":"","status":20,"timestamp":1471275372296},{"friendId":2899,"displayName":"","status":20,"timestamp":1472114462948},{"friendId":3858,"displayName":"","status":20,"timestamp":1472010879425},{"friendId":4582,"displayName":"","status":20,"timestamp":1471264518387},{"friendId":4955,"displayName":"","status":20,"timestamp":1471833331872}],"groups":[{"displayName":"","role":1,"isDeleted":false,"group":{"id":472,"name":"大融云","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/Tp6nLyUKX1466570117209114014","timestamp":1472796005679}},{"displayName":"","role":1,"isDeleted":false,"group":{"id":2,"name":"产品研发部","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/Uz6Sw8GXx1466575289048886963","timestamp":1472544772360}},{"displayName":"","role":1,"isDeleted":false,"group":{"id":908,"name":"Android工作组","portraitUri":"","timestamp":1471766602484}},{"displayName":"","role":1,"isDeleted":false,"group":{"id":738,"name":"移动端研发","portraitUri":"","timestamp":1471766395907}},{"displayName":"","role":0,"isDeleted":false,"group":{"id":1695,"name":"会话置顶","portraitUri":"","timestamp":1472461369663}},{"displayName":"","role":1,"isDeleted":false,"group":{"id":1737,"name":"Test","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/1wqmFbjA11472642497997760010","timestamp":1472642498356}}],"group_members":[{"groupId":472,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1471256883504,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":2,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1471256988955,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":908,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1471257007807,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":738,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1471257025066,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":1695,"memberId":4725,"displayName":"","role":0,"isDeleted":false,"timestamp":1472461369663,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":1737,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1472639070700,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}}]}
     */

    private int code;
    /**
     * version : 1472796005679
     * user : {"id":4725,"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP","timestamp":1471234299530}
     * blacklist : null
     * friends : [{"friendId":16,"displayName":"","status":20,"timestamp":1471237611667},{"friendId":18,"displayName":"","status":10,"timestamp":1472116941839},{"friendId":19,"displayName":"","status":20,"timestamp":1472451436795},{"friendId":21,"displayName":"","status":20,"timestamp":1471856298683},{"friendId":32,"displayName":"","status":20,"timestamp":1471258732638},{"friendId":34,"displayName":"","status":20,"timestamp":1471273460917},{"friendId":38,"displayName":"","status":20,"timestamp":1471840668284},{"friendId":39,"displayName":"","status":11,"timestamp":1472551947837},{"friendId":42,"displayName":"","status":20,"timestamp":1472116024106},{"friendId":49,"displayName":"","status":20,"timestamp":1471420438165},{"friendId":668,"displayName":"","status":20,"timestamp":1471918471909},{"friendId":1771,"displayName":"","status":20,"timestamp":1471513368257},{"friendId":1939,"displayName":"","status":20,"timestamp":1471237906799},{"friendId":1949,"displayName":"","status":20,"timestamp":1471247596687},{"friendId":1986,"displayName":"","status":20,"timestamp":1471275372296},{"friendId":2899,"displayName":"","status":20,"timestamp":1472114462948},{"friendId":3858,"displayName":"","status":20,"timestamp":1472010879425},{"friendId":4582,"displayName":"","status":20,"timestamp":1471264518387},{"friendId":4955,"displayName":"","status":20,"timestamp":1471833331872}]
     * groups : [{"displayName":"","role":1,"isDeleted":false,"group":{"id":472,"name":"大融云","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/Tp6nLyUKX1466570117209114014","timestamp":1472796005679}},{"displayName":"","role":1,"isDeleted":false,"group":{"id":2,"name":"产品研发部","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/Uz6Sw8GXx1466575289048886963","timestamp":1472544772360}},{"displayName":"","role":1,"isDeleted":false,"group":{"id":908,"name":"Android工作组","portraitUri":"","timestamp":1471766602484}},{"displayName":"","role":1,"isDeleted":false,"group":{"id":738,"name":"移动端研发","portraitUri":"","timestamp":1471766395907}},{"displayName":"","role":0,"isDeleted":false,"group":{"id":1695,"name":"会话置顶","portraitUri":"","timestamp":1472461369663}},{"displayName":"","role":1,"isDeleted":false,"group":{"id":1737,"name":"Test","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/1wqmFbjA11472642497997760010","timestamp":1472642498356}}]
     * group_members : [{"groupId":472,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1471256883504,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":2,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1471256988955,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":908,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1471257007807,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":738,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1471257025066,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":1695,"memberId":4725,"displayName":"","role":0,"isDeleted":false,"timestamp":1472461369663,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}},{"groupId":1737,"memberId":4725,"displayName":"","role":1,"isDeleted":false,"timestamp":1472639070700,"user":{"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}}]
     */

    private ResultEntity result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity {
        private long version;
        /**
         * id : 4725
         * nickname : 李峰
         * portraitUri : http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP
         * timestamp : 1471234299530
         */

        private UserEntity user;
        private Object blacklist;
        /**
         * friendId : 16
         * displayName :
         * status : 20
         * timestamp : 1471237611667
         */

        private List<FriendsEntity> friends;
        /**
         * displayName :
         * role : 1
         * isDeleted : false
         * group : {"id":472,"name":"大融云","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/Tp6nLyUKX1466570117209114014","timestamp":1472796005679}
         */

        private List<GroupsEntity> groups;
        /**
         * groupId : 472
         * memberId : 4725
         * displayName :
         * role : 1
         * isDeleted : false
         * timestamp : 1471256883504
         * user : {"nickname":"李峰","portraitUri":"http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP"}
         */

        private List<GroupMembersEntity> group_members;

        public void setVersion(long version) {
            this.version = version;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public void setBlacklist(Object blacklist) {
            this.blacklist = blacklist;
        }

        public void setFriends(List<FriendsEntity> friends) {
            this.friends = friends;
        }

        public void setGroups(List<GroupsEntity> groups) {
            this.groups = groups;
        }

        public void setGroup_members(List<GroupMembersEntity> group_members) {
            this.group_members = group_members;
        }

        public long getVersion() {
            return version;
        }

        public UserEntity getUser() {
            return user;
        }

        public Object getBlacklist() {
            return blacklist;
        }

        public List<FriendsEntity> getFriends() {
            return friends;
        }

        public List<GroupsEntity> getGroups() {
            return groups;
        }

        public List<GroupMembersEntity> getGroup_members() {
            return group_members;
        }

        public static class UserEntity {
            private int id;
            private String nickname;
            private String portraitUri;
            private long timestamp;

            public void setId(int id) {
                this.id = id;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setPortraitUri(String portraitUri) {
                this.portraitUri = portraitUri;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public int getId() {
                return id;
            }

            public String getNickname() {
                return nickname;
            }

            public String getPortraitUri() {
                return portraitUri;
            }

            public long getTimestamp() {
                return timestamp;
            }
        }

        public static class FriendsEntity {
            private int friendId;
            private String displayName;
            private int status;
            private long timestamp;

            public void setFriendId(int friendId) {
                this.friendId = friendId;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public int getFriendId() {
                return friendId;
            }

            public String getDisplayName() {
                return displayName;
            }

            public int getStatus() {
                return status;
            }

            public long getTimestamp() {
                return timestamp;
            }
        }

        public static class GroupsEntity {
            private String displayName;
            private int role;
            private boolean isDeleted;
            /**
             * id : 472
             * name : 大融云
             * portraitUri : http://7xogjk.com1.z0.glb.clouddn.com/Tp6nLyUKX1466570117209114014
             * timestamp : 1472796005679
             */

            private GroupEntity group;

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public void setRole(int role) {
                this.role = role;
            }

            public void setIsDeleted(boolean isDeleted) {
                this.isDeleted = isDeleted;
            }

            public void setGroup(GroupEntity group) {
                this.group = group;
            }

            public String getDisplayName() {
                return displayName;
            }

            public int getRole() {
                return role;
            }

            public boolean isIsDeleted() {
                return isDeleted;
            }

            public GroupEntity getGroup() {
                return group;
            }

            public static class GroupEntity {
                private int id;
                private String name;
                private String portraitUri;
                private long timestamp;

                public void setId(int id) {
                    this.id = id;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public void setPortraitUri(String portraitUri) {
                    this.portraitUri = portraitUri;
                }

                public void setTimestamp(long timestamp) {
                    this.timestamp = timestamp;
                }

                public int getId() {
                    return id;
                }

                public String getName() {
                    return name;
                }

                public String getPortraitUri() {
                    return portraitUri;
                }

                public long getTimestamp() {
                    return timestamp;
                }
            }
        }

        public static class GroupMembersEntity {
            private int groupId;
            private int memberId;
            private String displayName;
            private int role;
            private boolean isDeleted;
            private long timestamp;
            /**
             * nickname : 李峰
             * portraitUri : http://7xogjk.com1.z0.glb.clouddn.com/FhZKRkT7DInMbrqCSKX6NqIqHbEP
             */

            private UserEntity user;

            public void setGroupId(int groupId) {
                this.groupId = groupId;
            }

            public void setMemberId(int memberId) {
                this.memberId = memberId;
            }

            public void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            public void setRole(int role) {
                this.role = role;
            }

            public void setIsDeleted(boolean isDeleted) {
                this.isDeleted = isDeleted;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public void setUser(UserEntity user) {
                this.user = user;
            }

            public int getGroupId() {
                return groupId;
            }

            public int getMemberId() {
                return memberId;
            }

            public String getDisplayName() {
                return displayName;
            }

            public int getRole() {
                return role;
            }

            public boolean isIsDeleted() {
                return isDeleted;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public UserEntity getUser() {
                return user;
            }

            public static class UserEntity {
                private String nickname;
                private String portraitUri;

                public void setNickname(String nickname) {
                    this.nickname = nickname;
                }

                public void setPortraitUri(String portraitUri) {
                    this.portraitUri = portraitUri;
                }

                public String getNickname() {
                    return nickname;
                }

                public String getPortraitUri() {
                    return portraitUri;
                }
            }
        }
    }
}
