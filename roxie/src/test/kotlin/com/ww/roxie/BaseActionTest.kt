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
package com.ww.roxie

import org.amshove.kluent.shouldNotBeEqualTo
import org.amshove.kluent.shouldNotContain
import org.junit.Test

class BaseActionTest {

    @Test
    fun `Given obfuscated action with data, when to string, then to string returns obfuscated string without data`() {
        // GIVEN
        val data = "test"

        // WHEN
        val result = ObfuscatedAction(data).toString()

        // THEN
        result shouldNotContain data
    }

    @Test
    fun `Given two obfuscated actions with different data, when to string, then actions have different obfuscated strings`() {
        // GIVEN
        val actionOne = ObfuscatedAction("test")
        val actionTwo = ObfuscatedAction("test2")

        // WHEN
        val resultOne = actionOne.toString()
        val resultTwo = actionTwo.toString()

        // THEN
        resultOne shouldNotBeEqualTo resultTwo
    }
}

private data class ObfuscatedAction(val data: String) : BaseAction {
    override fun toString(): String = obfuscatedString()
}
