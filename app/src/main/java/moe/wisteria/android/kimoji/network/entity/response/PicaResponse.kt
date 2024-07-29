package moe.wisteria.android.kimoji.network.entity.response

import moe.wisteria.android.kimoji.entity.BaseComic
import moe.wisteria.android.kimoji.entity.Comics
import moe.wisteria.android.kimoji.entity.Profile
import kotlin.Exception

data class PicaResponse<T>(
    val response: T? = null
) {
    companion object {
        class ErrorResponseException(
            val body: ErrorResponse
        ): Exception()
    }

    open class BaseResponse(
        val code: Int,
        val message: String
    )

    class ErrorResponse(
        code: Int,
        message: String,
        val error: String
    ) : BaseResponse(
        code = code,
        message = message
    )

    class SignInResponse(
        code: Int,
        message: String,
        val data: Data
    ) : BaseResponse(
        code = code,
        message = message
    ) {
        data class Data(
            val token: String
        )
    }

    class RandomComics(
        code: Int,
        message: String,
        val data: Data
    ) : BaseResponse(
        code = code,
        message = message
    ) {
        data class Data(
            val comics: List<BaseComic>
        )
    }

    class ComicList(
        code: Int,
        message: String,
        val data: Data
    ) : BaseResponse(
        code = code,
        message = message
    ) {
        data class Data(
            val comics: Comics
        )
    }

    class UsersProfile(
        code: Int,
        message: String,
        val data: Data
    ) : BaseResponse(
        code = code,
        message = message
    ) {
        data class Data(
            val user: Profile
        )
    }

    class UsersPunchIn(
        code: Int,
        message: String,
        val data: Data
    ) : BaseResponse(
        code = code,
        message = message
    ) {
        data class Data(
            val res: Result
        ) {
            data class Result(
                val status: String,
                val punchInLastDay: String
            )
        }
    }
}