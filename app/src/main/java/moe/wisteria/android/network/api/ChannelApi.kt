package moe.wisteria.android.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface ChannelApi {
    @GET("/init")
    fun init(): Call<ResponseBody>
}