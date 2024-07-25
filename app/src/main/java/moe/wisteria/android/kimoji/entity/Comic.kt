package moe.wisteria.android.kimoji.entity

import com.google.gson.annotations.SerializedName

open class BaseComic(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val author: String,
    val pagesCount: Int,
    val epsCount: Int,
    val finished: Boolean,
    val categories: List<String>,
    val thumb: Thumb,
    val totalViews: Int,
    val totalLikes: Int,
    val likesCount: Int
)