/*
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package io.bazel.kotlin.builder.utils

import io.bazel.kotlin.model.CompilationTaskInfo
import io.bazel.kotlin.model.JvmCompilationTask
import java.io.File

val JvmCompilationTask.Inputs.joinedClasspath: String get() = this.classpathList.joinToString(File.pathSeparator)

val CompilationTaskInfo.bazelRuleKind: String get() = "kt_${platform.name.lowercase()}_${ruleKind.name.lowercase()}"

fun Iterator<String>.partitionJvmSources(kt: (String) -> Unit, java: (String) -> Unit) {
    forEach {
        when {
            it.endsWith(".kt") -> kt(it)
            it.endsWith(".java") -> java(it)
            else -> throw IllegalStateException("invalid source file type $it")
        }
    }
}
