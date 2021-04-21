load("@io_bazel_rules_scala//scala:scala.bzl", "scala_binary", "scala_library")

scala_library(
    name = "App-src",
    srcs = glob(["src/main/scala/com/example/app/**/*.scala"]),
    deps = ["@maven//:joda_time_joda_time"],
)

scala_binary(
    name = "App",
    deps = [":App-src"],
    main_class = "com.example.app.Main",
)
