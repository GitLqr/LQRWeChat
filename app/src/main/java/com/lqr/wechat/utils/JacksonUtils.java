package com.lqr.wechat.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;


/**
 * @创建者 CSDN_LQR
 * @描述 JSON解析工具
 */
public class JacksonUtils {

    private static ObjectMapper mMapper;

    public static ObjectMapper getInstance() {
        if (mMapper == null) {
            synchronized (JacksonUtils.class) {
                if (mMapper == null) {
                    mMapper = new ObjectMapper();
                    //反序列化时忽略model没有字段
                    mMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    mMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                    //通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化
                    //Include.Include.ALWAYS 默认
                    //Include.NON_DEFAULT 属性为默认值不序列化
                    //Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
                    //Include.NON_NULL 属性为NULL 不序列化
                    mMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                }
            }
        }
        return mMapper;
    }

    public static String toJson(Object obj) {
        try {
            return getInstance().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return getInstance().readValue(json, classOfT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, JavaType javaType) {
        try {
            return getInstance().readValue(json, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJson(String json, TypeReference type) {
        try {
            return getInstance().readValue(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
