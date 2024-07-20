package moe.wisteria.android.kimoji.util

import moe.wisteria.android.kimoji.R

val localizationMapping: Map<String, Int> = mapOf(
    "invalid email or password" to R.string.network_invalid_email_or_password
)

fun getLocalization(
    key: String
): Int {
    return localizationMapping[key] ?: R.string.network_unknown_response
}