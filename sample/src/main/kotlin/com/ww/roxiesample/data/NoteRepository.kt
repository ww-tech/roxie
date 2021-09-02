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
package com.ww.roxiesample.data

import com.ww.roxiesample.domain.Note

/**
 * Normally we would implement a repository interface which is injected in the Domain layer
 */
object NoteRepository {
    private val notes = mutableListOf(
        Note(1, "note1"),
        Note(2, "note2"),
        Note(3, "note3")
    )

    suspend fun loadAll(): List<Note> = notes.toList()

    fun findById(id: Long): Note? = notes.firstOrNull { it.id == id }

    fun delete(note: Note): Boolean = notes.remove(note)
}