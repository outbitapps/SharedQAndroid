import class Foundation.Bundle

extension Foundation.Bundle {
    static let module: Bundle = {
        let mainPath = Bundle.main.bundleURL.appendingPathComponent("sharedq-skip_SharedQAndroidTests.bundle").path
        let buildPath = "/Users/paytondev/Documents/sharedq-skip/.build/arm64-apple-macosx/debug/sharedq-skip_SharedQAndroidTests.bundle"

        let preferredBundle = Bundle(path: mainPath)

        guard let bundle = preferredBundle ?? Bundle(path: buildPath) else {
            fatalError("could not load resource bundle: from \(mainPath) or \(buildPath)")
        }

        return bundle
    }()
}