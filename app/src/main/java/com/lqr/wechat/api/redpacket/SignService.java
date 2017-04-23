package com.lqr.wechat.api.redpacket;

import com.lqr.wechat.model.redpacket.SignModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @创建者 CSDN_LQR
 * @描述 云账户获取demo签名接口
 */

public interface SignService {

    @GET("api/demo-sign/")
    Call<SignModel> getSignInfo(@Query("uid") String userId, @Query("token") String token);
}
