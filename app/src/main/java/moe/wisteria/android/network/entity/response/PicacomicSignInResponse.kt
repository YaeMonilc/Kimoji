package moe.wisteria.android.network.entity.response

class PicacomicSignInResponse(
    code: Int,
    message: String,
    data: Data?
) : PicacomicStandardResponse<PicacomicSignInResponse.Data>(
    code = code,
    message = message,
    data = data
) {
    data class Data(
        val token: String
    )
}