import SwiftUI
import Foundation

public struct ContentView: View {
    
    @AppStorage("needsOnboarding") var needsOnboarding = true
    @Environment(FIRManager.self) var firManager
    public init() {
    }

    public var body: some View {
        VStack {
            Text("sharedq \(firManager.currentUser?.username ?? "no user")")
        }.fullScreenCover(isPresented: $needsOnboarding, content: {
            OnboardingAuthView()
        })
    }
}

enum Tab : String, Hashable {
    case welcome, home, settings
}

#Preview {
    ContentView()
}
