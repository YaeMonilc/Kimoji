package moe.wisteria.android.kimoji.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import coil.ImageLoader
import coil.disk.DiskCache
import coil.load
import coil.memory.MemoryCache
import com.google.android.material.snackbar.Snackbar
import moe.wisteria.android.kimoji.Kimoji
import moe.wisteria.android.kimoji.network.defaultOkHttpClient
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import moe.wisteria.android.kimoji.ui.view.BaseFragment
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
                                response = gson.fromJson(string, T::class.java)
                            )
                        }
                        else
                            throw PicaResponse.Companion.ErrorResponseException(
                                body = gson.fromJson(body.string(), PicaResponse.ErrorResponse::class.java)
                            )
                    }
                }
            else
                response.errorBody()?.let { errorBody ->
                    throw PicaResponse.Companion.ErrorResponseException(
                        body = gson.fromJson(errorBody.string(), PicaResponse.ErrorResponse::class.java)
                    )
                }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }

    return PicaResponse()
}

fun BaseFragment.picaExceptionHandler(
    exception: Exception,
    view: View? = null,
    length: Int = Snackbar.LENGTH_SHORT
) {
    if (exception is PicaResponse.Companion.ErrorResponseException)
        showSnackBar(getLocalization(exception.body.message), view, length)
    else
        exception.message?.let { message ->
            showSnackBar(message, view, length)
        }
}

val Context.imageLoader: ImageLoader
    get() = ImageLoader(this).newBuilder().apply {
        okHttpClient { defaultOkHttpClient }
        memoryCache {
            MemoryCache.Builder(this@imageLoader).apply {
                maxSizePercent(0.3)
                weakReferencesEnabled(true)
                strongReferencesEnabled(true)
                maxSizeBytes(1024 * 1024 * 10)
            }.build()
        }
        diskCache {
            DiskCache.Builder().apply {
                directory(this@imageLoader.cacheDir.resolve(Kimoji.Companion.Dir.COIL_CACHE))
                maxSizePercent(0.1)
                maxSizeBytes(1024 * 1024 * 10)
                cleanupDispatcher(IO)
            }.build()
        }
    }.build()

fun ImageView.loadImage(
    data: Any
) {
    load(
        data = data,
        imageLoader = context.imageLoader
    )
}