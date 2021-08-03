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
package com.ww.roxiesample.presentation.notedetail

import androidx.lifecycle.viewModelScope
import com.ww.roxie.BaseCoroutineViewModel
import com.ww.roxie.Reducer
import com.ww.roxiesample.domain.DeleteNoteUseCase
import com.ww.roxiesample.domain.GetNoteDetailUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class NoteDetailViewModel(
    initialState: State?,
    private val noteDetailUseCase: GetNoteDetailUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : BaseCoroutineViewModel<Action, State>() {

    override val initialState = initialState ?: State(isIdle = true)

    private val reducer: Reducer<State, Change> = { state, change ->
        when (change) {
            is Change.Loading -> state.copy(
                isLoading = true,
                note = null,
                isIdle = false,
                isLoadError = false,
                isDeleteError = false
            )
            is Change.NoteDetail -> state.copy(
                isLoading = false,
                note = change.note
            )
            is Change.NoteLoadError -> state.copy(
                isLoading = false,
                isLoadError = true
            )
            Change.NoteDeleted -> state.copy(
                isLoading = false,
                isNoteDeleted = true
            )
            is Change.NoteDeleteError -> state.copy(
                isLoading = false,
                isDeleteError = true
            )
        }
    }

    init {
        viewModelScope.launch {
            bindActions()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun bindActions() {
        val loadNoteChange = actions.filterIsInstance<Action.LoadNoteDetail>()
            .mapLatest { action ->
                Timber.v("Received action: $action, thread; ${Thread.currentThread().name}")
                Change.NoteDetail(noteDetailUseCase.findById(action.noteId))
            }
            .flowOn(Dispatchers.IO)
            .onStart<Change> { emit(Change.Loading) }
            .catch { emit(Change.NoteLoadError(it)) }

        val deleteNoteChange = actions.filterIsInstance<Action.DeleteNote>()
            .mapLatest { action ->
                Timber.v("Received action: $action, thread; ${Thread.currentThread().name}")
                val findById = noteDetailUseCase.findById(action.noteId)
                deleteNoteUseCase.delete(findById)
                Change.NoteDeleted
            }
            .onStart<Change> { emit(Change.Loading) }
            .catch { emit(Change.NoteDeleteError(it)) }
            .flowOn(Dispatchers.IO)

        val allChanges = merge(loadNoteChange, deleteNoteChange)

        allChanges.scan(initialState) { state, change -> reducer(state, change) }
            .filter { !it.isIdle && !it.isLoading }
            .distinctUntilChanged()
            .collect {
                state.postValue(it)
            }
    }
}
