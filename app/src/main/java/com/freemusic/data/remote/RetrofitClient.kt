package com.freemusic.data.remote

import com.freemusic.data.remote.api.NeteaseApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit网络客户端工厂
 * 用于创建API服务实例
 */
object RetrofitClient {
    
    // API基础URL
    // 使用社区维护的网易云音乐API公共服务
    // 注意：公共服务仅供测试使用，生产环境建议自建服务
    // 自建教程：https://github.com/neteasecloudmusicapienhanced/api-enhanced
    private const val BASE_URL = "https://netease-cloud-music-api-rouge-five-72.vercel.app/"
    
    /**
     * OkHttp客户端
     * 配置超时、日志拦截器等
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
    
    /**
     * Retrofit实例
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * 网易云音乐API服务
     */
    val neteaseApi: NeteaseApiService by lazy {
        retrofit.create(NeteaseApiService::class.java)
    }
}
