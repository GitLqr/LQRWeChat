package com.lqr.wechat.model.response;

import java.util.List;

/**
 * Created by AMing on 16/3/4.
 * Company RongCloud
 */
public class GetBlackListResponse {

    /**
     * code : 200
     * result : [{"user":{"id":"Cz3bcYl2K","nickname":"android","portraitUri":"http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-29_596_1454056971?e=2147483647&token=CddrKW5AbOMQaDRwc3ReDNvo3-sL_SO1fSUBKV3H:rCO5hOO04fOock28pLnp4OGF1QM=","updatedAt":"2016-03-04T07:59:58.000Z"}}]
     */

    private int code;
    /**
     * user : {"id":"Cz3bcYl2K","nickname":"android","portraitUri":"http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-29_596_1454056971?e=2147483647&token=CddrKW5AbOMQaDRwc3ReDNvo3-sL_SO1fSUBKV3H:rCO5hOO04fOock28pLnp4OGF1QM=","updatedAt":"2016-03-04T07:59:58.000Z"}
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

    public static class ResultEntity {
        /**
         * id : Cz3bcYl2K
         * nickname : android
         * portraitUri : http://rongcloud-image.ronghub.com/image_jpeg__RC-0116-00-29_596_1454056971?e=2147483647&token=CddrKW5AbOMQaDRwc3ReDNvo3-sL_SO1fSUBKV3H:rCO5hOO04fOock28pLnp4OGF1QM=
         * updatedAt : 2016-03-04T07:59:58.000Z
         */

        private UserEntity user;

        public void setUser(UserEntity user) {
            this.user = user;
        }

        public UserEntity getUser() {
            return user;
        }

        public static class UserEntity {
            private String id;
            private String nickname;
            private String portraitUri;
            private String updatedAt;

            public void setId(String id) {
                this.id = id;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public void setPortraitUri(String portraitUri) {
                this.portraitUri = portraitUri;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
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

            public String getUpdatedAt() {
                return updatedAt;
            }
        }
    }
}
