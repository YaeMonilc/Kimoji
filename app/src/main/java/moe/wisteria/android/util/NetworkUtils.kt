package moe.wisteria.android.util

import moe.wisteria.android.network.defaultOkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object NetworkUtils {
    enum class Methods {
        GET,
        POST,
        DELETE,
        PUT
    }

    fun tryConnect(
        url: String,
        method: Methods = Methods.GET
    ): Boolean {
        try {
            defaultOkHttpClient.newCall(
                Request.Builder().apply {
                    url(url)
                    when (method) {
                        Methods.GET -> get()
                        Methods.POST -> post("".toRequestBody())
                        Methods.DELETE -> delete()
                        Methods.PUT -> put("".toRequestBody())
                    }
                }.build()
            ).execute()

            return true
        } catch (e: Exception) {
            return false
        }
    }


}