package com.lqr.wechat.model.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AMing on 16/1/27.
 * Company RongCloud
 */
public class GetGroupMemberResponse implements Serializable {

    private static final long serialVersionUID = -3972802951229254770L;
    /**
     * code : 200
     * result : [{"displayName":"","role":1,"createdAt":"2016-01-26T08:50:57.000Z","user":{"id":"6nx4DGtCu","nickname":"超时分辨率","portraitUri":"http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-20_79_1453284569?e=2147483647&token=CddrKW5AbOMQaDRwc3ReDNvo3-sL_SO1fSUBKV3H:k11JduqwbWD0UDsKl_SOhIDj1NY="}},{"displayName":"","role":0,"createdAt":"2016-01-26T08:50:57.000Z","user":{"id":"7w0UxC8IB","nickname":"赵哈哈","portraitUri":"http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-18_402_1453097818?e=2147483647&token=CddrKW5AbOMQaDRwc3ReDNvo3-sL_SO1fSUBKV3H:bymTynVuA6G1RMwoaV0LLLHcl4s="}},{"displayName":"","role":1,"createdAt":"2016-01-26T08:50:57.000Z","user":{"id":"tTLZwBEfM","nickname":"那边风景如画","portraitUri":"http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-20_208_1453276329?e=2147483647&token=livk5rb3__JZjCtEiMxXpQ8QscLxbNLehwhHySnX:wC8EyBWlFEQ0_8ZNCD-aVSZjq9o="}},{"displayName":"","role":1,"createdAt":"2016-01-26T08:50:57.000Z","user":{"id":"ktbmrlZxf","nickname":"大秦秦","portraitUri":"http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-18_583_1453097893?e=2147483647&token=livk5rb3__JZjCtEiMxXpQ8QscLxbNLehwhHySnX:pOv6vxKfUlW4ksyl8NVAf0x9R8k="}}]
     */

    private int code;
    /**
     * displayName :
     * role : 1
     * createdAt : 2016-01-26T08:50:57.000Z
     * user : {"id":"6nx4DGtCu","nickname":"超时分辨率","portraitUri":"http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-20_79_1453284569?e=2147483647&token=CddrKW5AbOMQaDRwc3ReDNvo3-sL_SO1fSUBKV3H:k11JduqwbWD0UDsKl_SOhIDj1NY="}
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

    public static class ResultEntity implements Serializable {
        private static final long serialVersionUID = -7540110593142872802L;
        private String displayName;
        private int role;
        private String createdAt;
        /**
         * id : 6nx4DGtCu
         * nickname : 超时分辨率
         * portraitUri : http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-20_79_1453284569?e=2147483647&token=CddrKW5AbOMQaDRwc3ReDNvo3-sL_SO1fSUBKV3H:k11JduqwbWD0UDsKl_SOhIDj1NY=
         */

        private UserEntity user;

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getRole() {
            return role;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public UserEntity getUser() {
            return user;
        }

        public static class UserEntity implements Serializable {
            private static final long serialVersionUID = -6304502679596557562L;
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
