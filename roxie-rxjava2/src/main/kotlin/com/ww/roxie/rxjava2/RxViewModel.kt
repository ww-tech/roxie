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
package com.ww.roxie.rxjava2

import androidx.lifecycle.viewModelScope
import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import com.ww.roxie.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn

/**
 * Store which manages business data and state.
 */
@Suppress("unused")
abstract class RxViewModel<A : BaseAction, S : BaseState> : BaseViewModel<A, S>() {

    protected val actions: PublishSubject<A> = PublishSubject.create<A>()

    protected val disposables: CompositeDisposable = CompositeDisposable()

    override fun offerAction(action: A) {
        actions.onNext(action)
    }

    fun <T> Flow<T>.launchHere() = launchIn(viewModelScope)

    override fun onCleared() {
        disposables.clear()
    }

}