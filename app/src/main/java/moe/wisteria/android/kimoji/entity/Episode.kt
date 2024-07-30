package moe.wisteria.android.kimoji.entity

import com.google.gson.annotations.SerializedName

data class Episode(
    val id: String,
    val title: String,
    val order: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
