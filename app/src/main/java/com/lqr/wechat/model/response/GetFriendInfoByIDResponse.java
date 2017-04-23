package com.lqr.wechat.model.response;

/**
 * Created by wangmingqiang on 16/9/11.
 * Company RongCloud
 */

public class GetFriendInfoByIDResponse {
    private int code;
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


        private String displayName;
        private UserEntity user;


        public void setdisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public String getdisplayName() {
            return displayName;
        }

        public UserEntity getUser() {
            return user;
        }

        public static class UserEntity {
            private String id;
            private String nickname;
            private String region;
            private String phone;
            private String portraitUri;

            public void setId(String id) {
                this.id = id;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public void setPhone(String phone) {
                this.phone = phone;
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

            public String getRegion() {
                return region;
            }

            public String getPhone() {
                return phone;
            }

            public String getPortraitUri() {
                return portraitUri;
            }
        }
    }
}
