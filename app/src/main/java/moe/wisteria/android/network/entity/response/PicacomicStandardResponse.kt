package moe.wisteria.android.network.entity.response

open class PicacomicStandardResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)