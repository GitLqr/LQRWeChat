package com.lqr.wechat.api.base;

import com.lqr.wechat.BuildConfig;
import com.lqr.wechat.api.base.persistentcookiejar.ClearableCookieJar;
import com.lqr.wechat.api.base.persistentcookiejar.PersistentCookieJar;
import com.lqr.wechat.api.base.persistentcookiejar.cache.SetCookieCache;
import com.lqr.wechat.api.base.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.lqr.wechat.app.MyApp;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.NetUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @创建者 GitLqr
 * @描述 配置Retrofit（配置网络缓存cache、配置持久cookie免登录）
 */

public class BaseApiRetrofit {

    //How long a cached response may still be used while offline
    private static final int OFFLINE_MAX_STALE_DAYS = 28;
    //Explicit instead of OkHttp's implicit 10s defaults, with more time to transfer bodies such as images on slow networks
    private static final int CONNECT_TIMEOUT_SECONDS = 15;
    private static final int READ_TIMEOUT_SECONDS = 30;
    private static final int WRITE_TIMEOUT_SECONDS = 30;

    private final OkHttpClient mClient;
    private final ClearableCookieJar mCookieJar;

    public OkHttpClient getClient() {
        return mClient;
    }

    /**
     * Call on logout, so the next account does not reuse this session
     */
    public void clearCookies() {
        mCookieJar.clear();
    }

    /**
     * Call on logout: cached GET responses (friends, groups...) belong to this account
     * and would otherwise be served to the next one while offline
     */
    public void clearResponseCache() {
        try {
            mClient.cache().evictAll();
        } catch (IOException e) {
            LogUtils.e(e);
        }
    }

    public BaseApiRetrofit() {
        /*================== common ==================*/

        // Log信息拦截器
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);//这里可以选择拦截级别

        //cache
        File httpCacheDir = new File(MyApp.getContext().getCacheDir(), "response");
        int cacheSize = 10 * 1024 * 1024;// 10 MiB
        Cache cache = new Cache(httpCacheDir, cacheSize);

        //cookie
        mCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(MyApp.getContext()));

        //OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(REWRITE_HEADER_CONTROL_INTERCEPTOR)
                .addInterceptor(OFFLINE_CACHE_INTERCEPTOR)
                //Must be a network interceptor: only then is the rewritten response what gets stored in the cache
                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//                .addInterceptor(loggingInterceptor)//设置 Debug Log 模式
                .cache(cache)
                .cookieJar(mCookieJar);
        //It logs headers (cookies included) and response bodies, and peeks up to 1 MiB of every body, so debug builds only
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new LoggingInterceptor());
        }
        mClient = builder.build();
    }

    //header配置
    Interceptor REWRITE_HEADER_CONTROL_INTERCEPTOR = chain -> {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
//                .addHeader("Content-Type", "application/json; charset=utf-8")
//                .addHeader("Accept-Encoding", "gzip, deflate")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "*/*")
//                .addHeader("Cookie", "add cookies here")
                .build();
        return chain.proceed(request);
    };

    //Offline: accept a stale cached response instead of failing right away
    Interceptor OFFLINE_CACHE_INTERCEPTOR = chain -> {
        Request request = chain.request();
        if (!NetUtils.isNetworkAvailable(MyApp.getContext())) {
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(OFFLINE_MAX_STALE_DAYS, TimeUnit.DAYS)
                    .build();
            request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build();
        }
        return chain.proceed(request);
    };

    //Make successful GETs cacheable (max-age=0 still sends online requests to the network) and mark the rest
    //no-store: OkHttp caches some of them by default, and offline max-stale would replay a cached Set-Cookie
    //into the cookie jar. ETag/Last-Modified are dropped so a 304 cannot merge a Set-Cookie into the cache.
    Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
        Response response = chain.proceed(chain.request());
        if (!"GET".equals(chain.request().method())) {
            //OkHttp only caches GET
            return response;
        }
        if (!response.isSuccessful()
                || response.header("Set-Cookie") != null
                || response.cacheControl().noStore()) {
            return response.newBuilder()
                    .header("Cache-Control", "no-store")
                    .build();
        }
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("ETag")
                .removeHeader("Last-Modified")
                .header("Cache-Control", "public, max-age=0")
                .build();
    };

    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            //这个chain里面包含了request和response，所以你要什么都可以从这里拿
            Request request = chain.request();
            long t1 = System.nanoTime();//请求发起的时间
            LogUtils.sf(String.format("发送请求 %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();//收到响应的时间
            //这里不能直接使用response.body().string()的方式输出日志
            //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
            //个新的response给应用层处理
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            LogUtils.sf(String.format("接收响应: [%s] %n返回json:【%s】 %.1fms%n%s",
                    response.request().url(),
                    responseBody.string(),
                    (t2 - t1) / 1e6d,
                    response.headers()));
            return response;
        }
    }

}
