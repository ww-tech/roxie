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
package com.ww.roxiesample.presentation.notelist

import com.ww.roxie.*
import com.ww.roxie.coroutines.CoroutineViewModel
import com.ww.roxie.coroutines.defaultOnEmpty
import com.ww.roxie.coroutines.ofType
import com.ww.roxiesample.domain.GetNoteListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import timber.log.Timber

class NoteListViewModel(
    initialState: State?,
    private val loadNoteListUseCase: GetNoteListUseCase
) : CoroutineViewModel<Action, State>() {

    override val initialState = initialState ?: State(isIdle = true)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isIdle = false,
                isLoading = true,
                notes = emptyList(),
                isError = false
            )
            is Change.Notes -> state.copy(
                isLoading = false,
                notes = change.notes
            )
            is Change.Error -> state.copy(
                isLoading = false,
                isError = true
            )
        }
    }

    init {
        bindActions()
    }

    private fun bindActions() {
        val loadNotesChange: Flow<Change> = actions.asFlow()
            .ofType<Action.LoadNotes>()
            .flatMapLatest {
                loadNoteListUseCase.loadAll()
                    .flowOn(Dispatchers.IO)
                    .map { Change.Notes(it) }
                    .defaultOnEmpty(Change.Notes(emptyList()))
                    .catch<Change> {
                        emit(Change.Error(it))
                    }
                    .onStart { emit(Change.Loading) }
            }

        // to handle multiple Changes, use Observable.merge to merge them into a single stream:
        // val allChanges = Observable.merge(loadNotesChange, ...)

        loadNotesChange
            .scan(initialState) { state, change ->
                reducer.invoke(state, change)
            }
            .filter { !it.isIdle }
            .distinctUntilChanged()
            .flowOn(Dispatchers.Main)
            .observeState()
            .catch {
                Timber.e(it)
            }
            .launchHere()
    }
}
