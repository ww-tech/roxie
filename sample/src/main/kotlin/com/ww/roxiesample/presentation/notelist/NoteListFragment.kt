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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ww.roxiesample.R
import com.ww.roxiesample.databinding.NoteListBinding
import com.ww.roxiesample.domain.GetNoteListUseCase
import com.ww.roxiesample.domain.Note
import com.ww.roxiesample.presentation.notedetail.NoteDetailFragment

class NoteListFragment : Fragment() {

    private val clickListener: ClickListener = this::onNoteClicked

    private val recyclerViewAdapter = NoteAdapter(clickListener)

    companion object {
        fun newInstance() = NoteListFragment()
    }

    private lateinit var viewModel: NoteListViewModel

    private var _binding: NoteListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        // Normally ViewModelFactory should be injected here along with its UseCases injected into it
        viewModel = ViewModelProviders.of(this, NoteListViewModelFactory(null, GetNoteListUseCase()))
            .get(NoteListViewModel::class.java)

        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })

        viewModel.dispatch(Action.LoadNotes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderState(state: State) {
        with(state) {
            when {
                isLoading -> renderLoadingState()
                isError -> renderErrorState()
                else -> renderNotesState(notes)
            }
        }
    }

    private fun renderLoadingState() {
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun renderErrorState() {
        binding.loadingIndicator.visibility = View.GONE
        Toast.makeText(requireContext(), R.string.error_loading_notes, Toast.LENGTH_LONG).show()
    }

    private fun renderNotesState(notes: List<Note>) {
        binding.loadingIndicator.visibility = View.GONE
        recyclerViewAdapter.updateNotes(notes)
        binding.notesRecyclerView.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notesRecyclerView.adapter = recyclerViewAdapter
        binding.notesRecyclerView.setHasFixedSize(true)
    }

    private fun onNoteClicked(note: Note) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, NoteDetailFragment.newInstance(note.id))
            .addToBackStack(null)
            .commit()
    }
}
