package com.lqr.wechat.model.response;

import com.lqr.wechat.util.PinyinUtils;

import java.util.List;

/**
 * Created by AMing on 16/1/7.
 * Company RongCloud
 */
public class UserRelationshipResponse {

    /**
     * code : 200
     * result : [{"displayName":"","message":"手机号:18622222222昵称:的用户请求添加你为好友","status":11,"updatedAt":"2016-01-07T06:22:55.000Z","user":{"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}}]
     */

    private int code;
    /**
     * displayName :
     * message : 手机号:18622222222昵称:的用户请求添加你为好友
     * status : 11
     * updatedAt : 2016-01-07T06:22:55.000Z
     * user : {"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}
     */

    private List<ResultEntity> result;

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public static class ResultEntity implements Comparable {

        public ResultEntity(String displayName, String message, int status, String updatedAt, UserEntity user) {
            this.displayName = displayName;
            this.message = message;
            this.status = status;
            this.updatedAt = updatedAt;
            this.user = user;
            this.mPinyin = PinyinUtils.getPinyin(displayName);
        }

        public ResultEntity() {

        }

        private String displayName;
        private String message;
        private int status;
        private String updatedAt;
        private String mPinyin;
        /**
         * id : i3gRfA1ml
         * nickname : nihaoa
         * portraitUri :
         */

        private UserEntity user;

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public UserEntity getUser() {
            return user;
        }

        public String getPinyin() {
            return mPinyin;
        }

        public void setPinyin(String pinyin) {
            mPinyin = pinyin;
        }

        @Override
        public int compareTo(Object another) {
            return 0;
        }

        public static class UserEntity {
            private String id;
            private String nickname;
            private String portraitUri;

            public void setId(String id) {
                this.id = id;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setPortraitUri(String portraitUri) {
                this.portraitUri = portraitUri;
            }

            public String getId() {
                return id;
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

