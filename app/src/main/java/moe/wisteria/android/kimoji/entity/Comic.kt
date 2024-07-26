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
    val thumb: Media,
    val totalViews: Int,
    val totalLikes: Int,
    val likesCount: Int
)

class Comic(
    id: String,
    title: String,
    author: String,
    pagesCount: Int,
    epsCount: Int,
    finished: Boolean,
    categories: List<String>,
    thumb: Media,
    totalViews: Int,
    totalLikes: Int,
    likesCount: Int,
    @SerializedName("_creator")
    val creator: BaseProfile,
    val description: String,
    val chineseTeam: String,
    val tags: List<String>,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("created_at")
    val createdAt: String,
    val allowDownload: Boolean,
    val allowComment: Boolean,
    val totalComments: Int,
    val viewsCount: Int,
    val commentCount: Int,
    val isFavourite: Boolean,
    val isLiked: Boolean
) : BaseComic(
    id,
    title,
    author,
    pagesCount,
    epsCount,
    finished,
    categories,
    thumb,
    totalViews,
    totalLikes,
    likesCount
)

data class Comics(
    val docs: List<Comic>,
    val total: Int,
    val limit: Int,
    val page: Int,
    val pages: Int
)