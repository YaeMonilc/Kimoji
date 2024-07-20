package moe.wisteria.android.kimoji.network.api

import moe.wisteria.android.kimoji.network.entity.response.ChannelResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface ChannelApi {
    @GET("/init")
    suspend fun init(): ChannelResponse
}