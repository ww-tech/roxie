package com.ww.roxiesample.presentation.notelist

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.ww.roxiesample.domain.GetNoteListUseCase
import com.ww.roxiesample.domain.Note
import com.ww.roxiesample.presentation.RxTestSchedulerRule
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NoteListViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var testSubject: NoteListViewModel

    private val idleState = State(isIdle = true)

    private val loadingState = State(isLoading = true)

    private val noteListUseCase = mock<GetNoteListUseCase>()

    private val observer = mock<Observer<State>>()

    @Before
    fun setUp() {
        testSubject = NoteListViewModel(idleState, noteListUseCase)
        testSubject.observableState.observeForever(observer)
    }

    @Test
    fun `Given notes successfully loaded, when action LoadNotes is received, then State contains notes`() {
        // GIVEN
        val noteList = listOf(Note(1L, "dummy text"))
        val successState = State(noteList)

        whenever(noteListUseCase.loadAll()).thenReturn(Single.just(noteList))

        // WHEN
        testSubject.dispatch(Action.LoadNotes)
        testSchedulerRule.triggerActions()

        // THEN
        inOrder(observer) {
            verify(observer).onChanged(loadingState)
            verify(observer).onChanged(successState)
        }
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `Given notes failed to load, when action LoadNotes is received, then State contains error`() {
        // GIVEN
        whenever(noteListUseCase.loadAll()).thenReturn(Single.error(RuntimeException()))
        val errorState = State(isError = true)

        // WHEN
        testSubject.dispatch(Action.LoadNotes)
        testSchedulerRule.triggerActions()

        // THEN
        inOrder(observer) {
            verify(observer).onChanged(loadingState)
            verify(observer).onChanged(errorState)
        }
        verifyNoMoreInteractions(observer)
    }
}