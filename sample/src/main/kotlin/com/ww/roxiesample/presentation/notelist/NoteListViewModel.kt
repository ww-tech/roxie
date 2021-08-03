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

import androidx.lifecycle.viewModelScope
import com.ww.roxie.BaseCoroutineViewModel
import com.ww.roxie.Reducer
import com.ww.roxiesample.domain.GetNoteListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.collect

import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NoteListViewModel(
    initialState: State?,
    private val loadNoteListUseCase: GetNoteListUseCase
) : BaseCoroutineViewModel<Action, State>() {

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
        viewModelScope.launch {
            bindActions()
        }
    }

    @ExperimentalCoroutinesApi
    private suspend fun bindActions() {
        val loadNotesChange: Flow<Change> = actions.filterIsInstance<Action.LoadNotes>()
            .mapLatest { Change.Notes(loadNoteListUseCase.loadAll().ifEmpty { emptyList() }) }
            .catch<Change> { emit(Change.Error(it)) } // TODO: Do we need to emit?
            .flowOn(Dispatchers.IO)
            .onStart { emit(Change.Loading) }

        loadNotesChange.scan(initialState) { state, change -> reducer(state, change) }
            .filterNot { it.isIdle }
            .distinctUntilChanged()
            .collect {
                state.value = it
            }
    }
}
