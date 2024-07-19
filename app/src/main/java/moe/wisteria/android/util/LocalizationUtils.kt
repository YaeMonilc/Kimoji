package moe.wisteria.android.util

import moe.wisteria.android.R

val localizationMapping: Map<String, Int> = mapOf(
    "invalid email or password" to R.string.network_invalid_email_or_password
)

fun getLocalization(
    key: String
): Int {
    return localizationMapping[key] ?: R.string.network_unknown_response
}