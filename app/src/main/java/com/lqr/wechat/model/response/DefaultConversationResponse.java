package com.lqr.wechat.model.response;

import java.util.List;

/**
 * Created by AMing on 16/4/1.
 * Company RongCloud
 */
public class DefaultConversationResponse {
    /**
     * code : 200
     * result : [{"type":"group","id":"E1IoyL5Pj","name":"用户体验群 Ⅰ","portraitUri":"","memberCount":3,"maxMemberCount":3000},{"type":"group","id":"iNj2YO4ib","name":"用户体验群 Ⅱ","portraitUri":"","memberCount":3,"maxMemberCount":3000},{"type":"group","id":"qGEj03bpP","name":"用户体验群 Ⅲ","portraitUri":"","memberCount":3,"maxMemberCount":3000},{"type":"chatroom","id":"OIBbeKlkx","name":"聊天室 I"},{"type":"chatroom","id":"675NdFjkx","name":"聊天室 II"},{"type":"chatroom","id":"MfgILRowx","name":"聊天室 III"},{"type":"chatroom","id":"lFVuoM7Jx","name":"聊天室 IV"}]
     */

    private int code;
    /**
     * type : group
     * id : E1IoyL5Pj
     * name : 用户体验群 Ⅰ
     * portraitUri :
     * memberCount : 3
     * maxMemberCount : 3000
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
        private String type;
        private String id;
        private String name;
        private String portraitUri;
        private int memberCount;
        private int maxMemberCount;

        public void setType(String type) {
            this.type = type;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }

        public void setMemberCount(int memberCount) {
            this.memberCount = memberCount;
        }

        public void setMaxMemberCount(int maxMemberCount) {
            this.maxMemberCount = maxMemberCount;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPortraitUri() {
            return portraitUri;
        }

        public int getMemberCount() {
            return memberCount;
        }

        public int getMaxMemberCount() {
            return maxMemberCount;
        }
    }
//
//    /**
//     * code : 200
//     * result : [{"type":"group","id":"9uWQgG6hH","memberCount":2,"maxMemberCount":3000},{"type":"group","id":"xQm0BvEUA","memberCount":2,"maxMemberCount":3000},{"type":"group","id":"h0COZMkPf","memberCount":2,"maxMemberCount":3000},{"type":"chatroom","id":"LoDld8izA"},{"type":"chatroom","id":"mFGfyNe51"},{"type":"chatroom","id":"Sv7gGCckF"},{"type":"chatroom","id":"u4pLPtsY9"}]
//     */
//
//    private int code;
//    /**
//     * type : group
//     * id : 9uWQgG6hH
//     * memberCount : 2
//     * maxMemberCount : 3000
//     */
//
//    private List<ResultEntity> result;
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public void setResult(List<ResultEntity> result) {
//        this.result = result;
//    }
//
//    public int sendCode() {
//        return code;
//    }
//
//    public List<ResultEntity> getResult() {
//        return result;
//    }
//
//    public static class ResultEntity {
//        private String type;
//        private String id;
//        private int memberCount;
//        private int maxMemberCount;
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        public void setId(String id) {
//            this.id = id;
//        }
//
//        public void setMemberCount(int memberCount) {
//            this.memberCount = memberCount;
//        }
//
//        public void setMaxMemberCount(int maxMemberCount) {
//            this.maxMemberCount = maxMemberCount;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public int getMemberCount() {
//            return memberCount;
//        }
//
//        public int getMaxMemberCount() {
//            return maxMemberCount;
//        }
//    }


}
