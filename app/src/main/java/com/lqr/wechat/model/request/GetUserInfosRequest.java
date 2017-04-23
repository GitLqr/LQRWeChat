package com.lqr.wechat.model.request;

import java.util.List;

/**
 * Created by AMing on 16/5/23.
 * Company RongCloud
 */
public class GetUserInfosRequest {
    private List<String> querystring;

    public List<String> getQuerystring() {
        return querystring;
    }

    public void setQuerystring(List<String> querystring) {
        this.querystring = querystring;
    }

    public GetUserInfosRequest(List<String> querystring) {
        this.querystring = querystring;
    }
}
