package com.ww.roxiesample.presentation.notedetail

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import com.ww.roxiesample.domain.DeleteNoteUseCase
import com.ww.roxiesample.domain.GetNoteDetailUseCase
import com.ww.roxiesample.domain.Note
import com.ww.roxiesample.presentation.RxTestSchedulerRule
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test


private const val NOTE_ID = 1L
private const val NOTE_TEXT = "dummy text"

class NoteDetailViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testSchedulerRule = RxTestSchedulerRule()

    private lateinit var testSubject: NoteDetailViewModel

    private val noteDetailUseCase = mock<GetNoteDetailUseCase>()

    private val deleteNoteUseCase = mock<DeleteNoteUseCase>()

    private val observer = mock<Observer<State>>()

    @Before
    fun setUp() {
        val idleState = State(isIdle = true)
        testSubject = NoteDetailViewModel(idleState, noteDetailUseCase, deleteNoteUseCase)
        testSubject.observableState.observeForever(observer)
    }

    @Test
    fun `Given note successfully loaded, when action LoadNoteDetail is received, then State contains note`() {
        // GIVEN
        val note = Note(NOTE_ID, NOTE_TEXT)
        val successState = State(note)

        whenever(noteDetailUseCase.findById(NOTE_ID)).thenReturn(Single.just(note))

        // WHEN
        testSubject.dispatch(Action.LoadNoteDetail(NOTE_ID))
        testSchedulerRule.triggerActions()

        // THEN
        verify(observer).onChanged(successState)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `Given note failed to load, when action LoadNoteDetail is received, then State contains error`() {
        // GIVEN
        val loadErrorState = State(isLoadError = true)

        whenever(noteDetailUseCase.findById(NOTE_ID)).thenReturn(Single.error(RuntimeException()))

        // WHEN
        testSubject.dispatch(Action.LoadNoteDetail(NOTE_ID))
        testSchedulerRule.triggerActions()

        // THEN
        verify(observer).onChanged(loadErrorState)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `Given note successfully deleted, when action DeleteNote is received, then emits note deleted State`() {
        // GIVEN
        val note = Note(NOTE_ID, NOTE_TEXT)
        val noteDeletedState = State(isNoteDeleted = true)

        whenever(noteDetailUseCase.findById(NOTE_ID)).thenReturn(Single.just(note))
        whenever(deleteNoteUseCase.delete(note)).thenReturn(Completable.complete())

        // WHEN
        testSubject.dispatch(Action.DeleteNote(NOTE_ID))
        testSchedulerRule.triggerActions()

        // THEN
        inOrder(noteDetailUseCase, deleteNoteUseCase) {
            verify(noteDetailUseCase).findById(NOTE_ID)
            verify(deleteNoteUseCase).delete(note)
        }
        verify(observer).onChanged(noteDeletedState)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `Given note for deletion failed to load, when action DeleteNote is received, then emits delete note error State`() {
        // GIVEN
        val deleteErrorState = State(isDeleteError = true)
        whenever(noteDetailUseCase.findById(NOTE_ID)).thenReturn(Single.error(RuntimeException()))

        // WHEN
        testSubject.dispatch(Action.DeleteNote(NOTE_ID))
        testSchedulerRule.triggerActions()

        // THEN
        verify(noteDetailUseCase).findById(NOTE_ID)
        verify(deleteNoteUseCase, never()).delete(any())
        verify(observer).onChanged(deleteErrorState)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `Given note deletion failed, when action DeleteNote is received, then emits delete note error State`() {
        // GIVEN
        val note = Note(NOTE_ID, NOTE_TEXT)
        val deleteErrorState = State(isDeleteError = true)

        whenever(noteDetailUseCase.findById(NOTE_ID)).thenReturn(Single.just(note))
        whenever(deleteNoteUseCase.delete(note)).thenReturn(Completable.error(RuntimeException()))

        // WHEN
        testSubject.dispatch(Action.DeleteNote(NOTE_ID))
        testSchedulerRule.triggerActions()

        // THEN
        inOrder(noteDetailUseCase, deleteNoteUseCase) {
            verify(noteDetailUseCase).findById(NOTE_ID)
            verify(deleteNoteUseCase).delete(note)
        }
        verify(observer).onChanged(deleteErrorState)
        verifyNoMoreInteractions(observer)
    }
}