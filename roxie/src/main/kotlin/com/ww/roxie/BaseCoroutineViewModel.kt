/*
* Copyright (C) 2019. WW International, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.ww.roxie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * Store which manages business data and state.
 */
abstract class BaseCoroutineViewModel<A : BaseAction, S : BaseState> : ViewModel() {
    private val _actionsFlow = MutableSharedFlow<A>()
    protected val actions : SharedFlow<A> = _actionsFlow

    protected abstract val initialState: S

    protected val state = MutableLiveData<S>()

    private val tag by lazy { javaClass.simpleName }

    /**
     * Returns the current state. It is equal to the last value returned by the store's reducer.
     */
    val observableState: LiveData<S> = MediatorLiveData<S>().apply {
        addSource(state) { data ->
            Roxie.log("$tag: Received state: $data")
            setValue(data)
        }
    }

    /**
     * Dispatches an action. This is the only way to trigger a state change.
     */
    fun dispatch(action: A) {
        Roxie.log("$tag: Received action: $action")
        viewModelScope.launch {
            _actionsFlow.emit(action)
        }
    }

    override fun onCleared() {
        // TODO: Close flow somehow? Emit terminal event?
        // _actionsFlow.cancel()
    }
}
