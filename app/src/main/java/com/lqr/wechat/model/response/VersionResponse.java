package com.lqr.wechat.model.response;

/**
 * Created by AMing on 16/7/20.
 * Company RongCloud
 */
public class VersionResponse {


    /**
     * version : 1.0.5
     * build : 201607181821
     * url : https://dn-rongcloud.qbox.me/app.plist
     */

    private IosEntity ios;
    /**
     * version : 1.0.5
     * url : http://downloads.rongcloud.cn/SealTalk_by_RongCloud_Android_v1_0_5.apk
     */

    private AndroidEntity android;

    public void setIos(IosEntity ios) {
        this.ios = ios;
    }

    public void setAndroid(AndroidEntity android) {
        this.android = android;
    }

    public IosEntity getIos() {
        return ios;
    }

    public AndroidEntity getAndroid() {
        return android;
    }

    public static class IosEntity {
        private String version;
        private String build;
        private String url;

        public void setVersion(String version) {
            this.version = version;
        }

        public void setBuild(String build) {
            this.build = build;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public String getBuild() {
            return build;
        }

        public String getUrl() {
            return url;
        }
    }

    public static class AndroidEntity {
        private String version;
        private String url;

        public void setVersion(String version) {
            this.version = version;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public String getUrl() {
            return url;
        }
    }
}
