package moe.wisteria.android.kimoji.entity

import com.google.gson.annotations.SerializedName

data class BaseProfile(
    @SerializedName("_id")
    val id: String,
    val gender: String,
    val name: String,
    val verified: Boolean,
    val exp: Int,
    val level: Int,
    val characters: List<String>,
    val role: String,
    val avatar: Media?,
    val title: String,
    val slogan: String
) {

}