package moe.wisteria.android.entity

import java.lang.Exception

data class NetworkState<T>(
    val state: State = State.WAITING,
    val data: T? = null,
    val exception: Exception? = null
) {
    enum class State {
        WAITING,
        LOADING,
        SUCCESS,
        FAILED,
        EXCEPTION
    }
}