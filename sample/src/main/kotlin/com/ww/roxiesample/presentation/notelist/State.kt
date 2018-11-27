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

import com.ww.roxie.BaseState
import com.ww.roxiesample.domain.Note

data class State(val notes: List<Note> = listOf(),
                 val isIdle: Boolean = false,
                 val isLoading: Boolean = false,
                 val isError: Boolean = false) : BaseState