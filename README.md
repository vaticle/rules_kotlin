[![Build Status](https://badge.buildkite.com/a8860e94a7378491ce8f50480e3605b49eb2558cfa851bbf9b.svg)](https://buildkite.com/bazel/kotlin-postsubmit)

# Bazel Kotlin Rules (with modifications by Vaticle)

Current release: ***`legacy-1.3.0-rc3`***<br />
Main branch: `master`

### Modifications by Vaticle

- We added support for Kotlin 1.5.
- We added the ability to pass a plugin file to the Kotlin compiler. `kotlin_jvm_library` and `kotlin_jvm_binary` have been enhanced with an additional optional parameter, `kotlin_compiler_plugin`. A single `File` should be passed to it (see example usage below).

#### Example usage

```python
kt_jvm_binary(
    name = "main",
    srcs = ["main.kt"],
    main_class = "com.vaticle.MainKt",
    kotlin_compiler_plugin = "@org_jetbrains_compose_compiler//file",
    deps = ["@maven//:org_jetbrains_compose_desktop_desktop_jvm_1_0_0_alpha3"],
)
```

# News!
* <b>Dec 6, 2019.</b> Released version [1.3.0-rc3](https://github.com/bazelbuild/rules_kotlin/releases/tag/legacy-1.3.0-rc3).
* <b>Dec 6, 2019.</b> Add support for later java versions as target platforms (#236).
* <b>Dec 5, 2019.</b> Released version [1.3.0-rc2](https://github.com/bazelbuild/rules_kotlin/releases/tag/legacy-1.3.0-rc2).
* <b>Dec 5, 2019.</b> Fix for problem with jdeps generation (#235).
* <b>Oct 29, 2019.</b> Released version [1.3.0-rc1](https://github.com/bazelbuild/rules_kotlin/releases/tag/legacy-1.3.0-rc1). 
* <b>Oct 5, 2019.</b> github.com/cgruber/rules_kotlin upstreamed into this repository. 

For older news, please see [Changelog](CHANGELOG.md)

# Overview 

**rules_kotlin** supports the basic paradigm of `*_binary`, `*_library`, `*_test` of other bazel 
language rules. It also supports `jvm`, `android`, and `js` flavors, with the prefix `kt_jvm`
and `kt_js`, and `kt_android` typically applied to the rules (the exception being 
`kt_android_local_test`, which doesn't exist. Use an `android_local_test` that takes a 
`kt_android_library` as a dependency).

Limited "friend" support is available, in the form of tests being friends of their library for the
system under test, allowing `internal` access to types and functions.

Also, jvm rules support the following standard java rules attributes:
  * `data`
  * `resource_jars`
  * `runtime_deps`
  * `resources`
  * `resources_strip_prefix`
  * `exports`
  
Android rules also support custom_package for `R.java` generation, `manifest=`, `resource_files`, etc.

Other features:
  * Persistent worker support.
  * Mixed-Mode compilation (compile Java and Kotlin in one pass).
  * Configurable Kotlinc distribtution and version
  * Configurable Toolchain
  * Kotlin 1.3 support
  
Javascript is reported to work, but is not as well maintained (at present)

# Quick Guide

## WORKSPACE
In the project's `WORKSPACE`, declare the external repository and initialize the toolchains, like
this:

```build
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

rules_kotlin_version = "legacy-1.3.0-rc3"
rules_kotlin_sha = "54678552125753d9fc0a37736d140f1d2e69778d3e52cf454df41a913b964ede"
http_archive(
    name = "io_bazel_rules_kotlin",
    urls = ["https://github.com/bazelbuild/rules_kotlin/archive/%s.zip" % rules_kotlin_version],
    type = "zip",
    strip_prefix = "rules_kotlin-%s" % rules_kotlin_version,
    sha256 = rules_kotlin_sha,
)

load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kotlin_repositories", "kt_register_toolchains")
kotlin_repositories() # if you want the default. Otherwise see custom kotlinc distribution below
kt_register_toolchains() # to use the default toolchain, otherwise see toolchains below
```

## BUILD files

In your project's `BUILD` files, load the kotlin rules and use them like so:

```
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_jvm_library")

kt_jvm_library(
    name = "package_name",
    srcs = glob(["*.kt"]),
    deps = [
        "//path/to/dependency",
    ],
)
```

## Custom toolchain

To enable a custom toolchain (to configure language level, etc.)
do the following.  In a `<workspace>/BUILD.bazel` file define the following:

```
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "define_kt_toolchain")

define_kt_toolchain(
    name = "kotlin_toolchain",
    api_version = KOTLIN_LANGUAGE_LEVEL,  # "1.1", "1.2", or "1.3"
    jvm_target = JAVA_LANGUAGE_LEVEL, # "1.6", "1.8", "9", "10", "11", or "12",
    language_version = KOTLIN_LANGUAGE_LEVEL,  # "1.1", "1.2", or "1.3"
)
```

and then in your `WORKSPACE` file, instead of `kt_register_toolchains()` do

```
register_toolchains("//:kotlin_toolchain")
```

## Custom kotlinc distribution (and version)

To choose a different kotlinc distribution (only 1.3 variants supported), do the following
in your `WORKSPACE` file (or import from a `.bzl` file:

```
load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kotlin_repositories")

KOTLIN_VERSION = "1.3.31"
KOTLINC_RELEASE_SHA = "107325d56315af4f59ff28db6837d03c2660088e3efeb7d4e41f3e01bb848d6a"

KOTLINC_RELEASE = {
    "urls": [
        "https://github.com/JetBrains/kotlin/releases/download/v{v}/kotlin-compiler-{v}.zip".format(v = KOTLIN_VERSION),
    ],
    "sha256": KOTLINC_RELEASE_SHA,
}

kotlin_repositories(compiler_release = KOTLINC_RELEASE)
```

## Third party dependencies 
_(e.g. maven artifacts)_

Third party (external) artifacts can be brought in with systems such as [rules_jvm_external](https://github.com/bazelbuild/rules_jvm_external) or [bazel_maven_repository](https://github.com/square/bazel_maven_repository) or [bazel-deps](https://github.com/johnynek/bazel-deps), but make sure the version you use doesn't naively use java_import, as this will cause bazel to make an interface-only (ijar), or ABI jar, and the native ijar tool does not know about kotlin metadata with respect to inlined functions, and will remove method bodies inappropriately.  Recent versions of [rules_jvm_external] and [bazel_maven_repository] are known to work with kotlin.

## Examples

Examples can be found in the [examples directory](https://github.com/bazelbuild/rules_kotlin/tree/master/examples), including usage with Android, Dagger, Node-JS, etc.

# History

These rules were initially forked from [pubref/rules_kotlin](http://github.com/pubref/rules_kotlin), and then re-forked from [bazelbuild/rules_kotlin](http://github.com/bazelbuild/rules_kotlin). They were merged back into this repository in October, 2019.

# License

This project is licensed under the [Apache 2.0 license](LICENSE), as are all contributions

# Contributing

See the [CONTRIBUTING](CONTRIBUTING.md) doc for information about how to contribute to
this project.

