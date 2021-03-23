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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ww.roxiesample.R
import com.ww.roxiesample.domain.DeleteNoteUseCase
import com.ww.roxiesample.domain.GetNoteDetailUseCase
import com.ww.roxiesample.domain.Note
import kotlinx.android.synthetic.main.note_detail.*

private const val NOTE_ID = "noteId"

class NoteDetailFragment : Fragment() {

    private val noteId by lazy {
        arguments?.getLong(NOTE_ID)
            ?: throw IllegalArgumentException("noteId is required")
    }

    companion object {
        fun newInstance(id: Long): NoteDetailFragment {
            val bundle = Bundle().apply {
                putLong(NOTE_ID, id)
            }
            return NoteDetailFragment().apply {
                arguments = bundle
            }
        }
    }

    private lateinit var viewModel: NoteDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.note_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Normally ViewModelFactory should be injected here along with its UseCases injected into it
        viewModel = ViewModelProvider(
            this,
            NoteDetailViewModelFactory(null, GetNoteDetailUseCase(), DeleteNoteUseCase())
        ).get(NoteDetailViewModel::class.java)

        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })

        if (savedInstanceState == null) {
            viewModel.dispatch(Action.LoadNoteDetail(noteId))
        }

        deleteNoteButton.setOnClickListener {
            viewModel.dispatch(Action.DeleteNote(noteId))
        }
    }

    private fun renderState(state: State) {
        with(state) {
            when {
                isLoadError -> renderLoadNoteDetailError()
                isDeleteError -> renderNoteDeleteError()
                note != null -> renderNoteDetailState(note)
                isNoteDeleted -> renderNoteDeleted()
            }
        }
    }

    private fun renderNoteDetailState(note: Note) {
        noteIdView.visibility = View.VISIBLE
        noteTextView.visibility = View.VISIBLE
        noteIdView.text = String.format(getString(R.string.note_detail_id), note.id)
        noteTextView.text = String.format(getString(R.string.note_detail_text), note.text)
    }

    private fun renderLoadNoteDetailError() {
        Toast.makeText(requireContext(), R.string.error_loading_note, Toast.LENGTH_LONG).show()
        noteIdView.visibility = View.GONE
        noteTextView.visibility = View.GONE
    }

    private fun renderNoteDeleteError() {
        Toast.makeText(requireContext(), R.string.error_deleting_note, Toast.LENGTH_LONG).show()
    }

    private fun renderNoteDeleted() {
        requireActivity().supportFragmentManager.popBackStack()
    }
}
