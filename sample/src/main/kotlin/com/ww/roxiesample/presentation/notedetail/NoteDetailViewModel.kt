/*
* Copyright (C) 2019Action.kt. WW International, Inc.
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

import com.ww.roxie.BaseViewModel
import com.ww.roxie.Reducer
import com.ww.roxiesample.domain.DeleteNoteUseCase
import com.ww.roxiesample.domain.GetNoteDetailUseCase
import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class NoteDetailViewModel(
    initialState: State?,
    private val noteDetailUseCase: GetNoteDetailUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : BaseViewModel<Action, State>() {

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
        bindActions()
    }

    private fun bindActions() {
        val loadNoteChange = actions.ofType<Action.LoadNoteDetail>()
            .switchMap { action ->
                noteDetailUseCase.findById(action.noteId)
                    .subscribeOn(Schedulers.io())
                    .toObservable()
                    .map<Change> { Change.NoteDetail(it) }
                    .onErrorReturn { Change.NoteLoadError(it) }
                    .startWith(Change.Loading)
            }

        val deleteNoteChange = actions.ofType<Action.DeleteNote>()
            .switchMap { action ->
                noteDetailUseCase.findById(action.noteId)
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable { deleteNoteUseCase.delete(it) }
                    .toSingleDefault<Change>(Change.NoteDeleted)
                    .onErrorReturn { Change.NoteDeleteError(it) }
                    .toObservable()
                    .startWith(Change.Loading)
            }

        val allChanges = Observable.merge(loadNoteChange, deleteNoteChange)

        disposables += allChanges
            .scan(initialState, reducer)
            .filter { !it.isIdle && !it.isLoading }
            .distinctUntilChanged()
            .subscribe(state::postValue, Timber::e)
    }
}
