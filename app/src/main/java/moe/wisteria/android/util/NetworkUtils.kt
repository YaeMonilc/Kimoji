package moe.wisteria.android.util

import moe.wisteria.android.network.defaultOkHttpClient
import moe.wisteria.android.network.entity.response.PicacomicStandardResponse
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

    suspend inline fun <reified SuccessType, reified ErrorType> Call<ResponseBody>.responseAnalysis(
        success: (SuccessType) -> Unit = {},
        error: (ErrorType) -> Unit = {},
        failure: (Exception) -> Unit = {},
        finally: () -> Unit = {}
    ) {
        try {
            awaitResponse().let { response ->
                if (response.code() == 200) {
                    val successObject = gson.fromJson(response.body()?.string(), SuccessType::class.java)

                    if (SuccessType::class is PicacomicStandardResponse<*>) {
                        if ((successObject as PicacomicStandardResponse<*>).code == 200)
                            success(successObject)
                        else
                            error(gson.fromJson(response.errorBody()?.string(), ErrorType::class.java))
                    } else {
                        success(successObject)
                    }
                }
                else
                    error(gson.fromJson(response.errorBody()?.string(), ErrorType::class.java))
            }
        } catch (e: Exception) {
            failure(e)
        } finally {
            finally()
        }
    }
}