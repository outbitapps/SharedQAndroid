import Foundation
import OSLog
import SwiftUI

let logger: Logger = Logger(subsystem: "com.paytondeveloper.sharedqandroid", category: "SharedQAndroid")

/// The Android SDK number we are running against, or `nil` if not running on Android
let androidSDK = ProcessInfo.processInfo.environment["android.os.Build.VERSION.SDK_INT"].flatMap({ Int($0) })

/// The shared top-level view for the app, loaded from the platform-specific App delegates below.
///
/// The default implementation merely loads the `ContentView` for the app and logs a message.
public struct RootView : View {
    public init() {
        #if SKIP
        AMManager.shared
        #endif
    }

    public var body: some View {
        ContentView().environment(FIRManager(env: .superDev))
    }
}

#if !SKIP
public protocol SharedQAndroidApp : App {
}

/// The entry point to the SharedQAndroid app.
/// The concrete implementation is in the SharedQAndroidApp module.
public extension SharedQAndroidApp {
    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
#endif
