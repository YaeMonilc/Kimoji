package moe.wisteria.android.kimoji.entity

data class SearchState(
    val state: State = State.WAIT,
    val comics: List<BaseComic> = listOf()
) {
    enum class State {
        LOADING,
        EMPTY,
        SUCCESS,
        WAIT
    }
}