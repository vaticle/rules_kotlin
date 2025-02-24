# Copyright 2018 The Bazel Authors. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
workspace(name = "dev_io_bazel_rules_kotlin")

load("//kotlin:dependencies.bzl", "kt_download_local_dev_dependencies")

kt_download_local_dev_dependencies()

load("//kotlin:repositories.bzl", "kotlin_repositories", "versions")

kotlin_repositories()

register_toolchains("@dev_io_bazel_rules_kotlin//kotlin/internal:default_toolchain")

# Creates toolchain configuration for remote execution with BuildKite CI
# for rbe_ubuntu1604
load("@bazel_toolchains//rules:rbe_repo.bzl", "rbe_autoconfig")

rbe_autoconfig(
    name = "buildkite_config",
)

load(
    "@rules_android//android:rules.bzl",
    "android_ndk_repository",
    "android_sdk_repository",
)

android_sdk_repository(
    name = "androidsdk",
    build_tools_version = versions.ANDROID.BUILD_TOOLS,
)

android_ndk_repository(name = "androidndk")

[
    local_repository(
        name = version,
        path = "src/main/starlark/%s" % version,
        repo_mapping = {
            "@dev_io_bazel_rules_kotlin": "@",
        },
    )
    for version in versions.CORE
]
