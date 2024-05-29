// swift-tools-version: 5.9
// This is a Skip (https://skip.tools) package,
// containing a Swift Package Manager project
// that will use the Skip build plugin to transpile the
// Swift Package, Sources, and Tests into an
// Android Gradle Project with Kotlin sources and JUnit tests.
import PackageDescription

let package = Package(
    name: "sharedq-skip",
    defaultLocalization: "en",
    platforms: [.iOS(.v17), .macOS(.v14), .tvOS(.v17), .watchOS(.v10), .macCatalyst(.v17)],
    products: [
        .library(name: "SharedQAndroidApp", type: .dynamic, targets: ["SharedQAndroid"]),
    ],
    dependencies: [
        .package(url: "https://source.skip.tools/skip.git", from: "0.8.45"),
        .package(url: "https://source.skip.tools/skip-ui.git", from: "0.0.0"),
        .package(url: "git@github.com:paytontech/sharedqprotocol.git", branch: "main"),
        .package(url: "git@github.com:paytontech/sharedqsync.git", branch: "main"),
    ],
    targets: [
        .target(name: "SharedQAndroid", dependencies: [.product(name: "SkipUI", package: "skip-ui"), .product(name: "SharedQProtocol", package: "sharedqprotocol"), .product(name: "SharedQSync", package: "sharedqsync")], resources: [.process("Resources")], plugins: [.plugin(name: "skipstone", package: "skip")]),
        .testTarget(name: "SharedQAndroidTests", dependencies: ["SharedQAndroid", .product(name: "SkipTest", package: "skip")], resources: [.process("Resources")], plugins: [.plugin(name: "skipstone", package: "skip")]),
    ]
)

