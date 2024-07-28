package moe.wisteria.android.kimoji.entity

import com.google.gson.annotations.SerializedName

open class BaseProfile(
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
)

class Profile(
    id: String,
    gender: String,
    name: String,
    verified: Boolean,
    exp: Int,
    level: Int,
    characters: List<String>,
    role: String,
    avatar: Media?,
    title: String,
    slogan: String,
    val email: String,
    @SerializedName("created_at")
    val createdTime: String,
    val isPunched: Boolean
): BaseProfile(
    id = id,
    gender = gender,
    name = name,
    verified = verified,
    exp = exp,
    level = level,
    characters = characters,
    role = role,
    avatar = avatar,
    title = title,
    slogan = slogan
)