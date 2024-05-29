import class Foundation.Bundle

extension Foundation.Bundle {
    static let module: Bundle = {
        let mainPath = Bundle.main.bundleURL.appendingPathComponent("skip-ui_SkipUI.bundle").path
        let buildPath = "/Users/paytondev/Documents/sharedq-skip/.build/arm64-apple-macosx/debug/skip-ui_SkipUI.bundle"

        let preferredBundle = Bundle(path: mainPath)

        guard let bundle = preferredBundle ?? Bundle(path: buildPath) else {
            fatalError("could not load resource bundle: from \(mainPath) or \(buildPath)")
        }

        return bundle
    }()
}