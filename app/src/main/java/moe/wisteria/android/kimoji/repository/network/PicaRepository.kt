package moe.wisteria.android.kimoji.repository.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import moe.wisteria.android.kimoji.entity.BaseComic
import moe.wisteria.android.kimoji.entity.Profile
import moe.wisteria.android.kimoji.entity.Sort
import moe.wisteria.android.kimoji.network.entity.body.RegisterBody
import moe.wisteria.android.kimoji.network.entity.body.SearchBody
import moe.wisteria.android.kimoji.network.entity.body.SignInBody
import moe.wisteria.android.kimoji.network.entity.response.PicaResponse
import moe.wisteria.android.kimoji.network.picaApi
import moe.wisteria.android.kimoji.util.executeForPica

object PicaRepository {
    object Auth {
        fun signIn(
            email: String,
            password: String
        ): Flow<String> = channelFlow {
            picaApi.signIn(
                body = SignInBody(
                    email = email,
                    password = password
                )
            ).executeForPica<PicaResponse.SignInResponse>().let {
                it.response?.data?.token?.let { token -> trySend(token) }
            }
        }

        fun register(
            email: String,
            name: String,
            password: String,
            gender: String,
            birthday: String,
            question1: String,
            answer1: String,
            question2: String = question1,
            answer2: String = answer1,
            question3: String = question1,
            answer3: String = answer1,
        ): Flow<Nothing?> = channelFlow {
            picaApi.register(
                body = RegisterBody(
                    email = email,
                    name = name,
                    password = password,
                    gender = gender,
                    birthday = birthday,
                    question1 = question1,
                    answer1 = answer1,
                    question2 = question2,
                    answer2 = answer2,
                    question3 = question3,
                    answer3 = answer3
                )
            ).executeForPica<PicaResponse.BaseResponse>().also {
                trySend(null)
            }
        }
    }
    object User {
        fun profile(
            token: String
        ): Flow<Profile> = channelFlow {
            picaApi.usersProfile(
                token = token
            ).executeForPica<PicaResponse.UsersProfile>().let {
                it.response?.data?.user?.let { profile -> trySend(profile) }
            }
        }

        fun punchIn(
            token: String
        ): Flow<PicaResponse.UsersPunchIn.Data.Result> = channelFlow {
            picaApi.usersPunchIn(
                token = token
            ).executeForPica<PicaResponse.UsersPunchIn>().let {
                it.response?.data?.res?.let { result -> trySend(result) }
            }
        }
    }
    object Comics {
        fun random(
            token: String
        ): Flow<List<BaseComic>> = channelFlow {
            picaApi.randomComic(
                token = token
            ).executeForPica<PicaResponse.RandomComics>().let {
                it.response?.data?.let { data ->
                    trySend(data.comics)
                }
            }
        }

        fun search(
            token: String,
            keyword: String,
            page: Int = 1,
            sort: String = Sort.FAVORITE.string
        ): Flow<moe.wisteria.android.kimoji.entity.Comics> = channelFlow {
            picaApi.searchComic(
                token = token,
                body = SearchBody(
                    keyword = keyword,
                    sort = sort
                ),
                page = page
            ).executeForPica<PicaResponse.ComicList>().let {
                it.response?.data?.comics?.let { comics -> trySend(comics) }
            }
        }
    }
}