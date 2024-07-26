package moe.wisteria.android.kimoji.entity

import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val order: Int,
    @SerializedName("updated_at")
    val updatedAt: String
)