/*
* Copyright (C) 2018. WW International, Inc.
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

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.ww.roxiesample.R
import com.ww.roxiesample.domain.Note

typealias ClickListener = (Note) -> Unit

class NoteAdapter(private val clickListener: ClickListener) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private var notes = emptyList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemContainer = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false) as ViewGroup
        val viewHolder = ViewHolder(itemContainer)
        itemContainer.setOnClickListener { clickListener(notes[viewHolder.adapterPosition]) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        holder.noteId.text = note.id.toString()
        holder.noteText.text = note.text
    }

    override fun getItemCount() = notes.size

    fun updateNotes(notes: List<Note>) {
        val diffResult = DiffUtil.calculateDiff(ItemDiffCallback(this.notes, notes))
        this.notes = notes
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(itemViewGroup: ViewGroup) : RecyclerView.ViewHolder(itemViewGroup) {
        val noteId: TextView = itemViewGroup.findViewById(R.id.noteId)
        val noteText: TextView = itemViewGroup.findViewById(R.id.noteText)
    }
}