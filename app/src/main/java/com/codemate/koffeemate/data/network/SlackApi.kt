package com.codemate.koffeemate.data.network

import com.codemate.koffeemate.BuildConfig
import com.codemate.koffeemate.data.network.models.UserListResponse
import com.codemate.koffeemate.extensions.toRequestBody
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import rx.Observable

interface SlackApi {

    @GET("users.list")
    fun getUsers(@Query("token") token: String): Observable<UserListResponse>

    @FormUrlEncoded
    @POST("chat.postMessage")
    fun postMessage(
            @Field("channel") channel: String,
            @Field("text") text: String,
            @Field("token") token: String = BuildConfig.SLACK_AUTH_TOKEN,
            @Field("as_user") asUser: Boolean = false,
            @Field("username") username: String = "Koffeemate",
            @Field("icon_emoji") icon: String = ":coffee:"
    ): Observable<Response<ResponseBody>>

    @Multipart
    @POST("files.upload")
    fun postImage(
            @Part file: MultipartBody.Part,
            @Part("filename") filename: RequestBody,
            @Part("channels") channels: RequestBody,
            @Part("initial_comment") comment: RequestBody,
            @Part("token") token: RequestBody = BuildConfig.SLACK_AUTH_TOKEN.toRequestBody()
    ): Observable<Response<ResponseBody>>

    companion object {
        val BASE_URL = HttpUrl.parse("https://slack.com/api/")!!
    }
}
