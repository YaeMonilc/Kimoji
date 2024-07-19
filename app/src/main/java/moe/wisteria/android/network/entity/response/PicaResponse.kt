package moe.wisteria.android.network.entity.response

import java.lang.Exception

data class PicaResponse<T>(
    val status: Status = Status.WAITING,
    val data: T? = null,
    val error: ErrorResponse? = null,
    val exception: Exception? = null
) {
    companion object {
        fun <T> PicaResponse<T>.onSuccess(block: (T) -> Unit): PicaResponse<T> {
            if (status == Status.SUCCESS)
                data?.let(block)

            return this
        }
        fun <T> PicaResponse<T>.onError(block: (ErrorResponse) -> Unit): PicaResponse<T> {
            if (status == Status.ERROR)
                error?.let(block)

            return this
        }
        fun <T> PicaResponse<T>.onException(block: (Exception) -> Unit): PicaResponse<T> {
            if (status == Status.EXCEPTION)
                exception?.let(block)

            return this
        }
    }

    enum class Status {
        WAITING,
        SUCCESS,
        ERROR,
        EXCEPTION
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
}