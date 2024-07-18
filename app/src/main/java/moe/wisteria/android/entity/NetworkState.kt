package moe.wisteria.android.entity

data class NetworkState<T>(
    val state: State = State.WAITING,
    val data: T? = null
) {
    enum class State {
        WAITING,
        LOADING,
        SUCCESS,
        FAILED
    }
}