package moe.wisteria.android.kimoji.util

import moe.wisteria.android.kimoji.R

val localizationMapping: Map<String, Int> = mapOf(
    "validation error" to R.string.network_validation_error,
    "invalid email or password" to R.string.network_invalid_email_or_password,
    "email is already exist" to R.string.network_email_is_already_exist
)

fun getLocalization(
    key: String
): Int {
    return localizationMapping[key] ?: R.string.network_unknown_response
}