package com.ww.roxie.coroutines

import androidx.lifecycle.viewModelScope
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * roxie
 *
 * @author dragos
 * @since 23.03.2021
 */
abstract class CoroutineViewModel<A : BaseAction, S : BaseState> : BaseViewModel<A, S>() {

    protected val actions = ConflatedBroadcastChannel<A>()

    override fun offerAction(action: A) {
        actions.offer(action)
    }

    fun <T> Flow<T>.launchHere() = launchIn(viewModelScope)

    fun Flow<S>.observeState() = onEach {
        state.value = it
    }

}