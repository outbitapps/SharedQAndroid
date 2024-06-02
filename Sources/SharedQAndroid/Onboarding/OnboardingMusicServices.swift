//
//  SwiftUIView.swift
//  
//
//  Created by Payton Curry on 5/30/24.
//

import SwiftUI

struct OnboardingMusicServices: View {
    @Binding var navPath: [String]
    var body: some View {
            List {
                HStack {
                    Text("Spotify")
                    Spacer()
//                    Image(systemName: "chevron.right")
                }.onTapGesture {
                    navPath.append("final-notes")
                }
                HStack {
                    Text("Apple Music")
                    Spacer()
//                    Image(systemName: "chevron.right")
                }.onTapGesture {
                    navPath.append("final-notes")
                }
            }.navigationTitle("Music Service")
    }
}

