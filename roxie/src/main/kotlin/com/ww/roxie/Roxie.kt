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
package com.ww.roxie

object Roxie {

    private var logger: Logger? = null

    /**
     * This enables logging of Actions. If [DefaultLogger] is used, logging will be done using [println].
     */
    fun enableLogging(logger: Logger = DefaultLogger()) {
        this.logger = logger
    }

    internal fun log(msg: String) {
        logger?.log(msg)
    }

    interface Logger {
        fun log(msg: String)
    }

    private class DefaultLogger : Logger {
        override fun log(msg: String) {
            println(msg)
        }
    }
}
