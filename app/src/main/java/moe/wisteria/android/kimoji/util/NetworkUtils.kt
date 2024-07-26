package moe.wisteria.android.kimoji.util

import android.content.Context
import coil.ImageLoader
import moe.wisteria.android.kimoji.network.defaultOkHttpClient
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.awaitResponse

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

suspend inline fun <reified T> Call<ResponseBody>.executeForPica(): PicaResponse<T> {
    try {
        awaitResponse().let { response ->
            if (response.isSuccessful)
                response.body()?.let { body ->
                    val string = body.string()
                    gson.fromJson(string, PicaResponse.BaseResponse::class.java).let { baseResponse ->
                        if (baseResponse.code == 200) {
                            return PicaResponse(
                                status = PicaResponse.Status.SUCCESS,
                                data = gson.fromJson(string, T::class.java)
                            )
                        }
                        else
                            return PicaResponse(
                                status = PicaResponse.Status.ERROR,
                                error = gson.fromJson(body.string(), PicaResponse.ErrorResponse::class.java)
                            )
                    }
                }
            else
                response.errorBody()?.let { errorBody ->
                    return PicaResponse(
                        status = PicaResponse.Status.ERROR,
                        error = gson.fromJson(errorBody.string(), PicaResponse.ErrorResponse::class.java)
                    )
                }
        }
    } catch (e: Exception) {
        //e.printStackTrace()
        return PicaResponse(
            status = PicaResponse.Status.EXCEPTION,
            exception = e
        )
    }

    return PicaResponse()
}

val Context.imageLoader: ImageLoader
    get() = ImageLoader(this).newBuilder().apply {
        okHttpClient { defaultOkHttpClient }
    }.build()