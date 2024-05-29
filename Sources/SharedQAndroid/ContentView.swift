import SwiftUI

public struct ContentView: View {
    @AppStorage("tab") var tab = Tab.welcome
    @AppStorage("name") var name = "Skipper"
    @State var appearance = ""
    @State var isBeating = false

    public init() {
    }

    public var body: some View {
        OnboardingAuthView()
    }
}

enum Tab : String, Hashable {
    case welcome, home, settings
}

#Preview {
    ContentView()
}
