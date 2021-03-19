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
package com.ww.roxiesample.domain

import com.ww.roxiesample.data.NoteRepository

class GetNoteDetailUseCase {
    suspend fun findById(id: Long): Note {
        // TODO: Is throwing the right thing to do below?
        return NoteRepository.findById(id) ?: throw IllegalArgumentException("Invalid note id passed in")
    }
}
