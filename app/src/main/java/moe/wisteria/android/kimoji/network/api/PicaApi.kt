package moe.wisteria.android.kimoji.network.api

import moe.wisteria.android.kimoji.network.entity.body.SignInBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PicaApi {
    @POST("/auth/sign-in")
    fun signIn(
        @Body
        body: SignInBody
    ): Call<ResponseBody>
}