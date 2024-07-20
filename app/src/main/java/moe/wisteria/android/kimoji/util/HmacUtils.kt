package moe.wisteria.android.kimoji.util

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacUtils {
    fun hmacSHA256(
        key: String,
        data: String
    ) = Mac.getInstance("HmacSHA256").apply {
            init(SecretKeySpec(key.toByteArray(), algorithm))
        }.doFinal(data.toByteArray())
}