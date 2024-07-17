package moe.wisteria.android.network.entity.response

data class ChannelInitResponse(
    val state: String,
    val addresses: List<String>,
    val waka: String,
    val adKeyword: String
)
