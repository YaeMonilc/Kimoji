package moe.wisteria.android.kimoji.entity

data class Page<T>(
    val docs: List<T>,
    val total: Int,
    val limit: Int,
    val page: Int,
    val pages: Int
)