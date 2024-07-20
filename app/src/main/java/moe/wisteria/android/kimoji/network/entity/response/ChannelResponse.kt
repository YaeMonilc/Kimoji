package moe.wisteria.android.kimoji.network.entity.response

data class ChannelResponse(
    val status: String,
    val addresses: List<String>,
    val waka: String,
    val adKeyword: String
)
