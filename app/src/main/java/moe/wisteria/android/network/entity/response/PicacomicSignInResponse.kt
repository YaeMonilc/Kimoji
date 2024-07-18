package moe.wisteria.android.network.entity.response

class PicacomicSignInResponse(
    code: Int,
    message: String,
    override val data: Data?
) : PicacomicStandardResponse(
    code = code,
    message = message,
    data = data
) {
    data class Data(
        val token: String
    )
}