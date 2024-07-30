package moe.wisteria.android.kimoji.network.api

import moe.wisteria.android.kimoji.network.entity.body.RegisterBody
import moe.wisteria.android.kimoji.network.entity.body.SearchBody
import moe.wisteria.android.kimoji.network.entity.body.SignInBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PicaApi {
    @POST("/auth/sign-in")
    fun signIn(
        @Body
        body: SignInBody
    ): Call<ResponseBody>

    @POST("/auth/register")
    fun register(
        @Body
        body: RegisterBody
    ): Call<ResponseBody>

    @GET("/comics/{comicId}")
    fun comicDetail(
        @Header("authorization")
        token: String,
        @Path("comicId")
        comicId: String
    ): Call<ResponseBody>

    @GET("/comics/{comicId}/eps")
    fun comicEpisode(
        @Header("authorization")
        token: String,
        @Path("comicId")
        comicId: String,
        @Query("page")
        page: Int = 1
    ): Call<ResponseBody>

    @POST("/comics/{comicId}/favourite")
    fun comicFavourite(
        @Header("authorization")
        token: String,
        @Path("comicId")
        comicId: String
    ): Call<ResponseBody>

    @GET("/comics/{comicId}/order/{order}/pages")
    fun comicOrder(
        @Header("authorization")
        token: String,
        @Path("comicId")
        comicId: String,
        @Path("order")
        order: String,
        @Query("page")
        page: Int = 1
    ): Call<ResponseBody>

    @POST("/comics/{comicId}/like")
    fun comicLike(
        @Header("authorization")
        token: String,
        @Path("comicId")
        comicId: String
    ): Call<ResponseBody>

    @GET("/comics/random")
    fun randomComic(
        @Header("authorization")
        token: String
    ): Call<ResponseBody>

    @POST("/comics/advanced-search")
    fun searchComic(
        @Header("authorization")
        token: String,
        @Body
        body: SearchBody,
        @Query("page")
        page: Int = 1
    ): Call<ResponseBody>

    @GET("/users/profile")
    fun usersProfile(
        @Header("authorization")
        token: String
    ): Call<ResponseBody>

    @POST("/users/punch-in")
    fun usersPunchIn(
        @Header("authorization")
        token: String
    ): Call<ResponseBody>
}